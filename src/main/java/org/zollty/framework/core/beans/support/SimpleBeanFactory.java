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
 * Create by Zollty Tsou (Contact: zollty@163.com, http://blog.zollty.com)
 */
package org.zollty.framework.core.beans.support;

import java.util.List;

import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.BeanReader;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-12-7
 */
public class SimpleBeanFactory extends AbstractBeanFactory {

    private Logger log;
    
    private long beginTimeMs;
    
    private BeanReader beanReader;
    
    public SimpleBeanFactory(BeanReader beanReader) {
        super();
        this.beanReader = beanReader;
        
        refresh();
    }

    public SimpleBeanFactory(BeanReader beanReader, ClassLoader beanClassLoader) {
        super(beanClassLoader);
        this.beanReader = beanReader;
        
        refresh();
    }

    @Override
    protected void doBeforeRefresh() {
        beginTimeMs = System.currentTimeMillis();
        log = LogFactory.getLogger(getClass());
        if (LogFactory.isDebugEnabled()) {
            log.debug("load {} start...", getClass().getSimpleName());
        }
    }

    @Override
    protected void doAfterRefresh() {
        if (LogFactory.isDebugEnabled()) {
            log.debug("{} completed in {} ms.", getClass().getSimpleName(), System.currentTimeMillis() - beginTimeMs);
        }
    }

    @Override
    protected List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> list = beanReader.loadBeanDefinitions();
        if (list != null) {
            log.trace("beans type = [{}] size = {}", beanReader.getClass().getName(), list.size());
            return list;
        }
        return null;
    }

    @Override
    protected void doAfterClose() {
    }

    public BeanReader getBeanReader() {
        return beanReader;
    }

    public void setBeanReader(BeanReader beanReader) {
        this.beanReader = beanReader;
    }

}
