/* @(#)ContextLoaderListener.java 
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
package org.zollty.framework.mvc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.zollty.framework.mvc.context.ContextLoader;

/**
 * @author zollty 
 * @since 2013-9-16
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

	private ContextLoader contextLoader;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (this.contextLoader == null) {
			this.contextLoader = this;
		}
		this.contextLoader.initWebApplicationContext(event.getServletContext());
	}

	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
		System.out.println(" ================================================================= ");
		System.out.println("================= zolltyMVC contextDestroyed ======================");
		System.out.println(" ================================================================= ");
	}
}
