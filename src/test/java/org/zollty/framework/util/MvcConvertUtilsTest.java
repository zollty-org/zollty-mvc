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
package org.zollty.framework.util;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author zollty
 * @since 2014-5-29
 */
@RunWith(JUnit4.class)
public class MvcConvertUtilsTest {
    
    @Test
    public void testCanConvert() {
        
        assertNotNull(MvcConvertUtils.canConvert(String.class));
        
        assertNull(MvcConvertUtils.canConvert(String[].class));
    }
    
    
    @Test
    public void testConvert() {
        
        assertEquals(new Integer(1243), MvcConvertUtils.convert("1243", Integer.class));
        
        
        assertEquals(new Boolean(true), MvcConvertUtils.convert("true", Boolean.class));
        
    }

}
