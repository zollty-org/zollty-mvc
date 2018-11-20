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

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.zollty.framework.core.beans.annotation.AnnotationBeanDefinition;
import org.zollty.framework.core.beans.xml.XmlBeanDefinition;
import org.zollty.framework.core.beans.xml.XmlBeanReader;
import org.zollty.framework.core.config.IServletContextFileConfig;
import org.zollty.framework.mvc.handler.support.HttpRequestHandlerMapping;
import org.zollty.framework.util.ResourceContext;

/**
 * @author zollty
 * @since 2013-10-11
 */
public class DefaultWebApplicationContext extends AbstractWebApplicationContext {

    private Logger log;

    private long beginTimeMs;
    
    private List<AnnotationBeanDefinition> beanDefListTmp;

    public DefaultWebApplicationContext(IServletContextFileConfig config) {
        super(config);
    }

    public DefaultWebApplicationContext(IServletContextFileConfig config,
            ClassLoader beanClassLoader) {
        super(config, beanClassLoader);
    }

    public DefaultWebApplicationContext(IServletContextFileConfig config,
            ClassLoader beanClassLoader, ServletContext servletContext) {
        super(config, beanClassLoader, servletContext);
    }

    @Override
    protected void doBeforeRefresh() {
        beginTimeMs = System.currentTimeMillis();
        log = LogFactory.getLogger(getClass());
        log.debug("load {} ...", getClass().getSimpleName());
        super.doBeforeRefresh();
    }

    @Override
    protected List<XmlBeanDefinition> loadXmlBeanDefinitions() {
        IServletContextFileConfig config = (IServletContextFileConfig) getConfig();

        ResourceContext resourcContext = new ResourceContext(config.getClassLoader(),
                config.getServletContext(), config.getConfigLocation());
        List<XmlBeanDefinition> list = new XmlBeanReader(resourcContext).loadBeanDefinitions();
        if (list != null) {
            log.debug("-- Xml beans -- size = {}", list.size());
        }
        return list;
    }

    @Override
    protected List<AnnotationBeanDefinition> loadAnnoBeanDefinitions() {
        beanDefListTmp = new WebAnnotationBeanReader(getConfig().getScanningPackages(),
                getBeanClassLoader(), null).loadBeanDefinitions();
        if (beanDefListTmp != null) {
            log.debug("-- WebAnnotation beans -- size = {}", beanDefListTmp.size());
        }
        return beanDefListTmp;
    }
    
    @Override
    protected void doAfterRefresh() {
        
        initController();
        
        if (log.isDebugEnabled()) {
            log.debug("{} completed in {} ms.", getClass().getSimpleName(),
                    (System.currentTimeMillis() - beginTimeMs));
        }
        
        super.doAfterRefresh();
    }
    
    protected void initController() {
        // 解析Controller中的AOP定义
        new ControllerAopAnnotationParser(beanDefListTmp);
        // 解析Controller，绑定URL
        handlerMapping = new HttpRequestHandlerMapping(beanDefListTmp, getConfig());
        // 没有用了，释放内存
        beanDefListTmp = null;
    }

    @Override
    protected void doAfterClose() {
        handlerMapping = null;
        log = null;
        super.doAfterClose();
    }
    
}