/* 
 * Copyright (C) 2012-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by Zollty Tsou [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.core.support.xml;

import java.util.LinkedHashMap;

import org.zollty.framework.core.support.BeanDefinition;

/**
 * Xml方式Bean定义
 * @author zollty
 */
public interface XmlBeanDefinition extends BeanDefinition {

	/**
	 * 取得属性集合
	 */
	public abstract LinkedHashMap<String, Object> getProperties();
	
	/**
	 * 设置属性集合
	 * @param properties
	 */
	public abstract void setProperties(LinkedHashMap<String, Object> properties);
}
