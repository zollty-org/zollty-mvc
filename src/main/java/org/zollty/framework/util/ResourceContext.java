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
 * Create by ZollTy on 2015-5-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.util;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

/**
 * Resource相关的 “上下文”(背景/环境/语境)
 * @author zollty
 * @since 2015-5-15
 */
public class ResourceContext {

    private final List<String> locations;

    private final ClassLoader classLoader;

    private final ServletContext servletContext;

    /**
     * @param classLoader 指定解析resource所使用的ClassLoader
     * @param locations resources的路径
     */
    public ResourceContext(ClassLoader classLoader, String... locations) {
        this(classLoader, null, locations);
    }

    /**
     * @param classLoader 指定解析resource所使用的ClassLoader，若为空，则使用{@code MvcUtils.ClassUtil.getDefaultClassLoader()}
     * @param servletContext 指定解析Servlet based resource所使用的ServletContext
     * @param locations resources的路径
     */
    public ResourceContext(ClassLoader classLoader, ServletContext servletContext, String... locations) {
        this.locations = (locations != null) ? Arrays.asList(locations) : null;
        this.classLoader = (classLoader != null ? classLoader : MvcUtils.ClassUtil.getDefaultClassLoader());
        this.servletContext = servletContext;
    }

    /**
     * 获取单一resource路径，如果有多个locations则取List的第一个
     */
    public String getLocation() {
        return locations != null ? locations.get(0) : null;
    }

    /**
     * 解析resources所使用的ClassLoader
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * 解析Servlet based resource所使用的ServletContext
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * resources的路径
     */
    public List<String> getLocations() {
        return locations;
    }

}
