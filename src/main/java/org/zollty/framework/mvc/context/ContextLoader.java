/* @(#)ContextLoader.java 
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
package org.zollty.framework.mvc.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.zollty.framework.core.BeanFactoryHelper;
import org.zollty.framework.core.Const;
import org.zollty.framework.mvc.context.support.WebAnnotationAndXmlApplicationContext;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty 
 * @since 2013-9-16
 */
public class ContextLoader {
	
	// SYS
	private Logger logger = LogFactory.getLogger(ContextLoader.class);
			
	/**
	 * Name of servlet context parameter (i.e. {@value}) that can specify the
	 * config location for the root context, falling back to the implementation's
	 * default otherwise.
	 * @see org.zollty.framework.core.Const#DEFAULT_CONFIG_LOCATION
	 */
	public static final String CONFIG_LOCATION_PARAM = "_zollty_mvc_context";
	
	/**
	 * Map from (thread context) ClassLoader to corresponding 'current' WebApplicationContext.
	 */
	private static final Map<ClassLoader, WebApplicationContext> currentContextPerThread =
			new ConcurrentHashMap<ClassLoader, WebApplicationContext>(1);

	/**
	 * The 'current' WebApplicationContext, if the ContextLoader class is
	 * deployed in the web app ClassLoader itself.
	 */
	private static volatile WebApplicationContext currentContext;
	
	/**
	 * The root WebApplicationContext instance that this loader manages.
	 */
	private WebApplicationContext context;

	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		
		if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
			throw new IllegalStateException(
					"Cannot initialize context because there is already a root application context present - " +
					"check whether you have multiple ContextLoader* definitions in your web.xml!");
		}
		
		logger.info("Root WebApplicationContext initialization started ------------------");
		
		long startTime = System.currentTimeMillis();

		try {
			// Store context in local instance variable, to guarantee that
			// it is available on ServletContext shutdown.
			if (this.context == null) {
				this.context = createWebApplicationContext(servletContext);
			}
			
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
			BeanFactoryHelper.setBeanFactory(this.context);
			
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == ContextLoader.class.getClassLoader()) {
				currentContext = this.context;
			}
			else if (ccl != null) {
				currentContextPerThread.put(ccl, this.context);
			}
			
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms ------------------");
			
			return this.context;
		}
		catch (RuntimeException ex) {
			logger.error(ex, "Context initialization failed");
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
			throw ex;
		}
		catch (Throwable err) {
			logger.error(err, "Context initialization failed");
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, err);
			throw new RuntimeException();
		}
	}
	
	
	protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
		String configLocation = sc.getInitParameter(CONFIG_LOCATION_PARAM);
		
		if(null!=configLocation) logger.info("CONFIG_LOCATION at Context initParameter = " + configLocation);
		
		if(configLocation==null){
			configLocation = Const.DEFAULT_CONFIG_LOCATION;
		}else if(configLocation.startsWith("/")){
			configLocation = configLocation.substring(1);
		}
		
		//ConfigReader.getInstance().load(configLocation, sc.getRealPath("/"));
		
		return new WebAnnotationAndXmlApplicationContext(configLocation,sc);
	}


	/**
	 * Obtain the Spring root web application context for the current thread
	 * (i.e. for the current thread's context ClassLoader, which needs to be
	 * the web application's ClassLoader).
	 * @return the current root web application context, or <code>null</code>
	 * if none found
	 */
	public static WebApplicationContext getCurrentWebApplicationContext() {
		ClassLoader ccl = Thread.currentThread().getContextClassLoader();
		if (ccl != null) {
			WebApplicationContext ccpt = currentContextPerThread.get(ccl);
			if (ccpt != null) {
				return ccpt;
			}
		}
		return currentContext;
	}

}
