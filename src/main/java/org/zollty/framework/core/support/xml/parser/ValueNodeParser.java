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
package org.zollty.framework.core.support.xml.parser;

import org.w3c.dom.Element;

import static org.zollty.framework.core.support.xml.XmlNodeConstants.*;

import org.zollty.framework.core.support.xml.ManagedValue;
import org.zollty.framework.util.dom.Dom;

public class ValueNodeParser extends AbstractXmlNodeParser {

    @Override
    public Object parse(Element ele, Dom dom) {
        ManagedValue typedValue = new ManagedValue();
        String value = dom.getTextValue(ele);
        String typeName = null;
        if (ele.hasAttribute(TYPE_ATTRIBUTE)) {
            // 如果有type属性
            typeName = ele.getAttribute(TYPE_ATTRIBUTE);
            if (typeName == null) {
                error("<value> element contains empty target attribute");
                return null;
            }
        }

        typedValue.setValue(value);
        typedValue.setTypeName(typeName);
        return typedValue;
    }
}