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
 * Create by ZollTy on 2015-2-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.aop.bean;

import java.lang.reflect.Method;

import org.zollty.framework.core.support.annotation.AnnotatedBeanDefinition;

/**
 * @author zollty
 * @since 2015-2-15
 */
public class MvcBeforeBeanDefinition extends AnnotatedBeanDefinition implements AopBeanDefinition {

    private Method disposeMethod;

    private int order;

    @Override
    public Method getDisposeMethod() {
        return disposeMethod;
    }

    @Override
    public void setDisposeMethod(Method disposeMethod) {
        this.disposeMethod = disposeMethod;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "MvcBeforeBeanDefinition [id=" + getId() + ", className=" + getClassName() + "]";
    }

}