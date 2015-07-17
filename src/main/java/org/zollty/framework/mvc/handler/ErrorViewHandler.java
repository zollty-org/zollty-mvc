/* @(#)ErrorHandler.java 
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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.ViewHandler;
import org.zollty.framework.mvc.view.JspView;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-9-16
 */
public class ErrorViewHandler implements ViewHandler {

    private static Logger log = LogFactory.getLogger(ErrorViewHandler.class);

    private static String errorPage;

    protected Throwable e;
    protected String msg;
    protected int status = -1;

    public ErrorViewHandler() {
    }

    public ErrorViewHandler(String msg) {
        this.msg = msg;
    }

    public ErrorViewHandler(Throwable e, String msg) {
        this.e = e;
        this.msg = msg;
    }

    public ErrorViewHandler(Throwable e, String msg, int status) {
        this.e = e;
        this.msg = msg;
        this.status = status;
    }

    @Override
    public View invoke(HttpServletRequest request, HttpServletResponse response) {
        return doErrorPage(request, response);
    }

    /**
     * 提交错误页面视图
     */
    public void renderView(HttpServletRequest request, HttpServletResponse response) {
        try {
            View v = doErrorPage(request, response);
            if (null != v)
                v.render(request, response);
        }
        catch (Throwable e) {
            log.error(e, "the last invoke failed! ");
            try {
                response.sendError(status);
            }
            catch (IOException e1) {
                log.debug(e1, "response.sendError() failed! ");
            }
        }
    }

    /**
     * 处理错误页面视图，提交defaultErrorPage或者返回指定的错误页面视图对象
     */
    public View doErrorPage(HttpServletRequest request, HttpServletResponse response) {
        if (status != -1) {
            response.setStatus(status);
            // 不标记http的错误信息。也就是说返回的不管是什么status，都是success的
            // try { response.sendError(status);
            // } catch (IOException e1) { log.debug("response.sendError() failed! ",e1); }
        }

        boolean isAjaxRequest = MvcUtils.WebUtil.isAjaxRequest(request);
        if (!isAjaxRequest && errorPage != null) {
            request.setAttribute("e", e);
            request.setAttribute("msg", msg);
            return new JspView(errorPage);
        }

        // 记录错误信息
        if (log.isInfoEnabled())
            log.error("ErrorMsg: " + msg + ", StackTrace: " + MvcUtils.ExceptionUtil.getStackTraceStr(e));
        PrintWriter writer = null;
        try {
            try {
                writer = response.getWriter();
            }
            catch (Throwable t) {
                log.error(t);
                return null;
            }
            if (isAjaxRequest) {
                StringBuilder sb = new StringBuilder();
                sb.append("HTTP ERROR ");
                sb.append(status == -1 ? "" : Integer.toString(status));
                sb.append(" ");
                if (MvcUtils.StringUtil.isNotBlank(msg)) {
                    sb.append(msg);
                }
                else {
                    if (e != null) {
                        String em = e.getMessage();
                        if (em != null && em.length() > 10) {
                            sb.append(em);
                        }
                        else {
                            sb.append(MvcUtils.ExceptionUtil.getExceptionProfile(e, 200));
                        }
                    }
                }
                writer.print(sb.toString());
            }
            else {
                response.setHeader("Content-Type", "text/html;charset=UTF-8");
                writer.print(defaultErrorPage());
            }
        }
        finally {
            if (writer != null)
                writer.close();
        }

        return null;

    }

    
    private String defaultErrorPage() {
        StringBuilder ret = new StringBuilder();
        try {
            ret.append("<!DOCTYPE html><html><head><title>Server Error ")
                    .append(status == -1 ? "" : Integer.toString(status))
                    .append("</title></head><body>")
                    .append("<h2>HTTP ERROR ")
                    .append(status == -1 ? "" : Integer.toString(status))
                    .append("</h2><div>")
                    .append(MvcUtils.StringUtil.simpleHtmlEscape(URLDecoder.decode(
                            MvcUtils.StringUtil.isNotBlank(msg) ? msg : (MvcUtils.ExceptionUtil.getStackTraceStr(e)),
                            "UTF-8")))
                    .append("</div><hr/><div style=\"font-style: italic; font-family: Baskerville, 'Group Old Style', Palatino, 'Book Antiqua', serif;\"><small>Created-By: ZolltyMVC Framework ")
                    .append(MvcUtils.DATEFORMAT.format(new Date())).append("</small></div></body></html>");
        }
        catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        return ret.toString();
    }

    public static String getErrorPage() {
        return errorPage;
    }

    public static void setErrorPage(String errorPage) {
        ErrorViewHandler.errorPage = errorPage;
    }

}