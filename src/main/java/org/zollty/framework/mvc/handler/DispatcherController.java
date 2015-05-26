/* @(#)DispatcherController.java 
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
package org.zollty.framework.mvc.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.handler.support.ErrorHandler;
import org.zollty.framework.mvc.servlet.HttpServletBean;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-6-02
 */
@SuppressWarnings("serial")
abstract public class DispatcherController extends HttpServletBean {

    private Logger log = LogFactory.getLogger(DispatcherController.class);

    abstract public void dispatcher(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException;

    /**
     * 前端控制器，处理http请求到相应View
     * 
     * @param request HttpServletRequest对象
     * @param response HttpServletResponse对象
     */
    public void handleRequest(String servletURI, HttpServletRequest request, HttpServletResponse response) {

        try {
            request.setCharacterEncoding(encoding);
        }
        catch (Throwable t) {
            log.error(t, "dispatcher error");
        }
        response.setCharacterEncoding(encoding);

//        HandlerChainImpl chain = handlerMapping.match(servletURI, request);
//
//        if (chain.getHandlerSize() == 0) { // 没有找到处理器 404
//            new ErrorHandler(null, request.getRequestURI() + " not found", HttpServletResponse.SC_NOT_FOUND).render(
//                    request, response);
//            return;
//        }
        WebHandler handler = handlerMapping.match(servletURI, request);

        if (handler == null) { // 没有找到处理器 404
            new ErrorHandler(null, request.getRequestURI() + " not found", HttpServletResponse.SC_NOT_FOUND).render(
                    request, response);
            return;
        }
        
        // 执行对应的方法，返回方法指定的View
//        View v = chain.doNext(request, response, chain);
        View v = handler.invoke(request, response);
        // 返回View为null，此时一般是在该方法内，直接采用了response返回，故需要判断response是否提交，若没提交，则认为是出错了
        if (v == null) {
            if (!response.isCommitted()) {
                new ErrorHandler(null, "Server internal error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR).render(
                        request, response);
            }
            return;
        }

        try {
            v.render(request, response);
        }
        catch (Throwable t) {
            new ErrorHandler(t, "dispatcher error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR).render(request,
                    response);
            return;
        }
    }

    
    // /////////////Override the HttpServlet's methods////////////////

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatcher(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatcher(request, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatcher(request, response);

    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatcher(request, response);
    }

}
