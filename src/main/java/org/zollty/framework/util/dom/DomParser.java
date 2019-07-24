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
 * Create by ZollTy on 2013-6-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.util.dom;

import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author zollty
 * @since 2013-6-15
 */
public interface DomParser {

    /**
     * 
     * @param inputStream
     */
    Document getDocument(InputStream inputStream);

    /**
     * 取得根节点
     * 
     * @param doc
     */
    Element getRoot(Document doc);

    /**
     * 取得所有子元素
     * 
     * @param e
     */
    List<Element> elements(Element e);

    /**
     * 根据元素名取得子元素列表
     * 
     * @param e
     * @param name
     */
    List<Element> elements(Element e, String name);

    /**
     * 获取元素
     * 
     * @param e
     * @param name
     */
    Element element(Element e, String name);

    /**
     * 获取元素值
     * 
     * @param valueEle
     */
    String getTextValue(Element valueEle);
}