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
 * Zollty Framework MVC Source Code - Since v1.0
 * Author(s): 
 * Zollty Tsou (zolltytsou@gmail.com, http://blog.zollty.com)
 */
package org.zollty.framework.core.beans.support;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.zollty.framework.core.support.xml.XmlBeanReader;
import org.zollty.framework.util.ResourcContext;

/**
 * @author zollty
 * @since 2013-12-7
 */
public class XmlBeanTest {

    private static final String XML_PATH = "classpath:org/zollty/framework/core/beans/support/bean-test.xml";

    @Test
    public void testSimpleBeanFactory() {

        ResourcContext resourcContext = new ResourcContext(XML_PATH, Thread.currentThread().getContextClassLoader());
        SimpleBeanFactory sbf = new SimpleBeanFactory( new XmlBeanReader(resourcContext) );

        ArrayBean aBean = sbf.getBean("arrayBean");

        assertNotNull(aBean);
        assertArrayEquals(aBean.getCodes(), new int[] { 128, 16 });
    }

}
