/* 
 * Copyright (C) 2012-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by Zollty Tsou [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.core.support.xml.parse;

import java.util.List;
import org.w3c.dom.Element;
import org.zollty.framework.core.support.xml.ManagedArray;
import org.zollty.framework.util.dom.Dom;

public class ArrayNodeParser extends AbstractXmlNodeParser {

	@Override
	public Object parse(Element ele, Dom dom) {
		ManagedArray<Object> target = new ManagedArray<Object>();
		List<Element> elements = dom.elements(ele);
		for (Element e : elements) {
			target.add(XmlNodeStateMachine.stateProcessor(e, dom));
		}
		return target;
	}
}
