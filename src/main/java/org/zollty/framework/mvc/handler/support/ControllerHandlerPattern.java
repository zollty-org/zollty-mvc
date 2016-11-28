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
 * Create by ZollTy on 2014-5-29 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.handler.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zollty.framework.core.Const;
import org.zollty.framework.core.Const.ControllerMethodParamType;
import org.zollty.framework.mvc.handler.ControllerMeta;
import org.zollty.framework.mvc.handler.PrimParamMeta;
import org.zollty.framework.mvc.handler.RequestViewHandler;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.match.ZolltyPathMatcher;

/**
 * @author zollty
 * @since 2014-5-29
 */
class ControllerHandlerPattern {

    private static Logger LOG = LogFactory.getLogger(ControllerHandlerPattern.class);

    private ControllerMeta meta;
    private ZolltyPathMatcher pattern;
    private List<String> paramsName;
    private String patternStr;

    /**
     * @param meta
     * @param paramsName
     */
    public ControllerHandlerPattern(ControllerMeta meta, List<String> paramsName) {
        this.meta = meta;
        this.paramsName = paramsName;

        this.initContext();
    }

    public RequestViewHandler getHandler(String servletURI, String method) {
        if (!meta.allowMethod(method)) {
            return null;
        }
        List<String> valueList = pattern.match(servletURI);
        if (valueList != null) {
            Map<String, String> paramsMap = new HashMap<String, String>();
            for (int i = 0; i < paramsName.size(); i++) {
                paramsMap.put(paramsName.get(i), valueList.get(i));
            }
            return new RequestViewHandler(meta, paramsMap);
        }
        return null;
    }

    public ControllerMeta getMeta() {
        return meta;
    }

    public String getPatternStr() {
        return patternStr;
    }

    private void initContext() {
        // Check Params
        ControllerMethodParamType[] paramType = meta.getParamType();
        PrimParamMeta[] paramMetaPrims = meta.getParamMetaPrims();
        for (int i = 0; i < paramType.length; i++) {
            if (paramType[i] == Const.ControllerMethodParamType.URIParam) {
                PrimParamMeta pb = paramMetaPrims[i];
                int pos = Arrays.binarySearch(paramsName.toArray(new String[paramsName.size()]),
                        pb.getAttribute());
                // 如果参数的名称是在 URI 参数列表中，则OK，否则报错。
                if (pos < 0) {
                    throw new IllegalArgumentException(
                            "URIParam definition error, can't find the variable '"
                                    + pb.getAttribute() + "' in URI param(such as /{v1}{v2}/).");
                }
            }

        }

        String pstr = meta.getServletURI();
        for (String str : paramsName) {
            pstr = pstr.replace("{" + str + "}", "*");
            pstr = pstr.replace("[" + str + "]", "**");
        }
        if (pstr.indexOf("***") != -1) {
            throw new IllegalArgumentException(
                    "URI definition error, any two variables can't be connected. such as /{v1}{v2}/ is BAD. /{v1}-{v2}/ is OK.");
        }
        
        LOG.debug("URI Real Pattern={}", pstr);
        this.pattern = new ZolltyPathMatcher(pstr);
        
        this.patternStr = pstr;
    }

}