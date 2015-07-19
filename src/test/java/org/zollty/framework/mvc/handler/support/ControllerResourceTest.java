/* 
 * Copyright (C) 2012-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Zollty Framework MVC Source Code - Since v1.1
 * Author(s): 
 * Zollty Tsou (zolltytsou@gmail.com, http://blog.zollty.com)
 */
package org.zollty.framework.mvc.handler.support;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.zollty.framework.mvc.handler.ControllerViewHandler;
import org.zollty.framework.mvc.support.ControllerMetaInfo;
import org.zollty.framework.util.MvcUtils;

/**
 * @author zollty
 * @since 2014-6-2
 */
public class ControllerResourceTest {

    @Test
    public void testURIMapping1() {
        // DATA
        String uriPattern = "/mlf4j/{p1}/{p2}/{v1}";
        String servletURI = "/mlf4j/vf/logConfig/uu";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("p1", "vf");
        params.put("p2", "logConfig");
        params.put("v1", "uu");

        runURIMapping(uriPattern, servletURI, params);
    }

    private static void runURIMapping(String uriPattern, String servletURI, HashMap<String, String> params) {
        Method me = MvcUtils.ReflectUtil.findMethod(Pattern.class, "compile");
        ControllerMetaInfo cm = new ControllerMetaInfo(null, me, new String[] { "GET" }, uriPattern, null);
        ControllerResource cr = new ControllerResource();
        cr.addController(cm);
        ControllerViewHandler ha = (ControllerViewHandler) cr.getHandler(servletURI, new FakeHttpServletRequest("GET"));
        assertNotNull(ha);
        assertEquals(ha.getParamsMap(), params);
    }

    @Test
    public void testParseUriPathVariable() {
        String uri = "as{ak}sa/v{1}/[c8]-[mk]/{v2}";
        String[] keys = new String[]{"ak", "1", "c8", "mk", "v2"};

        List<String> list = ControllerResource.parseUriPathVariable(uri);
        assertArrayEquals(list.toArray(new String[0]), keys);
    }
    
    
    private static class FakeHttpServletRequest implements HttpServletRequest {
        
        private String method;
        
        public FakeHttpServletRequest(String method){
            this.method = method;
        }
        
        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        }
        
        @Override
        public void setAttribute(String name, Object o) {
        }
        
        @Override
        public void removeAttribute(String name) {
        }
        
        @Override
        public boolean isSecure() {
            return false;
        }
        
        @Override
        public int getServerPort() {
            return 0;
        }
        
        @Override
        public String getServerName() {
            return null;
        }
        
        @Override
        public String getScheme() {
            return null;
        }
        
        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }
        
        @Override
        public int getRemotePort() {
            return 0;
        }
        
        @Override
        public String getRemoteHost() {
            return null;
        }
        
        @Override
        public String getRemoteAddr() {
            return null;
        }
        
        @Override
        public String getRealPath(String path) {
            return null;
        }
        
        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }
        
        @Override
        public String getProtocol() {
            return null;
        }
        
        @Override
        public String[] getParameterValues(String name) {
            return null;
        }
        
        @Override
        public Enumeration getParameterNames() {
            return null;
        }
        
        @Override
        public Map getParameterMap() {
            return null;
        }
        
        @Override
        public String getParameter(String name) {
            return null;
        }
        
        @Override
        public Enumeration getLocales() {
            return null;
        }
        
        @Override
        public Locale getLocale() {
            return null;
        }
        
        @Override
        public int getLocalPort() {
            return 0;
        }
        
        @Override
        public String getLocalName() {
            return null;
        }
        
        @Override
        public String getLocalAddr() {
            return null;
        }
        
        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }
        
        @Override
        public String getContentType() {
            return null;
        }
        
        @Override
        public int getContentLength() {
            return 0;
        }
        
        @Override
        public String getCharacterEncoding() {
            return null;
        }
        
        @Override
        public Enumeration getAttributeNames() {
            return null;
        }
        
        @Override
        public Object getAttribute(String name) {
            return null;
        }
        
        @Override
        public boolean isUserInRole(String role) {
            return false;
        }
        
        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }
        
        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }
        
        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }
        
        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }
        
        @Override
        public Principal getUserPrincipal() {
            return null;
        }
        
        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }
        
        @Override
        public HttpSession getSession() {
            return null;
        }
        
        @Override
        public String getServletPath() {
            return null;
        }
        
        @Override
        public String getRequestedSessionId() {
            return null;
        }
        
        @Override
        public StringBuffer getRequestURL() {
            return null;
        }
        
        @Override
        public String getRequestURI() {
            return null;
        }
        
        @Override
        public String getRemoteUser() {
            return null;
        }
        
        @Override
        public String getQueryString() {
            return null;
        }
        
        @Override
        public String getPathTranslated() {
            return null;
        }
        
        @Override
        public String getPathInfo() {
            return null;
        }
        
        @Override
        public String getMethod() {
            return method;
        }
        
        @Override
        public int getIntHeader(String name) {
            return 0;
        }
        
        @Override
        public Enumeration getHeaders(String name) {
            return null;
        }
        
        @Override
        public Enumeration getHeaderNames() {
            return null;
        }
        
        @Override
        public String getHeader(String name) {
            return null;
        }
        
        @Override
        public long getDateHeader(String name) {
            return 0;
        }
        
        @Override
        public Cookie[] getCookies() {
            return null;
        }
        
        @Override
        public String getContextPath() {
            return null;
        }
        
        @Override
        public String getAuthType() {
            return null;
        }
    }

}
