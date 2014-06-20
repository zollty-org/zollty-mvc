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

import java.util.List;

import org.zollty.framework.core.support.BeanDefinition;

/**
 * @author zollty 
 * @since 2013-12-7
 */
public class SimpleBeanFactory extends AbstractBeanFactory {
	
	@Override
	public void refresh(){
		check(); //冲突检测
		addObjectToContext();
	}

	@Override
	protected List<BeanDefinition> loadBeanDefinitions() {
		//throw new UnsupportedOperationException("not support this method. please use loadBeanDefinitions(List<BeanDefinition> beandef) replace. ");
		return this.beanDefinitions;
	}
	
	public void loadBeanDefinitions(List<BeanDefinition> beandef){
		this.beanDefinitions = beandef;
	}


    @Override
    protected void doBeforeRefresh() {
    }

    @Override
    protected void doAfterRefresh() {
    }

}
