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
 * Create by ZollTy on 2013-9-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.beans.xml.parser;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zollty.framework.core.beans.BeanDefinition;
import org.zollty.framework.core.beans.xml.GenericXmlBeanDefinition;
import org.zollty.framework.core.beans.xml.XmlBeanDefinition;
import org.zollty.framework.core.beans.xml.value.ManagedRef;
import org.zollty.framework.core.beans.xml.value.ManagedValue;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.DomParser;

/**
 * 
 * @author zollty
 * @since 2013-9-15
 */
class BeanParser extends AbstractElementParser {
    
    private String ID_ATTRIBUTE = "id";
    private String CLASS_ATTRIBUTE = "class";
    private String NAME_ATTRIBUTE = "name";
    private String REF_ATTRIBUTE = "ref";

    private String PROPERTY_ELEMENT = "property";
    private String CONSTRUCTOR_ELEMENT = "constructor";

    public XmlBeanDefinition parse(Element ele, DomParser dom) {
        XmlBeanDefinition xmlBeanDefinition = new GenericXmlBeanDefinition();

        // 获取所有property
        List<Element> properties = dom.elements(ele, PROPERTY_ELEMENT);

        // 迭代property列表
        if (properties != null && !properties.isEmpty()) {
            for (Element property : properties) {
                String name = property.getAttribute(NAME_ATTRIBUTE);

                boolean hasValueAttribute = property.hasAttribute(VALUE_ATTRIBUTE);
                boolean hasRefAttribute = property.hasAttribute(REF_ATTRIBUTE);

                // 只能有一个子元素: ref, value, list, etc.
                NodeList nl = property.getChildNodes();
                Element subElement = null;
                for (int i = 0; i < nl.getLength(); ++i) {
                    Node node = nl.item(i);
                    if (node instanceof Element) {
                        if (subElement != null) {
                            error(name + " must not contain more than one sub-element");
                        } else {
                            subElement = (Element) node;
                        }
                    }
                }

                if (hasValueAttribute && hasRefAttribute
                        || ((hasValueAttribute || hasRefAttribute) && subElement != null)) {
                    error(name
                            + " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element");
                }

                if (hasValueAttribute) {
                    // 普通赋值
                    String value = property.getAttribute(VALUE_ATTRIBUTE);
                    if (MvcUtils.StringUtil.isBlank(value)) {
                        error(name + " contains empty 'value' attribute");
                    }
                    xmlBeanDefinition.getProperties().put(name, new ManagedValue(value));
                }
                else if (hasRefAttribute) {
                    // 依赖其他bean
                    String ref = property.getAttribute(REF_ATTRIBUTE);
                    if (MvcUtils.StringUtil.isBlank(ref)) {
                        error(name + " contains empty 'ref' attribute");
                    }
                    xmlBeanDefinition.getProperties().put(name, new ManagedRef(ref));
                }
                else if (subElement != null) {
                    // 处理子元素
                    Object subEle = XmlParserFactory.getElementValue(subElement, dom);
                    xmlBeanDefinition.getProperties().put(name, subEle);
                }
                else {
                    error(name + " must specify a ref or value");
                    return null;
                }
            }
        }
        
        // 获取constructor
        List<Element> constructorArs = dom.elements(ele, CONSTRUCTOR_ELEMENT);
        LinkedList<Object> constructorArgs = new LinkedList<Object>();
        if (constructorArs != null && !constructorArs.isEmpty()) {
            Element property = constructorArs.get(0);

            NodeList nl = property.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node node = nl.item(i);
                if (node instanceof Element) {
                    Element subElement = (Element) node;
                    // 处理子元素
                    Object subEle = XmlParserFactory.getElementValue(subElement, dom);
                    constructorArgs.add(subEle);
                }
            }
            xmlBeanDefinition.setConstructorArgs(constructorArgs);
        }

        // 获取基本属性
        String id = ele.getAttribute(ID_ATTRIBUTE);
        xmlBeanDefinition.setId(id);
        String className = ele.getAttribute(CLASS_ATTRIBUTE);

        if (className.indexOf("#") == -1) {
            xmlBeanDefinition.setBeanType(BeanDefinition.CLASS_BEAN_TYPE);
            xmlBeanDefinition.setClassName(className);
            
        } else { // method bean
            xmlBeanDefinition.setBeanType(BeanDefinition.METHOD_BEAN_TYPE);
            String[] tempArray = className.split("#");
            if (tempArray.length != 2) {
                error(id + " class attribute define error: " + className);
            }
            className = tempArray[0];
            String methodName = tempArray[1];
            xmlBeanDefinition.setClassName(className);
            xmlBeanDefinition.setMethodName(methodName);
        }
        return xmlBeanDefinition;
    }
    
}