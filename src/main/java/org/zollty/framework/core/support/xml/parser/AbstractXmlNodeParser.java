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

import org.zollty.framework.core.support.BeanDefinitionParsingException;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

abstract public class AbstractXmlNodeParser implements XmlNodeParser {

    protected Logger log = LogFactory.getLogger(AbstractXmlNodeParser.class);

    protected void error(String msg) {
        log.error(msg);
        throw new BeanDefinitionParsingException(msg);
    }
}