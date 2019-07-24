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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import org.jretty.util.NestedRuntimeException;
import org.zollty.framework.util.MvcUtils;

/**
 * TextConfig设计的初衷，就是想免去xml的繁琐配置，故TextConfig只支持注解形式的bean，不支持xml形式的bean定义
 * 
 * @author zollty
 * @since 2014-5-21
 */
public abstract class AbstractTextConfig extends AbstractFileConfig {

    public AbstractTextConfig() {
        super(DEFAULT_CONFIG_LOCATION_PROP);
    }

    public AbstractTextConfig(String configLocation) {
        super(configLocation, null);
    }

    public AbstractTextConfig(String configLocation, ClassLoader classLoader) {
        super(configLocation, classLoader);
    }

    public abstract InputStream getResourceInputStream() throws IOException;

    protected void loadConfig() {

        String configPath = getConfigLocation();
        if (configPath == null || !configPath.endsWith(".properties")) {
            throw new IllegalArgumentException(
                    "config location assume be a *.properties file but get: " + configPath);
        }

        InputStream in = null;
        try {
            in = getResourceInputStream();
        }
        catch (IOException e) {
            MvcUtils.IOUtil.closeIO(in);
            throw new NestedRuntimeException(e);
        }

        Properties props = MvcUtils.ResourceUtil.getProperties(in);
        Map<String, String> propsMap = MvcUtils.CollectionUtil.covertPropertiesToMap(props);
        
        // 优先解析日志配置
        setLogger(propsMap.get("jretty-logger"));

        String scanPackage = propsMap.get("scan-package");
        if (scanPackage != null) {
            // 得到所有scan节点
            this.setScanningPackages(MvcUtils.StringSplitUtil.splitIgnoreEmpty(scanPackage, ','));
        }
        else {
            this.setScanningPackages(new String[0]);
        }
        this.setViewPath(propsMap.get("view-path"));
        this.setEncoding(propsMap.get("view-encoding"));
        
        this.setErrorPagePath(propsMap.get("error-page"));
        
        this.setErrorPagePath(propsMap.get("error-page"));
        
        String prefix = propsMap.get("no-intercept-prefix");
        String suffix = propsMap.get("no-intercept-suffix");
        if (MvcUtils.StringUtil.isNotEmpty(prefix)) {
            this.setExcludePrefixes(ConfigTools.parseExcludePrefix(prefix.toString()));
        }
        if (MvcUtils.StringUtil.isNotEmpty(suffix)) {
            this.setExcludeSuffixes(ConfigTools.parseExcludeSuffix(suffix.toString()));
        }
        
        handleBeforeRefreshInterceptors(propsMap.get("before-refresh"));
        handleAfterCloseInterceptors(propsMap.get("after-close"));
    }
    
    protected void handleBeforeRefreshInterceptors(String val) {
        if (MvcUtils.StringUtil.isNotEmpty(val)) {
            String[] incepts = MvcUtils.StringSplitUtil.split(val, ',');
            this.setBeforeRefreshInterceptors(new HashSet<String>(Arrays.asList(incepts)));
        }
    }
    
    protected void handleAfterCloseInterceptors(String val) {
        if (MvcUtils.StringUtil.isNotEmpty(val)) {
            String[] incepts = MvcUtils.StringSplitUtil.split(val, ',');
            this.setAfterCloseInterceptors(new HashSet<String>(Arrays.asList(incepts)));
        }
    }
    
    protected void setLogger(String loggerVal) {
        if (MvcUtils.StringUtil.isNotEmpty(loggerVal)) {
            String[] tmp = MvcUtils.StringSplitUtil.split(loggerVal, ':');
            String logName = tmp[0];
            String level = tmp[1];
            this.setInitLogger(logName, level);
        }
    }

}