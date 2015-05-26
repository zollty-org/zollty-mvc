/* @(#)HttpServletBean.java 
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
package org.zollty.framework.mvc.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.zollty.framework.mvc.context.ContextLoader;
import org.zollty.framework.mvc.context.WebApplicationContext;
import org.zollty.framework.mvc.context.WebApplicationContextUtils;
import org.zollty.framework.mvc.context.support.WebAnnotationAndXmlApplicationContext;
import org.zollty.framework.mvc.handler.HandlerMapping;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-6-02
 */
@SuppressWarnings("serial")
abstract public class HttpServletBean extends HttpServlet {

    private Logger log = LogFactory.getLogger(HttpServletBean.class);

    protected WebApplicationContext webApplicationContext;

    protected WebContainerDefaultServlet defaultServlet;

    protected String encoding;

    protected HandlerMapping handlerMapping;

    @Override
    public final void init() throws ServletException {

        log.debug("Initializing servlet '" + getServletName() + "'");

        if (webApplicationContext == null) {
            webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            if (webApplicationContext == null) {
                new ContextLoader().initWebApplicationContext(getServletContext());
                webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
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

        WebAnnotationAndXmlApplicationContext awac = (WebAnnotationAndXmlApplicationContext) webApplicationContext;

        handlerMapping = awac.getHandlerMapping();
    }

    /**
     * at the last of initialization operation, do something.
     */
    abstract public void finalInit();

}