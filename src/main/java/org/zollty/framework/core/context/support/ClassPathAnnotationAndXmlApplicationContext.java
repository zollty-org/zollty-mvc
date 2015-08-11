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
package org.zollty.framework.core.context.support;

import java.util.List;

import org.zollty.framework.core.beans.BeanDefinition;
import org.zollty.framework.core.beans.annotation.AnnotationBeanReader;
import org.zollty.framework.core.beans.xml.XmlBeanReader;
import org.zollty.framework.core.config.IFileConfig;
import org.zollty.framework.util.ResourceContext;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-10-11
 */
public class ClassPathAnnotationAndXmlApplicationContext extends AbstractApplicationContext {

    private Logger log;

    private long beginTimeMs;

    public ClassPathAnnotationAndXmlApplicationContext(IFileConfig config) {
        super(config);
    }

    public ClassPathAnnotationAndXmlApplicationContext(IFileConfig config,
            ClassLoader beanClassLoader) {
        super(config, beanClassLoader);
    }

    @Override
    protected void doBeforeRefresh() {
        beginTimeMs = System.currentTimeMillis();
        log = LogFactory.getLogger(getClass());
        if (log.isDebugEnabled()) {
            log.debug("load {} ...", getClass().getSimpleName());
        }
    }

    @Override
    protected void doAfterRefresh() {
        if (log.isDebugEnabled()) {
            log.debug("{} completed in {} ms.", getClass().getSimpleName(),
                    System.currentTimeMillis() - beginTimeMs);
        }
    }

    @Override
    protected List<BeanDefinition> loadBeanDefinitions() {
        IFileConfig config = (IFileConfig) getConfig();
        List<BeanDefinition> list1 = new AnnotationBeanReader(config.getScanningPackages(),
                getBeanClassLoader(), null).loadBeanDefinitions();

        ResourceContext resourcContext = new ResourceContext(config.getClassLoader(),
                config.getConfigLocation());
        List<BeanDefinition> list2 = new XmlBeanReader(resourcContext).loadBeanDefinitions();
        if (list1 != null && list2 != null) {
            list1.addAll(list2);
            log.debug(" [-- AnnotationBean & XmlBean --] size = {}", list1.size());
            return list1;
        }
        else if (list1 != null) {
            log.debug(" [-- Annotation beans --] size = {}", list1.size());
            return list1;
        }
        else if (list2 != null) {
            log.debug(" [-- Xml beans --] size = {}", list2.size());
            return list2;
        }
        return null;
    }

    @Override
    protected void doAfterClose() {
    }

}