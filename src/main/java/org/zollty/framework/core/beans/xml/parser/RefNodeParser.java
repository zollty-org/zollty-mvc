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

import static org.zollty.framework.core.beans.xml.XmlNodeConstants.BEAN_REF_ATTRIBUTE;

import org.w3c.dom.Element;
import org.zollty.framework.core.beans.xml.ManagedRef;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.Dom;

public class RefNodeParser extends AbstractXmlNodeParser {

    @Override
    public Object parse(Element ele, Dom dom) {
        if (ele.hasAttribute(BEAN_REF_ATTRIBUTE)) {
            String refText = ele.getAttribute(BEAN_REF_ATTRIBUTE);
            if (MvcUtils.StringUtil.isNotBlank(refText)) {
                ManagedRef ref = new ManagedRef();
                ref.setBeanName(refText);
                return ref;
            }
            else {
                error("<ref> element contains empty target attribute.");
                return null;
            }
        }
        else {
            String refText = dom.getTextValue(ele);
            if (MvcUtils.StringUtil.isNotEmpty(refText)) {
                ManagedRef ref = new ManagedRef();
                ref.setBeanName(refText);
                return ref;
            }
            error("<ref> element doesn't have any 'bean' attribute or text.");
            return null;
        }
    }

}