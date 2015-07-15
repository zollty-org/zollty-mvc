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
 * Create by ZollTy on 2014-5-21 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.config.impl;

import org.zollty.framework.core.config.IFileConfig;
import org.zollty.framework.util.MvcUtils;

/**
 * 
 * @author zollty
 * @since 2014-5-21
 */
public abstract class AbstractFileConfig extends AbstractApplicationConfig implements IFileConfig {

    /** ClassLoader to resolve bean class names with, if necessary */
    private ClassLoader classLoader;

    private String configLocation;

    public AbstractFileConfig(String configLocation) {
        this.configLocation = configLocation;
        this.classLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
    }

    public AbstractFileConfig(String configLocation, ClassLoader classLoader) {
        this.configLocation = configLocation;
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

}