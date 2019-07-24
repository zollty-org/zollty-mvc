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
package org.zollty.framework.core.config;

import java.util.Set;

/**
 * 
 * @author zollty
 * @since 2014-5-21
 */
public interface IApplicationConfig {
    
    /**
     * Default config file (xml) location.
     */
    String DEFAULT_CONFIG_LOCATION_XML = "classpath:zollty-mvc.xml";

    /**
     * Default properties config file location.
     */
    String DEFAULT_CONFIG_LOCATION_PROP = "classpath:zollty-mvc.properties";

    String DEFAULT_VIEW_PATH = "/WEB-INF/views";
    String DEFAULT_VIEW_ENCODING = "UTF-8";

    // List<String> defaultExcludeSuffixes = Arrays.asList(new
    // String[]{".css",".jpg",".png",".gif",".js",".html"});

    /**
     * @return the view basic path (relative to webroot path)
     */
    String getViewPath();

    /**
     * @return the global charactor encoding (view, resquest, response, and so on)
     */
    String getEncoding();

    /**
     * @return the class scanning packages
     */
    String[] getScanningPackages();

    /**
     * @return the view excludeSuffix
     */
    Set<String> getExcludeSuffixes();

    /**
     * @return the view excludeprefix
     */
    Set<String> getExcludePrefixes();

    /**
     * @return the special error page full path (relative to webroot path)
     */
    String getErrorPagePath();
    
    Set<String> getBeforeRefreshInterceptors();
    
    Set<String> getAfterCloseInterceptors();

}