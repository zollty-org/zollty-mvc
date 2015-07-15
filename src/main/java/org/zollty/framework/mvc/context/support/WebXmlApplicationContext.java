/* 
 * Copyright (C) 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by ZollTy on 2013-10-11 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.context.support;

import java.util.List;

import javax.servlet.ServletContext;

import org.zollty.framework.core.config.IServletContextFileConfig;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.xml.XmlBeanReader;
import org.zollty.framework.mvc.handler.HttpServletDispatcherHandler;
import org.zollty.framework.util.ResourceContext;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-10-11
 */
public class WebXmlApplicationContext extends AbstractWebApplicationContext {

    private Logger log;

    public WebXmlApplicationContext(IServletContextFileConfig config) {
        super(config);
    }

    public WebXmlApplicationContext(IServletContextFileConfig config, ClassLoader beanClassLoader) {
        super(config, beanClassLoader);
    }

    public WebXmlApplicationContext(IServletContextFileConfig config, ClassLoader beanClassLoader,
            ServletContext servletContext) {
        super(config, beanClassLoader, servletContext);
    }

    private long beginTimeMs;

    @Override
    protected void doBeforeRefresh() {
        beginTimeMs = System.currentTimeMillis();
        log = LogFactory.getLogger(getClass());
        log.debug("load {} ...", getClass().getSimpleName());
    }

    @Override
    protected void doAfterRefresh() {
        handlerMapping = new HttpServletDispatcherHandler(beanDefinitions, getConfig());

        if( log.isDebugEnabled() )
        log.debug("{} completed in {} ms.", getClass().getSimpleName(),
                (System.currentTimeMillis() - beginTimeMs));
    }

    @Override
    protected List<BeanDefinition> loadBeanDefinitions() {
        IServletContextFileConfig config = (IServletContextFileConfig) super.getConfig();
        ResourceContext beanXmlResourceContext = new ResourceContext(config.getClassLoader(),
                config.getServletContext(), config.getConfigLocation());
        List<BeanDefinition> list = new XmlBeanReader(beanXmlResourceContext).loadBeanDefinitions();
        if (list != null) {
            log.debug("-- WebXml bean --size = {}", list.size());
            return list;
        }
        return null;
    }

    @Override
    protected void doAfterClose() {
        handlerMapping = null;
    }

}