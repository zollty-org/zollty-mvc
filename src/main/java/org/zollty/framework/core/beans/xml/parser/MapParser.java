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

import java.util.List;

import org.w3c.dom.Element;
import org.zollty.framework.core.beans.xml.value.ManagedMap;
import org.zollty.framework.core.beans.xml.value.ManagedValue;
import org.zollty.framework.util.dom.DomParser;

/**
 * 解析map元素
 * 
 * @author zollty
 * @since 2013-9-15
 */
class MapParser extends AbstractElementParser {
    
    private String MAP_KEY_ELEMENT = KEY_ATTRIBUTE;
    private String MAP_VALUE_ELEMENT = VALUE_ATTRIBUTE;

    @Override
    public Object parse(Element ele, DomParser dom) {
        // 获取key,value定义类型
        String typeName = ele.getAttribute(TYPE_ATTRIBUTE);
        ManagedMap<Object, Object> target = new ManagedMap<Object, Object>();
        target.setTypeName(typeName);

        // 获取所有entry元素
        List<Element> elements = dom.elements(ele);
        for (Element entry : elements) {
            Object key = null;
            Object value = null;
            if (entry.hasAttribute(KEY_ATTRIBUTE)) { // 如果有key属性
                key = new ManagedValue(entry.getAttribute(KEY_ATTRIBUTE));
            }

            if (entry.hasAttribute(VALUE_ATTRIBUTE)) { // 如果有value属性
                value = new ManagedValue(entry.getAttribute(VALUE_ATTRIBUTE));
            }

            // 获取key元素
            List<Element> keyEle = dom.elements(entry, MAP_KEY_ELEMENT);
            if (keyEle.size() > 1) {
                // 有且只能有一个key元素
                error("must not contain more than one key-element");
            }
            else if (keyEle.size() == 1) {
                if (key != null) {
                    // key属性和key元素只能有一个
                    error("only allowed to contain either 'key' attribute OR key-element");
                }
                else {
                    // 获取key子元素
                    List<Element> subKey = dom.elements(keyEle.get(0));
                    if (subKey.size() == 0) {
                        String keyText = dom.getTextValue(keyEle.get(0));
                        if (keyText == null)
                            error("must contain one key-sub-element");
                        else
                            key = new ManagedValue(keyText);
                    }
                    else {
                        key = XmlParserFactory.getElementValue(subKey.get(0), dom);
                    }
                }
            }
            else {
                if (key == null)
                    error("not contain 'key' attribute or key-element");
            }

            // 获取value元素
            List<Element> valueEle = dom.elements(entry, MAP_VALUE_ELEMENT);
            if (valueEle.size() > 1) {
                // 有且只能有一个value元素
                error("must not contain more than one value-element");
            }
            else if (valueEle.size() == 1) {
                if (value != null) {
                    // value属性和value元素只能有一个
                    error("only allowed to contain either 'value' attribute or value-element");
                }
                else {
                    // 获取value子元素
                    List<Element> subValue = dom.elements(valueEle.get(0));
                    if (subValue.size() != 1) {
                        String valueText = dom.getTextValue(valueEle.get(0));
                        if (valueText == null)
                            error("must contain one value-sub-element");
                        else
                            value = new ManagedValue(valueText);
                    }
                    else {
                        value = XmlParserFactory.getElementValue(subValue.get(0), dom);
                    }
                }
            }
            else {
                if (value == null)
                    error("not contain 'value' attribute or value-element");
            }

            target.put(key, value);
        }
        return target;
    }

}