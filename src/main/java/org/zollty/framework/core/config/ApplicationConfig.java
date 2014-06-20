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
package org.zollty.framework.core.config;

import java.util.HashSet;
import java.util.Set;

import org.zollty.framework.util.MvcUtils;


/**
 * @author zollty
 * @since 2014-5-22
 */
public class ApplicationConfig implements IApplicationConfig {
    
    private IApplicationConfig config;
    
    private static final String defaultViewPath;
    private static final String defaultViewEncoding;
    /** 排除拦截 的后缀 */
    private static Set<String> defaultExcludeSuffixes = new HashSet<String>();
    
    static {
        defaultViewPath = "/WEB-INF/views";
        defaultViewEncoding = "UTF-8";
        //String[] ary  = new String[]{".css",".jpg",".png",".gif",".js",".html"};
        //defaultExcludeSuffixes.addAll(Arrays.asList(ary));
    }

    private String viewPath = defaultViewPath;;
    private String encoding  = defaultViewEncoding;
    private String errorPagePath;
    private String logLevel;
    private String[] paths;
    /** 排除拦截 的前缀 */
    private Set<String> excludePrefixes = new HashSet<String>();
    /** 排除拦截 的后缀 */
    private Set<String> excludeSuffixes = defaultExcludeSuffixes;
    
    public ApplicationConfig(IApplicationConfig config){
        this.config = config;
        init();
    }
    
    private void init(){
        if(MvcUtils.StringUtil.isNotBlank(config.getViewPath())){
            this.viewPath = config.getViewPath();
        }
        if(MvcUtils.StringUtil.isNotBlank(config.getEncoding())){
            this.encoding = config.getEncoding();
        }
        if(MvcUtils.StringUtil.isNotBlank(config.getErrorPagePath())){
            this.errorPagePath = config.getErrorPagePath();
        }
        if(MvcUtils.StringUtil.isNotBlank(config.getLogLevel())){
            this.logLevel = config.getLogLevel();
        }
        if( config.getPaths()!=null && config.getPaths().length!=0 ){
            this.paths = config.getPaths();
        }
        if( config.getExcludePrefixes()!=null && !config.getExcludePrefixes().isEmpty() ){
            this.excludePrefixes = config.getExcludePrefixes();
        }
        if( config.getExcludeSuffixes()!=null && !config.getExcludeSuffixes().isEmpty() ){
            this.excludeSuffixes = config.getExcludeSuffixes();
        }
    }
    

    public String getViewPath() {
        return viewPath;
    }

    public String getEncoding() {
        return encoding;
    }

    public String[] getPaths() {
        return paths;
    }
    
    public String getErrorPagePath() {
        return errorPagePath;
    }

    public String getLogLevel() {
        return logLevel;
    }

    @Override
    public Set<String> getExcludeSuffixes() {
        return excludeSuffixes;
    }

    @Override
    public Set<String> getExcludePrefixes() {
        return excludePrefixes;
    }

}