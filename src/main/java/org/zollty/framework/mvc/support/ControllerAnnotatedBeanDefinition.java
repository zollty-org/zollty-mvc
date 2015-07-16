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
package org.zollty.framework.mvc.support;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.zollty.framework.core.support.annotation.GenericAnnotationBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;

/**
 * @author zollty
 * @since 2013-9-21
 */
public class ControllerAnnotatedBeanDefinition extends GenericAnnotationBeanDefinition implements
        ControllerBeanDefinition {

    private List<Method> reqMethods;

    private Map<Method, List<MvcBeforeBeanDefinition>> reqMethodsAOP;

    private String uriPrefix;

    @Override
    public List<Method> getReqMethods() {
        return reqMethods;
    }

    @Override
    public void setReqMethods(List<Method> reqMethods) {
        this.reqMethods = reqMethods;
    }

    @Override
    public Map<Method, List<MvcBeforeBeanDefinition>> getReqMethodsAOP() {
        return reqMethodsAOP;
    }

    @Override
    public void setReqMethodsAOP(Map<Method, List<MvcBeforeBeanDefinition>> reqMethodsAOP) {
        this.reqMethodsAOP = reqMethodsAOP;
    }

    @Override
    public String getUriPrefix() {
        return uriPrefix;
    }

    @Override
    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    @Override
    public String toString() {
        return "ControllerAnnotatedBeanDefinition [id=" + getId() + ", className=" + getClassName() + "]";
    }
}