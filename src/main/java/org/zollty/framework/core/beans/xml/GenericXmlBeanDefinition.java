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

import org.zollty.framework.core.beans.AbstractBeanDefinition;

/**
 * Xml方式Bean实现
 */
public class GenericXmlBeanDefinition extends AbstractBeanDefinition implements XmlBeanDefinition {
    
    private LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
    private LinkedList<Object> constructorArgs;

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
    public String toString() {
        return "GenericXmlBeanDefinition [id=" + getId() + ", className=" + getClassName() + "]";
    }

}
