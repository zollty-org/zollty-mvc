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
package org.zollty.framework.mvc.handler;

import java.util.List;

import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.mvc.handler.support.HandlerChainImpl;


/**
 * @author zollty 
 * @since 2013-9-16
 */
public class HttpServletDispatcherHandler extends AbstractHandlerMapping {
	
	public HttpServletDispatcherHandler(List<BeanDefinition> beanDefinitions){
		super(beanDefinitions);
	}
	
	@Override
	public HandlerChainImpl match(String servletURI) {
		final HandlerChainImpl chain = new HandlerChainImpl();
		addInterceptor(servletURI, chain);
		addLastHandler(servletURI, chain);
		chain.init();
		return chain;
	}
	
    protected void addInterceptor(String servletURI, final HandlerChainImpl chain) {
        List<WebHandler> handlers = interceptorResource.getHandlers(servletURI);
        if (handlers != null) {
            for (WebHandler han : handlers) {
                chain.add(han);
            }
        }
    }
    
    protected void addLastHandler(String servletURI, final HandlerChainImpl chain) {
        WebHandler last = controllerResource.getHandler(servletURI);
        if(last != null){
            chain.add(last);
        }
    }

}
