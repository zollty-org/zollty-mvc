/* 
 * Copyright (C) 2013-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by Zollty Tsou on 2013-6-3 [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.view;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.handler.support.ErrorHandler;
import org.zollty.framework.util.MvcUtils;
import org.zollty.tool.web.ServletFileDownload;

/**
 * @author zollty
 * @since 2014-6-3
 */
abstract public class AbstractStaticResourceView implements View {
    
    private final String shortPath;

    public AbstractStaticResourceView(String shortPath) {
        this.shortPath = shortPath;
    }
    
    /**
     * 资源视图路径的前缀，例如/resources/、classpath:/META-INF/resources/
     */
    abstract public String getViewPathPrefix();

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InputStream in = null;
        try{
            in = MvcUtils.ResourceUtil.getResourceInputStream(getViewPathPrefix()+shortPath, null, null);
        }catch (Exception e) {
            new ErrorHandler(null, request.getRequestURI() + " not found",
                    HttpServletResponse.SC_NOT_FOUND).render(request, response);
            return;
        }
        if(in!=null){
            String contentType = MvcUtils.StringUtil.getFilenameExtension(shortPath);
            contentType = ServletFileDownload.MIME.get(contentType);
            if(contentType!=null) {
                response.setHeader("Content-Type", contentType);
            }
            MvcUtils.IOUtil.clone(in, response.getOutputStream());
        }
    }


}
