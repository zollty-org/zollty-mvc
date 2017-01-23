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

import org.zollty.framework.core.beans.BeanDefinition;

/**
 * Xml方式Bean定义
 */
public interface XmlBeanDefinition extends BeanDefinition {

    /**
     * 取得属性集合
     */
    public LinkedHashMap<String, Object> getProperties();
    
    /**
     * 设置属性集合
     * 
     * @param properties
     */
    public void setProperties(LinkedHashMap<String, Object> properties);
    
    /**
     * 取得参数集合
     */
    public LinkedList<Object> getConstructorArgs();
    
    /**
     * 设置参数集合
     */
    public void setConstructorArgs(LinkedList<Object> constructorArgs);
}