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
package org.zollty.framework.core;

import org.zollty.framework.core.beans.BeanFactory;


/**
 * @author zollty 
 * @since 2013-9-22
 */
public class BeanFactoryHelper {
	
	private static boolean isInited = false;
	private static BeanFactory beanFactory;
	
	// 不允许创建实例
	private BeanFactoryHelper(){
	}
	
	/**
	 * @return the beanFactory
	 */
	public static final BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	public static void setBeanFactory(BeanFactory beanFactory){
		if(!isInited){
			BeanFactoryHelper.beanFactory = beanFactory;
			isInited = true;
		}
	}
	
}
