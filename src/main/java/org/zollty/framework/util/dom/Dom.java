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
package org.zollty.framework.util.dom;

import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author zollty
 * @since 2013-5-15
 */
public interface Dom {

//	/**
//	 * 根据文件读取文档对象
//	 * @param file
//	 */
//	Document getDocument(String file);
//	
//	
//	Document getDocument(String resourceLocation, ClassLoader classLoader);
//	
//	
//	Document getDocument(String resourceLocation, ClassLoader classLoader, ServletContext servletContext);
	
	/**
	 * 
	 * @param inputStream
	 */
	Document getDocument(InputStream inputStream);
	
	/**
	 * 取得根节点
	 * @param doc
	 */
	Element getRoot(Document doc);
	
	/**
	 * 取得所有子元素
	 * @param e
	 */
	List<Element> elements(Element e);
	
	/**
	 * 根据元素名取得子元素列表
	 * @param e
	 * @param name
	 */
	List<Element> elements(Element e, String name);
	
	/**
	 * 获取元素
	 * @param e
	 * @param name
	 */
	Element element(Element e, String name);
	
	/**
	 * 获取元素值
	 * @param valueEle
	 */
	String getTextValue(Element valueEle);
}
