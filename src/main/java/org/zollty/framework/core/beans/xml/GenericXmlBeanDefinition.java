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
 */
package org.zollty.framework.core.beans.xml;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Xml方式Bean实现
 */
public class GenericXmlBeanDefinition implements XmlBeanDefinition {

    // id
    private String id;

    // className
    private String className;

    private String beanType = CLASS_BEAN_TYPE;

    private String methodName;

    // 属性集合
    private LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
    // 参数集合
    private LinkedList<Object> constructorArgs;
    private String[] names;
    private Object object;

    private boolean finished;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LinkedHashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(LinkedHashMap<String, Object> properties) {
        this.properties = properties;
    }
    
    public LinkedList<Object> getConstructorArgs() {
        return constructorArgs;
    }

    public void setConstructorArgs(LinkedList<Object> constructorArgs) {
        this.constructorArgs = constructorArgs;
    }

    @Override
    public String[] getInterfaceNames() {
        return names;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public void setInterfaceNames(String[] names) {
        this.names = names;
    }

    @Override
    public void setObject(Object object) {
        this.object = object;
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
        return "GenericXmlBeanDefinition [id=" + id + ", className=" + className + "]";
    }

}
