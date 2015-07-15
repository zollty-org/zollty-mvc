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

import org.zollty.framework.core.beans.support.AbstractBeanFactory;
import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.core.context.ApplicationContext;

/**
 * @author zollty
 * @since 2013-10-11
 */
public abstract class AbstractApplicationContext extends AbstractBeanFactory implements
        ApplicationContext {

    private IApplicationConfig config;

    public AbstractApplicationContext(IApplicationConfig config) {
        super();

        // 配置好了，才能刷新
        this.config = config;

        refresh();
    }

    public AbstractApplicationContext(IApplicationConfig config, ClassLoader beanClassLoader) {
        super(beanClassLoader);

        // 配置好了，才能刷新
        this.config = config;

        refresh();
    }

    public IApplicationConfig getConfig() {
        return config;
    }

    public void setConfig(IApplicationConfig config) {
        this.config = config;
    }

}