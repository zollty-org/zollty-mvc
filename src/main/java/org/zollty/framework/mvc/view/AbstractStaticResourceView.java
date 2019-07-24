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
 * Create by ZollTy on 2014-6-03 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.context.ContextLoader;
import org.zollty.framework.util.MvcUtils;

/**
 * @author zollty
 * @since 2014-6-03
 */
abstract public class AbstractStaticResourceView implements View {

    protected static final String VIEW_PATH_PREFIX = "classpath:/META-INF/resources/";
    
    private final String shortPath;

    public AbstractStaticResourceView(String shortPath) {
        this.shortPath = shortPath;
    }

    /**
     * 资源视图路径的前缀，例如/resources/、classpath:/META-INF/resources/
     */
    abstract public String getViewPathPrefix();

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        InputStream in = null;
        try {
            in = MvcUtils.ResourceUtil.getResourceInputStream(getViewPathPrefix() + shortPath,
                    null, ContextLoader.getCurrentWebApplicationContext().getServletContext());
        }
        catch (Exception e) {
            new ErrorView(HttpServletResponse.SC_NOT_FOUND, null, request.getRequestURI()
                    + " not found.").render(request, response);
            return;
        }
        if (in != null) {
            String contentType = MvcUtils.StringUtil.getFilenameExtension(shortPath);
            contentType = MIME.get(contentType);
            if (contentType != null) {
                response.setHeader("Content-Type", contentType);
            }
            MvcUtils.IOUtil.clone(in, response.getOutputStream());
        }
    }
    
    private static final Map<String, String> MIME = new HashMap<String, String>();
    static {
        // MIME类型
        MIME.put("csv", "text/comma-separated-values");
        MIME.put("xls", "application/x-msexcel");
        MIME.put("xlsx", "application/x-msexcel");
        MIME.put("txt", "text/plain");
        MIME.put("htm", "text/html");
        MIME.put("html", "text/html");
        MIME.put("htmls", "text/html");
        // MIME.put("zip", "application/x-compressed");
        MIME.put("zip", "application/x-zip-compressed");
        // MIME.put("zip", "application/zip");
        // MIME.put("zip", "multipart/x-zip");
        MIME.put("gz", "application/x-gzip");
        MIME.put("rar", "application/x-rar-compressed");

        MIME.put("doc", "application/msword");
        MIME.put("docx", "application/msword");
        MIME.put("wps", "application/application/vnd.ms-works");
        MIME.put("ppt", "application/application/vnd.ms-powerpoint");

        MIME.put("pdf", "application/pdf");
        MIME.put("jpe", "image/jpeg");
        MIME.put("jpeg", "image/jpeg");
        MIME.put("jpg", "image/jpeg");
        MIME.put("bmp", "image/bmp");
        MIME.put("png", "image/png");
        MIME.put("js", "application/x-javascript");
        // MIME.put("js", "application/x-ns-proxy-autoconfig");

        // MIME.put("rar", "application/rar");

        MIME.put("css", "text/css");
        MIME.put("avi", "video/msvideo");
        MIME.put("mp3", "audio/mp3");
        MIME.put("mp4", "video/mp4");

        MIME.put("jar", "application/x-java-archive");
        MIME.put("java", "text/x-java-source");
        MIME.put("class", "application/octet-stream");
        MIME.put("exe", "application/octet-stream");
    }

}