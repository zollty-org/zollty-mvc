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
package org.zollty.framework.core.beans.xml.parser;

import static org.zollty.framework.core.beans.xml.XmlNodeConstants.ARRAY_ELEMENT;
import static org.zollty.framework.core.beans.xml.XmlNodeConstants.LIST_ELEMENT;
import static org.zollty.framework.core.beans.xml.XmlNodeConstants.MAP_ELEMENT;
import static org.zollty.framework.core.beans.xml.XmlNodeConstants.REF_ELEMENT;
import static org.zollty.framework.core.beans.xml.XmlNodeConstants.SET_ELEMENT;
import static org.zollty.framework.core.beans.xml.XmlNodeConstants.VALUE_ELEMENT;
import static org.zollty.framework.core.beans.xml.XmlNodeConstants.NULL_ELEMENT;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.zollty.framework.core.beans.BeanDefinitionParsingException;
import org.zollty.framework.core.beans.xml.XmlNodeConstants;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.Dom;

public class XmlNodeParserFactory {
    
    private static final Map<String, XmlNodeParser> map = new HashMap<String, XmlNodeParser>();

    static {
        map.put(REF_ELEMENT, new RefNodeParser());
        map.put(VALUE_ELEMENT, new ValueNodeParser());
        map.put(LIST_ELEMENT, new ListNodeParser());
        map.put(SET_ELEMENT, new ListNodeParser());
        map.put(ARRAY_ELEMENT, new ArrayNodeParser());
        map.put(MAP_ELEMENT, new MapNodeParser());
        map.put(NULL_ELEMENT, new NullNodeParser());
    }

    public static XmlNodeParser getParser(String elementName) {
        return map.get(elementName);
    }
    
    public static Object getXmlBeanDefinition(Element ele, Dom dom, ClassLoader beanClassLoader) {

        String elementName = ele.getNodeName() != null ? ele.getNodeName() : ele.getLocalName();

        if (XmlNodeConstants.BEAN_ELEMENT.equals(elementName)) {
            return BeanNodeParser.parse(ele, dom, beanClassLoader != null ? beanClassLoader
                    : MvcUtils.ClassUtil.getDefaultClassLoader());
        }

        XmlNodeParser xmlNodeParser = XmlNodeParserFactory.getParser(elementName);
        if (xmlNodeParser == null) {
            throw new BeanDefinitionParsingException("Unknown property sub-element: ["
                    + ele.getNodeName() + "]");
        }
        return xmlNodeParser.parse(ele, dom);
    }
}