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
 * Create by zollty on 2013-9-16 [http://blog.csdn.net/zollty (or GitHub)]
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
	
	public void add(WebHandler webHandler) {
		list.add(webHandler);
	}
	
	public void init() {
		iterator = list.iterator();
	}

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
