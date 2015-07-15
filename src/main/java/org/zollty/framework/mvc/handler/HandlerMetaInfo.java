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
 * Create by ZollTy on 2013-9-16 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.aop.MvcContext;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.handler.support.ErrorHandler;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-9-16
 */
public abstract class HandlerMetaInfo {
    
    private Logger log = LogFactory.getLogger(HandlerMetaInfo.class);

    protected final Object object; // controller的实例对象
    protected final Method method; // 请求uri对应的方法
    protected final byte[] paramType; // 请求方法参数类型
    protected MvcBeforeBeanDefinition[] beforeInvoke; //

    public HandlerMetaInfo(Object object, Method method) {
        this.object = object;
        this.method = method;
        this.paramType = new byte[method.getParameterTypes().length];
    }

    private View doBefore(HttpServletRequest request, HttpServletResponse response,
            MvcContext mvcContext) {
        if (beforeInvoke != null && beforeInvoke.length > 0) {
            for (MvcBeforeBeanDefinition mbd : beforeInvoke) {
                if (log.isTraceEnabled()) {
                    log.trace("[Execute AOP Before Method] - {}", mbd.getClassName());
                }
                Method mt = mbd.getDisposeMethod();
                try {
                    mt.invoke(mbd.getObject(), new Object[] { mvcContext });
                }
                catch (ClassCastException e) {
                    return new ErrorHandler(null, "AOP Before Method invoke error: "
                            + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                            .doErrorPage(request, response);
                }
                catch (InvocationTargetException e) {
                    return new ErrorHandler(e.getTargetException(),
                            "AOP Before Method invoke error",
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR).doErrorPage(request,
                            response);
                }
                catch (Throwable e) {
                    return new ErrorHandler(e, "AOP Before Method invoke error",
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR).doErrorPage(request,
                            response);
                }
            }
        }
        return null;
    }

    private View doBeforeRender(HttpServletRequest request, HttpServletResponse response,
            MvcContext mvcContext) {
        return null;
    }

    private View doAfterThrowing(HttpServletRequest request, HttpServletResponse response,
            MvcContext mvcContext) {
        return null;
    }

    public final View invokeMethod(InvokeParamsAdapter paramsHolder, HttpServletRequest request,
            HttpServletResponse response) {

        MvcContext mvcContext = new MvcContext(request);

        View view = doBefore(request, response, mvcContext);
        if (view != null) {
            return view;
        }

        try {
            Object[] args = paramsHolder.getInvokeParams(request, response);
            view = (View) method.invoke(object, args);
        }
        catch (ClassCastException e) {
            View v = doAfterThrowing(request, response, mvcContext);
            if (v != null) {
                return v;
            }
            return new ErrorHandler(null, "handler invoke error: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR).doErrorPage(request, response);
        }
        catch (InvocationTargetException e) {
            View v = doAfterThrowing(request, response, mvcContext);
            if (v != null) {
                return v;
            }
            return new ErrorHandler(e.getTargetException(), "handler invoke error",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR).doErrorPage(request, response);
        }
        catch (Throwable e) {
            View v = doAfterThrowing(request, response, mvcContext);
            if (v != null) {
                return v;
            }
            return new ErrorHandler(e, "handler invoke error",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR).doErrorPage(request, response);
        }

        View v = doBeforeRender(request, response, mvcContext);
        if (v != null) {
            return v;
        }

        return view;
    }

    public byte[] getParamType() {
        return paramType;
    }

    @Override
    public String toString() {
        return "HandlerMetaInfo [method=" + method + ", paramType=" + Arrays.toString(paramType) + "]";
    }

}