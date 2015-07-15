/* 
 * Copyright (C) 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by ZollTy on 2013-10-11 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.context;

import javax.servlet.ServletContext;

import org.zollty.framework.core.context.ApplicationContext;
import org.zollty.framework.mvc.handler.HandlerMapping;

/**
 * @author zollty
 * @since 2013-10-11
 */
public interface WebApplicationContext extends ApplicationContext {

    /**
     * Context attribute to bind root WebApplicationContext to on successful startup.
     * <p>
     * Note: If the startup of the root context fails, this attribute can contain an exception or
     * error as value. Use WebApplicationContextUtils for convenient lookup of the root
     * WebApplicationContext.
     */
    public static String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";

    public ServletContext getServletContext();

    public HandlerMapping getHandlerMapping();
}