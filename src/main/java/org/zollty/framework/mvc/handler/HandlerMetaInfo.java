/* @(#)HandlerMetaInfo.java 
 * Copyright (C) 2013-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by zollty on 2013-9-16 [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.handler.support.ErrorHandler;

/**
 * @author zollty 
 * @since 2013-9-16
 */
public abstract class HandlerMetaInfo {
	
	protected final Object object; // controller的实例对象
	protected final Method method; // 请求uri对应的方法
	protected final byte[] paramType; // 请求方法参数类型
	
	public HandlerMetaInfo(Object object, Method method) {
		this.object = object;
		this.method = method;
		this.paramType = new byte[method.getParameterTypes().length];
	}
	
	public final View invokeMethod(Object[] args, HttpServletRequest request, HttpServletResponse response) {
		View ret = null;
		try {
			ret = (View)method.invoke(object, args);
		} catch (java.lang.ClassCastException e){
            return new ErrorHandler(null, "handler invoke error: "+e.getMessage(), 
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR).doErrorPage(request, response);
		} catch (Throwable e) {
			//if(log.isInfoEnabled()) log.error(((InvocationTargetException) e).getTargetException(),"handler invoke error");
			//throw new RuntimeException("handler invoke error: "+getExceptionMsg(e));
			return new ErrorHandler(((InvocationTargetException) e).getTargetException(), "handler invoke error", 
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR).doErrorPage(request, response);
		} 
		return ret;
	}
	
    public byte[] getParamType() {
        return paramType;
    }

	@Override
	public String toString() {
		return "HandlerMetaInfo [method=" + method + ", paramType="+ Arrays.toString(paramType) + "]";
	}

}
