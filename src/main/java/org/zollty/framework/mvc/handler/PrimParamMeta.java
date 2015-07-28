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
 * Create by ZollTy on 2013-9-21 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.handler;

import javax.servlet.http.HttpServletRequest;

import org.zollty.framework.util.MvcUtils;

/**
 * @author zollty
 * @since 2013-9-21
 */
public class PrimParamMeta {

    private final Class<?> paramClass;
    private final String attribute;
    private final boolean setAttr;
    private String orgValue;

    public PrimParamMeta(Class<?> paramClass, String attribute, boolean setAttr) {
        this.paramClass = paramClass;
        this.attribute = attribute;
        this.setAttr = setAttr;
    }

    /**
     * @return the paramClass
     */
    public Class<?> getParamClass() {
        return paramClass;
    }

    /**
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @return the setAttr
     */
    public boolean isSetAttr() {
        return setAttr;
    }

    /**
     * @return the orgValue
     */
    public String getOrgValue() {
        return orgValue;
    }

    public Object getValue(HttpServletRequest request) {
        this.orgValue = request.getParameter(attribute);
        return MvcUtils.ConvertUtil.convert(this.orgValue, this.getParamClass());
    }

}