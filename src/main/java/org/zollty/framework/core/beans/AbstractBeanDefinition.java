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
package org.zollty.framework.core.beans;

/**
 * 
 * @author zollty
 * @since 2013-9-15
 */
public class AbstractBeanDefinition implements BeanDefinition {

    private String id;
    private String className;
    private String beanType = CLASS_BEAN_TYPE;
    private String methodName;
    private String[] names;
    private Object object;
    private boolean finished;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
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

}
