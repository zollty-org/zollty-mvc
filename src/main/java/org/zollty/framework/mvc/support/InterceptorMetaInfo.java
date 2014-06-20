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
 * Create by Zollty Tsou [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.support;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.handler.HandlerChain;
import org.zollty.framework.mvc.handler.HandlerMetaInfo;
import org.zollty.framework.mvc.handler.InterceptorInfo;
import org.zollty.framework.util.match.AntPathMatcher;
import org.zollty.framework.util.match.PathMatcher;

/**
 * @author zollty 
 * @since 2013-9-21
 */
public class InterceptorMetaInfo extends HandlerMetaInfo implements Comparable<InterceptorMetaInfo> {
	
    private final String[] uriPattern;
	private final Integer order;

	public InterceptorMetaInfo(Object object, Method method, String[] uriPattern, int order) {
		super(object, method);
		Class<?>[] paraTypes = method.getParameterTypes();
		
		for (int i = 0; i < paraTypes.length; i++) {
			if (paraTypes[i].equals(HttpServletRequest.class))
			    paramType[i] = BasicParamMetaInfo.REQUEST;
			else if (paraTypes[i].equals(HttpServletResponse.class))
			    paramType[i] = BasicParamMetaInfo.RESPONSE;
			else if (paraTypes[i].equals(HandlerChain.class))
			    paramType[i] = BasicParamMetaInfo.HANDLER_CHAIN;
			else if (paraTypes[i].equals(InterceptorInfo.class))
			    paramType[i] = BasicParamMetaInfo.INTERCEPTOR_INFO;
		}
		this.uriPattern = uriPattern;
        // pattern = new ArrayList<Pattern>();
        // for(String uri: uriPattern){
        // pattern.add(Pattern.compile(uri, "*"));
        // }
		this.order = order;
	}
	
	private static PathMatcher pm = new AntPathMatcher();
    public boolean match(String servletURI) {
        boolean ret = false;
        for (String pa: uriPattern) {
            if (pm.match(pa, servletURI)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    // public boolean match(String servletURI) {
    // boolean ret = false;
    // for (Pattern p : pattern) {
    // if (p.match(servletURI) != null) {
    // ret = true;
    // break;
    // }
    // }
    // return ret;
    // }
	
   public String[] getUriPattern() {
        return uriPattern;
    }

	public Integer getOrder() {
		return order;
	}

	@Override
	public int compareTo(InterceptorMetaInfo o) {
		return order.compareTo(o.order);
	}
	
    @Override
    public String toString() {
        return "InterceptorMetaInfo [uriPattern=" + Arrays.toString(uriPattern) + ", order=" + order + "]";
    }
	
}
