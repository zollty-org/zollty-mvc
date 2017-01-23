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
package org.zollty.framework.core.beans.xml;

public interface XmlNodeConstants {
    
    public static final String BEAN_REF_ATTRIBUTE = "bean";
    public static final String ID_ATTRIBUTE = "id";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String REF_ATTRIBUTE = "ref";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String KEY_ATTRIBUTE = "key";

    public static final String BEAN_ELEMENT = "bean";
    public static final String IMPORT_ELEMENT = "import";
    public static final String PROPERTY_ELEMENT = "property";
    public static final String REF_ELEMENT = "ref";
    public static final String VALUE_ELEMENT = "value";

    public static final String ARRAY_ELEMENT = "array";
    public static final String LIST_ELEMENT = "list";
    public static final String SET_ELEMENT = "set"; // 和list等价

    public static final String MAP_ELEMENT = "map";
    public static final String MAP_ENTRY_ELEMENT = "entry";
    public static final String MAP_KEY_ELEMENT = "key";
    public static final String MAP_VALUE_ELEMENT = "value";
    public static final String NULL_ELEMENT = "null";
    
}