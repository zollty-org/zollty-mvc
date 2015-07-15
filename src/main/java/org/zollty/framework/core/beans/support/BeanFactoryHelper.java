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
package org.zollty.framework.core.beans.support;

import org.zollty.framework.core.beans.BeanFactory;


/**
 * Get Beans from BeanFactory under single ClassLoader enviriment.
 * If under Webapp enviriment, use 
 * {@code ContextLoader.getCurrentWebApplicationContext()} instead.
 * @author zollty 
 * @since 2013-9-22
 */
public class BeanFactoryHelper {
	
	private static BeanFactory beanFactory;
	
	// 不允许创建实例
	private BeanFactoryHelper(){
	}
	
	/**
	 * @return 当前的BeanFactory或者ApplicationContext实例，切勿修改
	 */
	public static final BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	/**
	 * 只允许在当前包路径下面对beanFactory进行重新赋值
	 */
	static void refreshBeanFactory(BeanFactory beanFactory){
		BeanFactoryHelper.beanFactory = beanFactory;
	}
}
