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
package org.zollty.framework.core.config.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.zollty.framework.core.config.ConfigTools;
import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.util.MvcUtils;

/**
 * @author zollty
 * @since 2014-5-26
 */
public class DefaultTextApplicationConfigImpl implements IApplicationConfig {

    public DefaultTextApplicationConfigImpl(Map<String, String> propsMap) {
        super();
        String scanPackage = propsMap.get("scan-package");
        if (scanPackage != null) {
            // 得到所有scan节点
            this.setPaths(MvcUtils.StringSplitUtil.split(scanPackage, ","));
        }
        else {
            this.setPaths(new String[0]);
        }
        this.setViewPath(propsMap.get("view-path"));
        this.setEncoding(propsMap.get("view-encoding"));

        String prefix = propsMap.get("no-intercept-prefix");
        String suffix = propsMap.get("no-intercept-suffix");
        if (MvcUtils.StringUtil.isNotEmpty(prefix)) {
            this.setExcludePrefixes(ConfigTools.parseExcludePrefix(prefix.toString()));
        }
        if (MvcUtils.StringUtil.isNotEmpty(suffix)) {
            this.setExcludeSuffixes(ConfigTools.parseExcludeSuffix(suffix.toString()));
        }
    }

    private String viewPath;
    private String encoding;
    private String errorPagePath;
    private String logLevel;
    
    /** 排除拦截 的前缀 */
    private Set<String> excludePrefixes = new HashSet<String>();
    
    /** 排除拦截 的后缀 */
    private Set<String> excludeSuffixes = new HashSet<String>();
    
    private String[] paths;

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public String getErrorPagePath() {
        return errorPagePath;
    }

    public void setErrorPagePath(String errorPagePath) {
        this.errorPagePath = errorPagePath;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public Set<String> getExcludeSuffixes() {
        return excludeSuffixes;
    }

    @Override
    public Set<String> getExcludePrefixes() {
        return excludePrefixes;
    }

    public void setExcludePrefixes(Set<String> excludePrefixes) {
        this.excludePrefixes = excludePrefixes;
    }

    public void setExcludeSuffixes(Set<String> excludeSuffixes) {
        this.excludeSuffixes = excludeSuffixes;
    }


}
