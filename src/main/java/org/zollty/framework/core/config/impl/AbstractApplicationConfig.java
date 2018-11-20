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

import java.util.HashSet;
import java.util.Set;

import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.core.config.InitByConfig;

/**
 * 
 * @author zollty
 * @since 2014-5-21
 */
public class AbstractApplicationConfig implements IApplicationConfig {

    private String viewPath;
    private String encoding;
    private String errorPagePath;

    /** 排除拦截 的前缀 */
    private Set<String> excludePrefixes = new HashSet<String>();

    /** 排除拦截 的后缀 */
    private Set<String> excludeSuffixes = new HashSet<String>();

    private String[] scanningPackages;
    
    private Set<String> beforeRefreshInterceptors;
    
    private Set<String> afterCloseInterceptors;

    @Override
    public String getViewPath() {
        if (viewPath == null) {
            setViewPath(DEFAULT_VIEW_PATH);
        }
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    @Override
    public String getEncoding() {
        if (encoding == null) {
            setEncoding(DEFAULT_VIEW_ENCODING);
        }
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String[] getScanningPackages() {
        return scanningPackages;
    }

    public void setScanningPackages(String[] scanningPackages) {
        this.scanningPackages = scanningPackages;
    }

    @Override
    public String getErrorPagePath() {
        return errorPagePath;
    }

    public void setErrorPagePath(String errorPagePath) {
        this.errorPagePath = errorPagePath;
    }

    public void setInitLogger(String logName, String logLevel) {
        InitByConfig.initLogFactory(logName, logLevel);
    }

    @Override
    public Set<String> getExcludeSuffixes() {
        return excludeSuffixes;
    }

    public void setExcludeSuffixes(Set<String> excludeSuffixes) {
        this.excludeSuffixes = excludeSuffixes;
    }

    @Override
    public Set<String> getExcludePrefixes() {
        return excludePrefixes;
    }

    public void setExcludePrefixes(Set<String> excludePrefixes) {
        this.excludePrefixes = excludePrefixes;
    }

    @Override
    public Set<String> getBeforeRefreshInterceptors() {
        return beforeRefreshInterceptors;
    }

    public void setBeforeRefreshInterceptors(Set<String> beforeRefreshInterceptors) {
        this.beforeRefreshInterceptors = beforeRefreshInterceptors;
    }

    @Override
    public Set<String> getAfterCloseInterceptors() {
        return afterCloseInterceptors;
    }

    public void setAfterCloseInterceptors(Set<String> afterCloseInterceptors) {
        this.afterCloseInterceptors = afterCloseInterceptors;
    }

}