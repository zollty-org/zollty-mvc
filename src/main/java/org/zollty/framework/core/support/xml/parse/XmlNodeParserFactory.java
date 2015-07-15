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
package org.zollty.framework.core.support.xml.parse;

import static org.zollty.framework.core.support.xml.parse.XmlNodeConstants.*;
import java.util.HashMap;
import java.util.Map;

public class XmlNodeParserFactory {
    
    private static final Map<String, XmlNodeParser> map = new HashMap<String, XmlNodeParser>();

    static {
        // map.put(BEAN_ELEMENT, new BeanNodeParser());
        map.put(REF_ELEMENT, new RefNodeParser());
        map.put(VALUE_ELEMENT, new ValueNodeParser());
        map.put(LIST_ELEMENT, new ListNodeParser());
        map.put(SET_ELEMENT, new ListNodeParser());
        map.put(ARRAY_ELEMENT, new ArrayNodeParser());
        map.put(MAP_ELEMENT, new MapNodeParser());
    }

    public static XmlNodeParser getParser(String elementName) {
        return map.get(elementName);
    }
}