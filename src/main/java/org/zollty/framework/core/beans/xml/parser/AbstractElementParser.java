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

import org.zollty.framework.core.beans.BeanDefinitionParsingException;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;

abstract class AbstractElementParser implements ElementParser {

    protected Logger logger = LogFactory.getLogger(getClass());
    
    protected String TYPE_ATTRIBUTE = "type";
    protected String KEY_ATTRIBUTE = "key";
    protected String VALUE_ATTRIBUTE = "value";

    protected void error(String msg) {
        logger.error(msg);
        throw new BeanDefinitionParsingException(msg);
    }
}