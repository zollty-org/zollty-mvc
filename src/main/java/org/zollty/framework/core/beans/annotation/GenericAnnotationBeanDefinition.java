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
 * Create by ZollTy on 2013-9-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.beans.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.zollty.framework.core.beans.AbstractBeanDefinition;

/**
 * 
 * @author zollty
 * @since 2013-9-15
 */
public class GenericAnnotationBeanDefinition extends AbstractBeanDefinition implements AnnotationBeanDefinition {

    private List<Field> fields;
    private List<Method> methods;

    @Override
    public List<Field> getInjectFields() {
        return fields;
    }

    @Override
    public List<Method> getInjectMethods() {
        return methods;
    }

    @Override
    public void setInjectFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    public void setInjectMethods(List<Method> methods) {
        this.methods = methods;
    }

    @Override
    public String toString() {
        return "GenericAnnotationBeanDefinition [id=" + getId() + ", className=" + getClassName() + "]";
    }

}
