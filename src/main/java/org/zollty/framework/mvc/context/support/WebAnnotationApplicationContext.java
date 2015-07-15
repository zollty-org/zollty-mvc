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
import org.zollty.framework.mvc.handler.HttpServletDispatcherHandler;
import org.zollty.framework.mvc.support.WebAnnotationBeanReader;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-10-11
 */
public class WebAnnotationApplicationContext extends AbstractWebApplicationContext {

    private Logger log;

    private long beginTimeMs;

    public WebAnnotationApplicationContext(IServletContextFileConfig config) {
        super(config);
    }

    public WebAnnotationApplicationContext(IServletContextFileConfig config,
            ClassLoader beanClassLoader) {
        super(config, beanClassLoader);
    }

    public WebAnnotationApplicationContext(IServletContextFileConfig config,
            ClassLoader beanClassLoader, ServletContext servletContext) {
        super(config, beanClassLoader, servletContext);
    }

    @Override
    protected void doBeforeRefresh() {
        beginTimeMs = System.currentTimeMillis();
        log = LogFactory.getLogger(getClass());
        log.debug("load {} ...", getClass().getSimpleName());
    }

    @Override
    protected void doAfterRefresh() {

        new BeanAopAnnotationParser(beanDefinitions);

        handlerMapping = new HttpServletDispatcherHandler(beanDefinitions, getConfig());

        if( log.isDebugEnabled() )
        log.debug("{} completed in {} ms.", getClass().getSimpleName(),
                (System.currentTimeMillis() - beginTimeMs));
    }

    @Override
    protected List<BeanDefinition> loadBeanDefinitions() {
        IServletContextFileConfig config = (IServletContextFileConfig) getConfig();

        List<BeanDefinition> list1 = new WebAnnotationBeanReader(config.getScanningPackages(),
                getBeanClassLoader(), null).loadBeanDefinitions();
        if (list1 != null) {
            log.debug("-- WebAnnotation bean -- size = {}", list1.size());
            return list1;
        }
        return null;
    }

    @Override
    protected void doAfterClose() {
        handlerMapping = null;
    }
}