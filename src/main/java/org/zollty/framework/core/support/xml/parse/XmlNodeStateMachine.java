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

import org.w3c.dom.Element;
import org.zollty.framework.core.support.exception.BeanDefinitionParsingException;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.Dom;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

public class XmlNodeStateMachine {
    
    private static Logger log = LogFactory.getLogger(XmlNodeStateMachine.class);

    public static Object stateProcessor(Element ele, Dom dom) {
        // Object ret = null;
        String elementName = ele.getNodeName() != null ? ele.getNodeName() : ele.getLocalName();

        if (XmlNodeConstants.BEAN_ELEMENT.equals(elementName)) {
            return BeanNodeParser.parse(ele, dom, MvcUtils.ClassUtil.getDefaultClassLoader());
        }
        else {
            XmlNodeParser xmlNodeParser = XmlNodeParserFactory.getParser(elementName);
            if (xmlNodeParser == null)
                error("Unknown property sub-element: [" + ele.getNodeName() + "]");
            return xmlNodeParser.parse(ele, dom);
        }
    }

    public static Object getXmlBeanDefinition(Element ele, Dom dom, ClassLoader beanClassLoader) {

        String elementName = ele.getNodeName() != null ? ele.getNodeName() : ele.getLocalName();

        if (XmlNodeConstants.BEAN_ELEMENT.equals(elementName)) {
            return BeanNodeParser.parse(ele, dom, beanClassLoader);
        }
        else {
            XmlNodeParser xmlNodeParser = XmlNodeParserFactory.getParser(elementName);
            if (xmlNodeParser == null)
                error("Unknown property sub-element: [" + ele.getNodeName() + "]");
            return xmlNodeParser.parse(ele, dom);
        }

    }

    private static void error(String msg) {
        log.error(msg);
        throw new BeanDefinitionParsingException(msg);
    }
}