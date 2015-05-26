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
package org.zollty.framework.core.context.support;

import java.util.List;

import org.zollty.framework.core.config.IFileConfig;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.xml.XmlBeanReader;
import org.zollty.framework.util.ResourcContext;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-10-11
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    private Logger log;
    
    private long beginTimeMs;

    public ClassPathXmlApplicationContext(IFileConfig config) {
        super(config);
    }

    public ClassPathXmlApplicationContext(IFileConfig config, ClassLoader beanClassLoader) {
        super(config, beanClassLoader);
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
        if (LogFactory.isDebugEnabled()) {
            log.debug("{} completed in {} ms.", getClass().getSimpleName(), (System.currentTimeMillis() - beginTimeMs));
        }
    }

    @Override
    protected List<BeanDefinition> loadBeanDefinitions() {
        IFileConfig config = (IFileConfig) getConfig();
        ResourcContext resourcContext = new ResourcContext(config.getConfigLocation(), config.getClassLoader());
        List<BeanDefinition> list = new XmlBeanReader(resourcContext).loadBeanDefinitions();
        if (list != null) {
            log.debug("-- xml bean --size = {}", list.size());
            return list;
        }
        return null;
    }

    @Override
    protected void doAfterClose() {
    }

}
