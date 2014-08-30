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
 * Create by Zollty Tsou (Contact: zollty@163.com, http://blog.zollty.com)
 */
package org.zollty.framework.mvc.handler.support;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.zollty.framework.mvc.support.ControllerMetaInfo;
import org.zollty.framework.util.MvcReflectUtils;

/**
 * @author zollty
 * @since 2014-6-2
 */
public class ControllerResourceTest {

    @Test
    public void testURIMapping1() {
        // DATA
        String uriPattern = "/mlf4j/{p1}/{p2}/{v1}";
        String servletURI = "/mlf4j/vf/logConfig/uu";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("p1", "vf");
        params.put("p2", "logConfig");
        params.put("v1", "uu");

        runURIMapping(uriPattern, servletURI, params);
    }

    private static void runURIMapping(String uriPattern, String servletURI, HashMap<String, String> params) {
        Method me = MvcReflectUtils.findMethod(Pattern.class, "compile");
        ControllerMetaInfo cm = new ControllerMetaInfo(null, me, new String[] { "GET" }, uriPattern);
        ControllerResource cr = new ControllerResource();
        cr.addController(cm);
        ControllerHandler ha = (ControllerHandler) cr.getHandler(servletURI);
        assertNotNull(ha);
        assertEquals(ha.getParamsMap(), params);
    }

    @Test
    public void testParseUriPathVariable() {
        String uri = "as{ak}sa/v{1}/[c8]-[mk]/{v2}";
        String[] keys = new String[]{"ak", "1", "c8", "mk", "v2"};

        List<String> list = ControllerResource.parseUriPathVariable(uri);
        assertArrayEquals(list.toArray(new String[0]), keys);
    }

}
