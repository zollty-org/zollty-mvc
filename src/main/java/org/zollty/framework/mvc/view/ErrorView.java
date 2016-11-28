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
 * Create by ZollTy on 2015-7-23 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.util.MvcUtils;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.json.SimpleJSON;

/**
 * 
 * @author zollty
 * @since 2015-7-23
 */
public class ErrorView implements View {
    
    private static final Logger LOG = LogFactory.getLogger(ErrorView.class);

    private static String errorPage;

    protected Throwable e;
    protected String msg;
    protected int status = -1;
    
    public static final String HTTP_ERR_500_INNER_ERR = "Server internal error.";
    public static final String HTTP_ERR_500_RESPONSE_NOT_COMMIT = "Server internal error. Response not commit.";
    public static final String HTTP_ERR_500_RENDER_ERR = "View render error.";
    public static final String HTTP_ERR_500_CTRLLER_ERR = "Controller Method invoke error.";
    public static final String HTTP_ERR_500_BEFORE_ERR = "AOP Before Method invoke error.";
    
    /**
     * @param status HTTP错误代码
     * @param e 要记录日志的异常
     * @param msg 要显示在页面上的内容
     */
    public ErrorView(int status, Throwable e, String msg) {
        this.status = status;
        this.e = e;
        this.msg = msg;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) {
        
        try {
            View v = getErrorView(request, response);
            if (null != v)
                v.render(request, response);
        }
        catch (Throwable e) {
            LOG.error(e, "the last invoke failed! ");
            try {
                response.sendError(status);
            }
            catch (IOException e1) {
                LOG.debug(e1, "response.sendError() failed! ");
            }
        }

    }
    
    /**
     * 返回指定的错误页面视图对象
     */
    private View getErrorView(HttpServletRequest request, HttpServletResponse response) {
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
        
        if (isAjaxRequest) {
            return new JsonView( defaultJsonErrorInfo() );
        }
        
        return new HtmlView( defaultErrorPage() );
    }
    
    

    // ~---------------------------------
    
    /** 形如 {"errorCode":"z-ds7eq","errorMsg":"HTTP ERROR 500 java.lang.ArithmeticException: / by zero"} */
    private String defaultJsonErrorInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP ERROR ");
        sb.append(status == -1 ? "" : Integer.toString(status));
        if (MvcUtils.StringUtil.isNotEmpty(msg)) {
            sb.append(" ");
            sb.append(msg);
        }
        if (e != null) {
            sb.append(" ");
            sb.append(MvcUtils.ExceptionUtil.getExceptionProfile(e, 200));
        }
        String errorCode = "z-" + MvcUtils.RandomUtil.getRadomStr09az(5);
        // 记录错误信息
        // [MVC ERROR]: [z-ds7eq] Controller Method invoke error. java.lang.ArithmeticException: / by zero
        if ( status==HttpServletResponse.SC_INTERNAL_SERVER_ERROR && LOG.isDebugEnabled()) {
            LOG.error("[MVC ERROR]: [{}] {} {}", errorCode, msg, MvcUtils.ExceptionUtil.getStackTraceStr(e));
        }

        return new SimpleJSON().addItem("errorCode", errorCode).addItem("errorMsg", sb.toString()).toString();
    }
    
    
    private String defaultErrorPage() {

        String errorCode = null;
        // 记录错误信息
        // [MVC ERROR]: [z-ds7eq] Controller Method invoke error. java.lang.ArithmeticException: / by zero
        if (status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR && LOG.isDebugEnabled()) {
            errorCode = "z-" + MvcUtils.RandomUtil.getRadomStr09az(5);
            LOG.error("[MVC ERROR]: [{}] {} {}", errorCode, msg,
                    MvcUtils.ExceptionUtil.getStackTraceStr(e));
        }

        StringBuilder ret = new StringBuilder();
        try {
            ret.append("<!DOCTYPE html><html><head><title>Server Error ")
                    .append(status == -1 ? "" : Integer.toString(status))
                    .append("</title></head><body>").append("<h2>HTTP ERROR ")
                    .append(status == -1 ? "" : Integer.toString(status)).append("</h2><div>");

            if (MvcUtils.StringUtil.isNotEmpty(msg)) {
                ret.append(MvcUtils.StringUtil.simpleHtmlEscape(URLDecoder.decode(msg, "UTF-8")));
            }
            if (e != null) {
                ret.append("&nbsp;(See: <a href=\"javascript:alert('")
                        .append(MvcUtils.ExceptionUtil.getExceptionProfile(e, 200).replaceAll("'", "\\\\\'").replaceAll("\"", "\\\\\'"))
                        .append("');\" title=\"The Error Code\">")
                        .append(errorCode != null ? errorCode : Integer.toString(status))
                        .append("</a>)");
            }

            ret.append("</div><hr/>")
                    .append("<div style=\"font-style: italic; font-family: Baskerville, 'Group Old Style', Palatino, 'Book Antiqua', serif;\">")
                    .append("<small>Created-By: ZolltyMVC Framework ")
                    .append(MvcUtils.DATEFORMAT.format(new Date()))
                    .append("</small></div></body></html>");
        }
        catch (UnsupportedEncodingException e) {
            LOG.error(e);
        }

        return ret.toString();
    }

    public static String getErrorPage() {
        return errorPage;
    }

    public static void setErrorPage(String errorPage) {
        ErrorView.errorPage = errorPage;
    }

}
