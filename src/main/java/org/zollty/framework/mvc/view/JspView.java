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
 * Create by ZollTy on 2013-8-05 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;

/**
 * @author zollty
 * @since 2013-8-05
 */
public class JspView implements View {

    private static String viewPath;
    private final String shortPagePath;
    private final boolean enableMainPath;

    public JspView(String shortPagePath) {
        this.shortPagePath = shortPagePath;
        this.enableMainPath = true;
    }

    public JspView(String shortPagePath, boolean enableMainPath) {
        this.shortPagePath = shortPagePath;
        this.enableMainPath = enableMainPath;
    }

    public static void setViewPath(String path) {
        if (JspView.viewPath == null && path != null)
            JspView.viewPath = path;
    }

    public String getShortPagePath() {
        return shortPagePath;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (enableMainPath) {
            request.getRequestDispatcher(JspView.viewPath + shortPagePath).forward(request,
                    response);
        }
        else {
            request.getRequestDispatcher(shortPagePath).forward(request, response);
        }
    }

}