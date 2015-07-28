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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.ViewHandler;
import org.zollty.framework.mvc.view.ErrorView;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-6-02
 */
@SuppressWarnings("serial")
abstract public class HttpRequestHandler extends HttpServletBean {

    private Logger log = LogFactory.getLogger(HttpRequestHandler.class);

    abstract public void dispatcher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    /**
     * 前端控制器，处理http请求到相应View
     */
    public void handleRequest(String servletURI, HttpServletRequest request,
            HttpServletResponse response) {

        try {
            request.setCharacterEncoding(encoding);
        }
        catch (Throwable t) {
            log.error(t, "dispatcher error");
        }
        
        response.setCharacterEncoding(encoding);

        ViewHandler handler = handlerMapping.match(servletURI, request);

        if (handler == null) { // 没有找到处理器 404
            new ErrorView(HttpServletResponse.SC_NOT_FOUND, null, request.getRequestURI()
                    + " not found.").render(request, response);;
            return;
        }

        // 执行对应的方法，返回方法指定的View
        View v = handler.getView(request, response);
        // 返回View为null，此时一般是在该方法内，直接采用了response返回，故需要判断response是否提交，若没提交，则认为是出错了
        if (v == null) {
            if (!response.isCommitted()) {
                new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null,
                        ErrorView.HTTP_ERR_500_RESPONSE_NOT_COMMIT).render(request, response);
            }
            handler.doAtfer(request, response);
            return;
        }
        
        try {
            v.render(request, response);
        }
        catch (Throwable t) {
            new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t,
                    ErrorView.HTTP_ERR_500_RENDER_ERR).render(request, response);
        }
        
        handler.doAtfer(request, response);
        
    }
    

    // ------------Override the HttpServlet's methods ---------------------

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        dispatcher(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        dispatcher(request, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        dispatcher(request, response);

    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        dispatcher(request, response);
    }

}