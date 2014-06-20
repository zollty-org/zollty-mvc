/*
 * @(#)InterceptorResource.java
 * Travelsky Report Engine (TRE) Source Code, Version 2.0
 * Author(s): 
 * Zollty Tsou (http://blog.csdn.net/zollty, zouty@travelsky.com)
 * Copyright (C) 2014-2015 Travelsky Technology. All rights reserved.
 */
package org.zollty.framework.mvc.handler.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.support.InterceptorMetaInfo;

/**
 * @author zollty
 * @since 2014-5-29
 */
public class InterceptorResource {
    
    protected final List<InterceptorMetaInfo> interceptorList = new LinkedList<InterceptorMetaInfo>();
    
    public void addInterceptor(List<InterceptorMetaInfo> interceptorList){
        this.interceptorList.addAll(interceptorList);
        Collections.sort(this.interceptorList);
    }
    
    public List<WebHandler> getHandlers(String servletURI){
        if (servletURI == null) {
            return null;
        }
        List<WebHandler> mappedHandlers = new ArrayList<WebHandler>();
        for (final InterceptorMetaInfo interceptor : interceptorList) {
            if (interceptor.match(servletURI)) {
                mappedHandlers.add(new InterceptorHandler(interceptor, servletURI));
            }
        }
        return mappedHandlers;
    }

}
