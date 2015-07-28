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
 * Create by ZollTy on 2013-9-20 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core;

/**
 * @author zollty
 * @since 2013-9-20
 */
public class Const {

    /**
     * Default config file (xml) location.
     */
    public static final String DEFAULT_CONFIG_LOCATION_XML = "classpath:zollty-mvc.xml";

    /**
     * Default properties config file location.
     */
    public static final String DEFAULT_CONFIG_LOCATION_PROP = "classpath:zollty-mvc.properties";
    
    public static final int DEFAULT_BEFORE_AOP_MAPPING_ORDER = 100;
    
    /**
     * Controller Method 的参数类型
     */
    public static enum ControllerMethodParamType {
        /** HttpParam注解的 基础数据类型（int、boolean等） */
        HttpParamBasic,
        /** HttpParam标注 Bean数据类型（标准POJO对象） */
        HttpParamBean,
        /** HttpServletRequest */
        HttpServletRequest,
        /** HttpServletResponse */
        HttpServletResponse,
        /** URIParam注解的 URI参数 */
        URIParam;
    }
    
//    /**
//     * Controller Method 的参数类型
//     */
//    public static interface CtrollerMethodParamType {
//        /** HttpParam注解的 基础数据类型（int、boolean等） */
//        char HttpParamBasic = 0;
//        /** HttpParam标注 Bean数据类型（标准POJO对象） */
//        char HttpParamBean = 1;
//        /** HttpServletRequest */
//        char HttpServletRequest = 2;
//        /** HttpServletResponse */
//        char HttpServletResponse = 3;
//        /** URIParam注解的 URI参数 */
//        char URIParam = 4;
//    }
    
}