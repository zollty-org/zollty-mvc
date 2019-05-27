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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.zollty.framework.core.config.IServletContextFileConfig;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.Dom;

/**
 * 
 * @author zollty
 * @since 2014-5-21
 */
public class DefaultXmlConfig extends AbstractXmlConfig implements IServletContextFileConfig {

    private ServletContext servletContext;

    public DefaultXmlConfig() {
        super();
        loadConfig();
    }

    public DefaultXmlConfig(String configLocation, ClassLoader classLoader) {
        super(configLocation, classLoader);
        loadConfig();
    }

    public DefaultXmlConfig(String configLocation, ClassLoader classLoader, Dom dom) {
        super(configLocation, classLoader, dom);
        loadConfig();
    }

    public DefaultXmlConfig(String configLocation, Dom dom) {
        super(configLocation, dom);
        loadConfig();
    }

    public DefaultXmlConfig(String configLocation) {
        super(configLocation);
        loadConfig();
    }

    public DefaultXmlConfig(String configLocation, ServletContext servletContext) {
        super(configLocation);
        this.servletContext = servletContext;
        loadConfig();
    }

    public DefaultXmlConfig(String configLocation, ClassLoader classLoader, Dom dom,
            ServletContext servletContext) {
        super(configLocation, classLoader, dom);
        this.servletContext = servletContext;
        loadConfig();
    }

    @Override
    public InputStream getResourceInputStream() throws IOException {
        return MvcUtils.ResourceUtil.getResourceInputStream(getConfigLocation(), getClassLoader(),
                getServletContext());
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

}