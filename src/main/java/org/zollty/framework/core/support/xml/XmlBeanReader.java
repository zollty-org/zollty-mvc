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

import static org.zollty.framework.core.support.xml.parse.XmlNodeConstants.BEAN_ELEMENT;
import static org.zollty.framework.core.support.xml.parse.XmlNodeConstants.IMPORT_ELEMENT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zollty.framework.core.config.ConfigReader;
import org.zollty.framework.core.support.AbstractBeanReader;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.xml.parse.XmlNodeStateMachine;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.DefaultDom;
import org.zollty.framework.util.dom.Dom;

/**
 * @author zollty 
 * @since 2013-8-21
 */
public class XmlBeanReader extends AbstractBeanReader {
	
	protected Dom dom = new DefaultDom();
	
	public XmlBeanReader(ClassLoader beanClassLoader) {
		
		beanDefinitions = new ArrayList<BeanDefinition>();
		
		// 得到所有bean节点
		List<Element> beansList = new ArrayList<Element>();

		parseXml(ConfigReader.getInstance().getConfigLocation(), 
				beanClassLoader, beansList);

		// 迭代beans列表
		if (beansList != null) {
			for (Element ele : beansList) {
				beanDefinitions.add( (BeanDefinition)XmlNodeStateMachine.getXmlBeanDefinition(ele, dom, beanClassLoader) );
			}
		}
	}
	
	public XmlBeanReader(ClassLoader beanClassLoader, String xmlLocation) {
		
		beanDefinitions = new ArrayList<BeanDefinition>();
		
		// 得到所有bean节点
		List<Element> beansList = new ArrayList<Element>();

		parseXml(xmlLocation, beanClassLoader, beansList);

		// 迭代beans列表
		if (beansList != null && !beansList.isEmpty()) {
			for (Element ele : beansList) {
				beanDefinitions.add( (BeanDefinition)XmlNodeStateMachine.getXmlBeanDefinition(ele, dom, beanClassLoader) );
			}
		}
	}
	
	private void parseXml(String fileLocation, ClassLoader classLoader, List<Element> beansList) {
		// 获得Xml文档对象
		Document doc = dom.getDocument(fileLocation, classLoader);
		if(doc!=null){
    		// 得到根节点
    		Element root = dom.getRoot(doc);
    		// 得到所有bean节点
    		List<Element> list = dom.elements(root, BEAN_ELEMENT);
    		beansList.addAll(list);
    		
    		// 得到所有import节点
    		List<Element> importList = dom.elements(root, IMPORT_ELEMENT);
    		// 判断循环引用
    		Set<String> existed = new HashSet<String>();
    		existed.add(fileLocation);
    		if (importList != null) {
    			for (Element ele : importList) {
    				if (ele.hasAttribute("resource")) {
    					String resourceLocation = ele.getAttribute("resource");
    					if( MvcUtils.StringUtil.isNotEmpty(resourceLocation) && !existed.contains(resourceLocation)){
    						parseBeansXml(resourceLocation, classLoader, beansList);
    						existed.add(resourceLocation);
    					}
    				}
    			}
    		}
		}
	}
	
	private void parseBeansXml(String fileLocation, ClassLoader classLoader, List<Element> beansList) {
		// 获得Xml文档对象
		Document doc = dom.getDocument(fileLocation, classLoader);
		// 得到根节点
		Element root = dom.getRoot(doc);
		// 得到所有bean节点
		List<Element> list = dom.elements(root, BEAN_ELEMENT);
		beansList.addAll(list);
	}
	
}
