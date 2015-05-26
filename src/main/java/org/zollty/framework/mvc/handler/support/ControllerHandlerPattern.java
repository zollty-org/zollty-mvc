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
 * Zollty Framework MVC Source Code - Since v1.1
 * Author(s): 
 * Zollty Tsou (zolltytsou@gmail.com, http://blog.zollty.com)
 */
package org.zollty.framework.mvc.handler.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.support.BasicParamMetaInfo;
import org.zollty.framework.mvc.support.ControllerMetaInfo;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.match.ZolltyPathMatcher;

/**
 * @author zollty
 * @since 2014-5-29
 */
public class ControllerHandlerPattern {

    private static Logger LOG = LogFactory.getLogger(ControllerHandlerPattern.class);

    private final ControllerMetaInfo controller;
    private ZolltyPathMatcher pattern;
    private final List<String> paramsName;
    private final String patternStr;

    /**
     * @param controller
     * @param paramsName
     */
    public ControllerHandlerPattern(ControllerMetaInfo controller, List<String> paramsName) {
        this.controller = controller;
        this.paramsName = paramsName;
        
        // Check Params
        byte[] paramType = controller.getParamType();
        BasicParamMetaInfo[] bParamMetas = controller.getbParamMetas();
        for (int i = 0; i < paramType.length; i++) {
            if(paramType[i]==BasicParamMetaInfo.URI_PARAM) {
                BasicParamMetaInfo pb = bParamMetas[i];
                int pos = Arrays.binarySearch(paramsName.toArray(new String[paramsName.size()]), pb.getAttribute());
                // 如果参数的名称是在 URI 参数列表中，则OK，否则报错。
                if (pos < 0) {
                    throw new IllegalArgumentException(
                            "URIParam definition error, can't find the variable '"+pb.getAttribute()+"' in URI param(such as /{v1}{v2}/).");
                }
            }
            
        }
        
        
        String pstr = controller.getServletURI();
        for (String str : paramsName) {
            pstr = pstr.replace("{" + str + "}", "*");
            pstr = pstr.replace("[" + str + "]", "**");
        }
        if (pstr.indexOf("***") != -1) {
            throw new IllegalArgumentException(
                    "URI definition error, any two variables can't be connected. such as /{v1}{v2}/ is BAD. /{v1}-{v2}/ is OK.");
        }
        pattern = new ZolltyPathMatcher(pstr);
        if (LogFactory.isDebugEnabled()) {
            LOG.debug("URI Real Pattern={}", pstr);
        }
        patternStr = pstr;
    }

    public WebHandler getHandler(String servletURI, String method) {
        if (!controller.allowMethod(method)) {
            return null;
        }
        List<String> valueList = pattern.match(servletURI);
        if (valueList != null) {
            Map<String, String> paramsMap = new HashMap<String, String>();
            for (int i = 0; i < paramsName.size(); i++) {
                paramsMap.put(paramsName.get(i), valueList.get(i));
            }
            return new ControllerHandler(controller, paramsMap);
        }
        return null;
    }

    public WebHandler getHandler(String servletURI, HttpServletRequest request) {
        return getHandler(servletURI, request.getMethod());
    }

    public ControllerMetaInfo getController() {
        return controller;
    }

    public String getPatternStr() {
        return patternStr;
    }

}
