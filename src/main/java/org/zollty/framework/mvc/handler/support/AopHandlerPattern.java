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
 * Create by ZollTy on 2015-7-23 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.handler.support;

import java.util.List;

import org.zollty.framework.mvc.aop.bean.AopBeanDefinition;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.match.ZolltyPathMatcher;

/**
 * 
 * @author zollty
 * @since 2015-7-23
 */
class AopHandlerPattern {

    private static final Logger LOG = LogFactory.getLogger(AopHandlerPattern.class);

    private ZolltyPathMatcher pattern;
    private String patternStr;

    private AopBeanDefinition aopInterc;

    /**
     * @param meta
     * @param paramsName
     */
    public AopHandlerPattern(AopBeanDefinition aopInterc) {
        this.aopInterc = aopInterc;

        String pstr = aopInterc.getUriPattern();
        if (pstr.indexOf("***") != -1) {
            throw new IllegalArgumentException(
                    "URI definition error, more than two '*' (like '***') is not allowed.");
        }
        pattern = new ZolltyPathMatcher(pstr);

        LOG.debug("Aop URI Pattern={}", pstr);
        patternStr = pstr;
    }

    public AopBeanDefinition getHandler(String servletURI) {
        List<String> valueList = pattern.match(servletURI);
        if (valueList != null) {
            return aopInterc;
        }
        return null;
    }

    public String getPatternStr() {
        return patternStr;
    }

}