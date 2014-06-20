/* @(#)InterceptorHandler.java 
 * Copyright (C) 2013-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by zollty on 2013-9-16 [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.handler.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.handler.HandlerChain;
import org.zollty.framework.mvc.handler.InterceptorInfo;
import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.support.BasicParamMetaInfo;
import org.zollty.framework.mvc.support.InterceptorMetaInfo;

/**
 * @author zollty 
 * @since 2013-9-16
 */
public class InterceptorHandler implements WebHandler {
	
	private final String servletURI;
	private final InterceptorMetaInfo interceptor;
	
	public InterceptorHandler(InterceptorMetaInfo interceptor, String servletURI){
		this.interceptor = interceptor;
		this.servletURI = servletURI;
	}
	
	@Override
	public View invoke(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
		
		byte[] paramType = interceptor.getParamType();
		Object[] p = new Object[paramType.length];
		
		for (int i = 0; i < p.length; i++) {
			switch (paramType[i]) {
			case BasicParamMetaInfo.REQUEST:
				p[i] = request;
				break;
			case BasicParamMetaInfo.RESPONSE:
				p[i] = response;
				break;
			case BasicParamMetaInfo.HANDLER_CHAIN:
				p[i] = chain;
				break;
			case BasicParamMetaInfo.INTERCEPTOR_INFO:
				p[i] = new InterceptorInfo(servletURI);
				break;
			}
		}
		
		return interceptor.invokeMethod(p, request, response);
	}
	
    @Override
    public String toString() {
        return "[interceptor=" + interceptor + ", servletURI=" + servletURI + "]";
    }
}
