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
 * Create by ZollTy on 2013-6-02 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.zollty.framework.mvc.HandlerMapping;
import org.zollty.framework.mvc.context.ContextLoader;
import org.zollty.framework.mvc.context.WebApplicationContext;
import org.zollty.framework.mvc.context.WebApplicationContextUtils;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;

/**
 * @author zollty
 * @since 2013-6-02
 */
@SuppressWarnings("serial")
public abstract class HttpServletBean extends HttpServlet {

    private Logger log = LogFactory.getLogger(HttpServletBean.class);

    protected WebApplicationContext webApplicationContext;

    protected WebContainerDefaultServlet defaultServlet;

    protected String encoding;

    protected HandlerMapping handlerMapping;
    
    private ContextLoader contextLoader;

    @Override
    public final void init() throws ServletException {

        log.debug("Initializing servlet '" + getServletName() + "'");

        if (webApplicationContext == null) {
            webApplicationContext = WebApplicationContextUtils
                    .getWebApplicationContext(getServletContext());
            if (webApplicationContext == null) {
                contextLoader = new ContextLoader();
                contextLoader.initWebApplicationContext(getServletContext());
                webApplicationContext = WebApplicationContextUtils
                        .getWebApplicationContext(getServletContext());
            }
        }

        defaultServlet = new WebContainerDefaultServlet(getServletContext());

        // ConfigReader.getInstance().getConfig().getEncoding();
        encoding = webApplicationContext.getHandlerMapping().getEncoding();

        initDispatcher();

        finalInit();

        log.debug("Servlet '" + getServletName() + "' configured successfully");
    }

    private void initDispatcher() {

        handlerMapping = webApplicationContext.getHandlerMapping();
    }

    /**
     * at the last of initialization operation, do something.
     */
    abstract public void finalInit();
    
    @Override
    public void destroy() {
        ServletContext ctx = getServletContext();
        if (contextLoader != null) {
            ctx.log("================= zolltyMVC contextDestroyed ======================");
            contextLoader.close();
        }
    }

}