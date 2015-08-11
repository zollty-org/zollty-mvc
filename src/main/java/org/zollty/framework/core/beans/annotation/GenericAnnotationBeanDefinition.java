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

/**
 * 
 * @author zollty
 * @since 2013-9-15
 */
public class GenericAnnotationBeanDefinition implements AnnotationBeanDefinition {

    private String id;
    private String className;
    private String[] names;
    private List<Field> fields;
    private List<Method> methods;
    private Object object;

    private String beanType = CLASS_BEAN_TYPE;
    private String methodName;

    private boolean finished;

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public List<Field> getInjectFields() {
        return fields;
    }

    @Override
    public List<Method> getInjectMethods() {
        return methods;
    }

    @Override
    public String[] getInterfaceNames() {
        return names;
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
    public void setInterfaceNames(String[] names) {
        this.names = names;

    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String getBeanType() {
        return beanType;
    }

    @Override
    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "GenericAnnotationBeanDefinition [id=" + id + ", className=" + className + "]";
    }

}
