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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.zollty.framework.core.beans.BeanDefinitionParsingException;
import org.zollty.framework.core.beans.xml.XmlBeanDefinition;
import org.zollty.framework.util.dom.DomParser;

public class XmlParserFactory {
    
    private static final Map<String, ElementParser> map = new HashMap<String, ElementParser>();
    private static BeanParser beanNodeParser = new BeanParser();
    
    public static final String BEAN_ELEMENT = "bean";
    public static final String IMPORT_ELEMENT = "import";
    
    public static final String REF_ELEMENT = "ref";
    public static final String VALUE_ELEMENT = "value";
    public static final String ARRAY_ELEMENT = "array";
    public static final String LIST_ELEMENT = "list";
    public static final String SET_ELEMENT = "set"; // 和list等价
    public static final String MAP_ELEMENT = "map";
    public static final String MAP_VALUE_ELEMENT = "value";
    public static final String NULL_ELEMENT = "null";

    static {
        map.put(BEAN_ELEMENT, beanNodeParser);
        
        map.put(REF_ELEMENT, new RefParser());
        map.put(VALUE_ELEMENT, new ValueParser());
        map.put(LIST_ELEMENT, new ListParser());
        map.put(SET_ELEMENT, new ListParser());
        map.put(ARRAY_ELEMENT, new ArrayParser());
        map.put(MAP_ELEMENT, new MapParser());
        map.put(NULL_ELEMENT, new NullParser());
    }
    
    private XmlParserFactory() {
    }

    public static ElementParser getParser(String elementName) {
        return map.get(elementName);
    }
    
    public static XmlBeanDefinition parserBean(Element ele, DomParser dom) {
        return beanNodeParser.parse(ele, dom);
    }
    
    public static Object getElementValue(Element ele, DomParser dom) {
        String elementName = ele.getNodeName() != null ? ele.getNodeName() : ele.getLocalName();

        ElementParser xmlNodeParser = XmlParserFactory.getParser(elementName);
        if (xmlNodeParser == null) {
            throw new BeanDefinitionParsingException("Unknown property sub-element: ["
                    + ele.getNodeName() + "]");
        }
        
        return xmlNodeParser.parse(ele, dom);
    }
}