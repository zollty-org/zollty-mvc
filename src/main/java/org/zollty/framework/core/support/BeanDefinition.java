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
package org.zollty.framework.core.support;


/**
 * Bean信息
 * @author zollty
 */
public interface BeanDefinition {
	
	String CLASS_BEAN_TYPE = "class";
	String METHOD_BEAN_TYPE = "method";
	
	// id className 以及该组件所有接口名作为 map 的key
	String getId();

	String getClassName();

	void setId(String id);

	void setClassName(String className);

	String[] getInterfaceNames();

	void setInterfaceNames(String[] names);

	// 该组件的对象实例
	Object getObject();

	void setObject(Object object);
	
	boolean isFinished();
	
	void setFinished(boolean finished);
	
	String getBeanType();
	
	void setBeanType(String beanType);
	
	String getMethodName();
	
	void setMethodName(String methodName);
	
}
