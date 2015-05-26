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
package org.zollty.framework.mvc.context.support;

import javax.servlet.ServletContext;

import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.core.context.support.AbstractApplicationContext;
import org.zollty.framework.mvc.context.ConfigurableWebApplicationContext;
import org.zollty.framework.mvc.handler.HandlerMapping;

/**
 * @author zollty 
 * @since 2013-10-11
 */
abstract public class AbstractWebApplicationContext extends AbstractApplicationContext implements ConfigurableWebApplicationContext {

	private ServletContext servletContext;
	
	protected HandlerMapping handlerMapping;

    public AbstractWebApplicationContext(IApplicationConfig config) {
        super(config);
    }

   public AbstractWebApplicationContext(IApplicationConfig config, ClassLoader beanClassLoader) {
       super(config, beanClassLoader);
   }
   
   public AbstractWebApplicationContext(IApplicationConfig config, ClassLoader beanClassLoader, ServletContext servletContext) {
       super(config, beanClassLoader);
       
       this.servletContext = servletContext;
   }
	
	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
    /**
     * @return the handlerMapping
     */
    @Override
    public final HandlerMapping getHandlerMapping() {
        return handlerMapping;
    }
}
