/* @(#)DispatcherServlet.java 
 * Copyright (C) 2013-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by zollty on 2013-6-02 [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.handler.DispatcherController;


/**
 * @author zollty 
 * @since 2013-6-02
 */
@SuppressWarnings("serial")
public class DispatcherServlet extends DispatcherController {
	
	@Override
	public void dispatcher(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String servletURI = new String( request.getRequestURI().substring( request.getContextPath().length() ) );
		
		// 排除拦截 的前缀，例如 可设置 /resources/ 开头的请求，一律不拦截
		for(String prefix: this.handlerMapping.getExcludePrefixes()){
			if( servletURI.startsWith(prefix) ){
				this.defaultServlet.handleRequest(request, response);
				return;
			}
		}
		// 排除拦截 的后缀
		for(String suffix: this.handlerMapping.getExcludeSuffixes()){
			if( servletURI.endsWith(suffix) ){
				this.defaultServlet.handleRequest(request, response);
				return;
			}
		}
		
		this.handleRequest(servletURI, request, response);
		
	}

	@Override
	public void finalInit() {
	}

}
