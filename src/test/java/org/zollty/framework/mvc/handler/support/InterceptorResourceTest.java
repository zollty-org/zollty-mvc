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
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.support.InterceptorMetaInfo;
import org.zollty.framework.util.MvcReflectUtils;

/**
 * @author zollty
 * @since 2014-6-3
 */
public class InterceptorResourceTest {
    
    @Test
    public void testInterceptor(){
        
        String[] uriPatterns1 = new String[]{"/mlf4j/**"};
        String[] uriPatterns2 = new String[]{"/mlf4j/vf/cc/**", "/mlf4j/vf/cb/**"};
        
        String servletURI = "/mlf4j/vf/cb/uu";
        
        
        Method me = MvcReflectUtils.findMethod(Pattern.class, "compile");
        
        InterceptorMetaInfo im1 = new InterceptorMetaInfo(null, me, uriPatterns1, 8);
        InterceptorMetaInfo im2 = new InterceptorMetaInfo(null, me, uriPatterns2, 1);
        
        String interceptor1 = "[interceptor=InterceptorMetaInfo [uriPattern=[/mlf4j/vf/cc/**, /mlf4j/vf/cb/**], order=1], servletURI=/mlf4j/vf/cb/uu]";
        String interceptor2 = "[interceptor=InterceptorMetaInfo [uriPattern=[/mlf4j/**], order=8], servletURI=/mlf4j/vf/cb/uu]";
        
        List<InterceptorMetaInfo> interceptorList = new LinkedList<InterceptorMetaInfo>();
        interceptorList.add(im1);
        interceptorList.add(im2);
        
        InterceptorResource cr = new InterceptorResource();
        cr.addInterceptor(interceptorList);
        List<WebHandler> handlers = cr.getHandlers(servletURI);
        
        assertTrue(interceptor1.equals(handlers.get(0).toString()));
        assertTrue(interceptor2.equals(handlers.get(1).toString()));
    }

}
