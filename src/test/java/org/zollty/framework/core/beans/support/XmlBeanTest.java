/*
 * @(#)XmlBeanTest.java
 * Create by Zollty_Tsow on 2013-12-7 
 * you may find ZollTy at csdn, github, oschina, stackoverflow...
 * e.g. https://github.com/zollty  http://www.cnblogs.com/zollty 
 * 
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 */
package org.zollty.framework.core.beans.support;

import java.util.List;

import org.zollty.framework.core.beans.support.SimpleBeanFactory;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.xml.XmlBeanReader;

/**
 * @author zollty 
 * @since 2013-12-7
 */
public class XmlBeanTest {
	
	public static final String XML_PATH = "test/bean/bean-test.xml";

	/**
	 * @param args
	 * @author zollty
	 */
	public static void main(String[] args) {
		
		SimpleBeanFactory sbf = new SimpleBeanFactory();
		List<BeanDefinition> beanslist = new XmlBeanReader( 
				Thread.currentThread().getContextClassLoader(),
				XML_PATH ).loadBeanDefinitions();
		sbf.loadBeanDefinitions(beanslist);
		sbf.refresh();
		
		BeanHolder holder = sbf.getBean("holder");
		System.out.println(holder.getCodes()[0]);
	}

}
