/* @(#)HandlerChainImpl.java 
 * Copyright (C) 2013-2014 the original author or authors.
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
package org.zollty.framework.mvc.handler.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.handler.HandlerChain;
import org.zollty.framework.mvc.handler.WebHandler;

/**
 * @author zollty 
 * @since 2013-9-16
 */
public class HandlerChainImpl implements HandlerChain {
	
	private List<WebHandler> list = new LinkedList<WebHandler>();
	private Iterator<WebHandler> iterator;
	
	public int getHandlerSize(){
		return list.size();
	}
	
	/**
	 * 
	 * @param webHandler
	 */
	public void add(WebHandler webHandler) {
		list.add(webHandler);
	}
	
	public void init() {
		iterator = list.iterator();
	}

	/**
     * This method [ chain.doNext(...) ] would be recursive invocation.<p>
     * First invoking at this place, then it could be invoking by InterceptorHandlers of the APP definition.
     * e.g. The APP has a AdminRightInterceptor, it could invoking: </p>
     * <p><code>return chain.doNext(request, response, chain); </code> </p>
     * <p>then the request will back to this place [ chain.doNext(...) ].</p>
     */
	@Override
	public View doNext(HttpServletRequest request,
			HttpServletResponse response, HandlerChain chain) {
		if(iterator.hasNext()){
			return iterator.next().invoke(request, response, chain);
		}else{
			return null;
		}
	}

}
