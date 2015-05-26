/* 
 * Copyright (C) 2012-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by Zollty Tsou [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.context.support;

import java.util.List;

import javax.servlet.ServletContext;

import org.zollty.framework.core.config.IServletContextFileConfig;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.xml.XmlBeanReader;
import org.zollty.framework.mvc.handler.HttpServletDispatcherHandler;
import org.zollty.framework.mvc.support.WebAnnotationBeanReader;
import org.zollty.framework.util.ResourcContext;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-10-11
 */
public class WebAnnotationAndXmlApplicationContext extends AbstractWebApplicationContext {

    private Logger log;

    private long beginTimeMs;
    
    public WebAnnotationAndXmlApplicationContext(IServletContextFileConfig config) {
        super(config);
    }

    public WebAnnotationAndXmlApplicationContext(IServletContextFileConfig config, ClassLoader beanClassLoader) {
        super(config, beanClassLoader);
    }

    public WebAnnotationAndXmlApplicationContext(IServletContextFileConfig config, ClassLoader beanClassLoader,
            ServletContext servletContext) {
        super(config, beanClassLoader, servletContext);
    }

    @Override
    protected void doBeforeRefresh() {
        beginTimeMs = System.currentTimeMillis();
        log = LogFactory.getLogger(getClass());
        if (LogFactory.isDebugEnabled()) {
            log.debug("load {} ...", getClass().getSimpleName());
        }
    }
    

    @Override
    protected void doAfterRefresh() {

        new BeanAopAnnotationParser(beanDefinitions);

        handlerMapping = new HttpServletDispatcherHandler(beanDefinitions, getConfig());

        if (LogFactory.isDebugEnabled()) {
            log.debug("{} completed in {} ms.", getClass().getSimpleName(), (System.currentTimeMillis() - beginTimeMs));
        }
    }
    

    @Override
    protected List<BeanDefinition> loadBeanDefinitions() {
        IServletContextFileConfig config = (IServletContextFileConfig) getConfig();
        
        List<BeanDefinition> list1 = new WebAnnotationBeanReader(
                config.getScanningPackages(), getBeanClassLoader(), null).loadBeanDefinitions(); 
        
        ResourcContext resourcContext = new ResourcContext(config.getConfigLocation(), config.getClassLoader(), config.getServletContext());
        List<BeanDefinition> list2 = new XmlBeanReader(resourcContext).loadBeanDefinitions();
        
        if (list1 != null && list2 != null) {
            list1.addAll(list2);
            log.debug("mixed bean -- WebAnnotationBean & XmlBean -- size = {}", list1.size());
            return list1;
        }
        else if (list1 != null) {
            log.debug("-- WebAnnotation bean -- size = {}", list1.size());
            return list1;
        }
        else if (list2 != null) {
            log.debug("-- xml bean -- size = {}", list2.size());
            return list2;
        }
        return null;
    }

    @Override
    protected void doAfterClose() {
        handlerMapping = null;
    }
}
