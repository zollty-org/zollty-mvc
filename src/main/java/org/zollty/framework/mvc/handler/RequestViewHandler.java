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
package org.zollty.framework.mvc.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.core.Const;
import org.zollty.framework.core.Const.ControllerMethodParamType;
import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.ViewHandler;
import org.zollty.framework.mvc.aop.MvcJoinPoint;
import org.zollty.framework.mvc.aop.bean.AopBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAfterBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAfterThrowBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAroundBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeRenderBeanDefinition;
import org.zollty.framework.mvc.view.ErrorView;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.BasicRuntimeException;

/**
 * 
 * @author zollty
 * @since 2015-7-23
 */
public class RequestViewHandler implements ViewHandler, ViewHandlerAopSupport {

    private static final Logger LOG = LogFactory.getLogger(RequestViewHandler.class);

    private ControllerMeta meta;
    private Map<String, String> paramsMap;

    List<AopBeanDefinition> aopIntercList = new ArrayList<AopBeanDefinition>();

    List<MvcBeforeBeanDefinition> beforeIntercList;
    List<MvcAroundBeanDefinition> aroundIntercList;
    List<MvcBeforeRenderBeanDefinition> beforeRenderIntercList;
    List<MvcAfterThrowBeanDefinition> afterThrowIntercList;
    List<MvcAfterBeanDefinition> afterIntercList;

    public RequestViewHandler(ControllerMeta meta) {
        // Check paramType
        ControllerMethodParamType[] paramType = meta.getParamType();
        for (ControllerMethodParamType type : paramType) {
            if (type == Const.ControllerMethodParamType.URIParam) {
                throw new BasicRuntimeException("the common meta can't use URIParam!");
            }
        }
        this.meta = meta;
    }

    public RequestViewHandler(ControllerMeta meta, Map<String, String> paramsMap) {
        this.meta = meta;
        this.paramsMap = paramsMap;
    }

    @Override
    public View getView(HttpServletRequest request, HttpServletResponse response) {

        if (aopIntercList != null) {
            beforeIntercList = new ArrayList<MvcBeforeBeanDefinition>();
            aroundIntercList = new ArrayList<MvcAroundBeanDefinition>();
            beforeRenderIntercList = new ArrayList<MvcBeforeRenderBeanDefinition>();
            afterThrowIntercList = new ArrayList<MvcAfterThrowBeanDefinition>();
            afterIntercList = new ArrayList<MvcAfterBeanDefinition>();

            for (AopBeanDefinition beanDef : aopIntercList) {
                if (beanDef instanceof MvcBeforeBeanDefinition) {
                    beforeIntercList.add((MvcBeforeBeanDefinition) beanDef);
                }
                else if (beanDef instanceof MvcAroundBeanDefinition) {
                    aroundIntercList.add((MvcAroundBeanDefinition) beanDef);
                }
                else if (beanDef instanceof MvcBeforeRenderBeanDefinition) {
                    beforeRenderIntercList.add((MvcBeforeRenderBeanDefinition) beanDef);
                }
                else if (beanDef instanceof MvcAfterThrowBeanDefinition) {
                    afterThrowIntercList.add((MvcAfterThrowBeanDefinition) beanDef);
                }
                else if (beanDef instanceof MvcAfterBeanDefinition) {
                    afterIntercList.add((MvcAfterBeanDefinition) beanDef);
                }
            }
            Collections.reverse(aroundIntercList);
        }

        return invokeMethod(request, response);
    }

    public ControllerMeta getMeta() {
        return meta;
    }

    public Map<String, String> getParamsMap() {
        return paramsMap;
    }

    @Override
    public void setAopIntercs(List<AopBeanDefinition> aopIntercList) {
        if (MvcUtils.CollectionUtil.isNotEmpty(aopIntercList)) {
            this.aopIntercList = aopIntercList;
        }
    }

    private View invokeMethod(HttpServletRequest request, HttpServletResponse response) {

        View view = doBefore(request, response);
        if (view != null) {
            return view;
        }

        List<MvcAroundBeanDefinition> tempIntercs = null;
        if (MvcUtils.CollectionUtil.isNotEmpty(aroundIntercList)) {
            // aroundIntercList 后执行
            tempIntercs = new ArrayList<MvcAroundBeanDefinition>(meta.getAroundIntercList());
            tempIntercs.addAll(aroundIntercList);
        }
        else {
            tempIntercs = meta.getAroundIntercList();
        }

        try {
            Object[] args = this.getInvokeParams(request, response);
            if (MvcUtils.CollectionUtil.isNotEmpty(tempIntercs)) {

                int pos = MvcUtils.CollectionUtil.checkDuplication(tempIntercs);
                if (pos != -1) {
                    LOG.warn("\"{}\" in [{}] is duplicate~! Framework auto removed the last one.",
                            tempIntercs.get(pos), meta.getObject().getClass());
                    tempIntercs.remove(pos);
                }

                view = doAround(tempIntercs, request, response, args, tempIntercs.size());
            }
            else {
                view = (View) meta.getMethod().invoke(meta.getObject(), args);
            }

        }
        // catch (ClassCastException e) {
        // View v = doAfterThrow(request, response, mvcContext);
        // if (v != null) {
        // return v;
        // }
        // return new ErrorViewHandler(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        // new NestedCheckedException(e.getMessage()),
        // ErrorViewHandler.HTTP_ERR_500_CTRLLER_ERR).getErrorView(request, response);
        // }
        catch (InvocationTargetException e) {
            Throwable t = getTargetException(e);
            View v = doAfterThrow(request, response, t);

            return v != null ? v : new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    t, ErrorView.HTTP_ERR_500_CTRLLER_ERR);
        }
        catch (Throwable e) {
            View v = doAfterThrow(request, response, e);
            return v != null ? v : new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e,
                    ErrorView.HTTP_ERR_500_CTRLLER_ERR);
        }

        View v = doBeforeRender(request, response);

        return v != null ? v : view;
    }
    
    
    private View doBefore(HttpServletRequest request, HttpServletResponse response) {

        List<MvcBeforeBeanDefinition> tempIntercs = null;
        if (MvcUtils.CollectionUtil.isNotEmpty(beforeIntercList)) {
            // beforeIntercList 先执行
            tempIntercs = new ArrayList<MvcBeforeBeanDefinition>(beforeIntercList);
            tempIntercs.addAll(meta.getBeforeIntercList());
        }
        else {
            tempIntercs = meta.getBeforeIntercList();
        }

        if (MvcUtils.CollectionUtil.isNotEmpty(tempIntercs)) {

            int pos = MvcUtils.CollectionUtil.checkDuplication(tempIntercs);
            if (pos != -1) {
                LOG.warn("\"{}\" in [{}] is duplicate~! Framework auto removed the first one.",
                        tempIntercs.get(pos), meta.getObject().getClass());
                tempIntercs.remove(pos);
            }

            View ret = null;
            for (MvcBeforeBeanDefinition mbd : tempIntercs) {
                LOG.trace("[Execute AOP Before Method] - {}", mbd.getClassName());
                Method mt = mbd.getDisposeMethod();
                try {
                    ret = (View) mt.invoke(mbd.getObject(), new Object[] { request, response });
                    if (ret != null) {
                        return ret;
                    }
                }
                catch (InvocationTargetException e) {
                    return new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getTargetException(), ErrorView.HTTP_ERR_500_BEFORE_ERR);
                }
                catch (Throwable e) {
                    return new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e,
                            ErrorView.HTTP_ERR_500_BEFORE_ERR);
                }
            }
        }
        return null;
    }

    private View doBeforeRender(HttpServletRequest request, HttpServletResponse response) {

        List<MvcBeforeRenderBeanDefinition> tempIntercs = null;
        if (MvcUtils.CollectionUtil.isNotEmpty(beforeRenderIntercList)) {
            // beforeRenderIntercList 后执行
            tempIntercs = new ArrayList<MvcBeforeRenderBeanDefinition>(
                    meta.getBeforeRenderIntercList());
            tempIntercs.addAll(beforeRenderIntercList);
        }
        else {
            tempIntercs = meta.getBeforeRenderIntercList();
        }

        if (MvcUtils.CollectionUtil.isNotEmpty(tempIntercs)) {

            int pos = MvcUtils.CollectionUtil.checkDuplication(tempIntercs);
            if (pos != -1) {
                LOG.warn("\"{}\" in [{}] is duplicate~! Framework auto removed the first one.",
                        tempIntercs.get(pos), meta.getObject().getClass());
                tempIntercs.remove(pos);
            }

            View ret = null;
            for (MvcBeforeRenderBeanDefinition mbd : tempIntercs) {
                LOG.trace("[Execute AOP BeforeRender Method] - {}", mbd.getClassName());
                Method mt = mbd.getDisposeMethod();
                try {
                    ret = (View) mt.invoke(mbd.getObject(), new Object[] { request, response });
                    if (ret != null) {
                        return ret;
                    }
                }
                catch (InvocationTargetException e) {
                    return new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getTargetException(), ErrorView.HTTP_ERR_500_BEFORE_ERR);
                }
                catch (Throwable e) {
                    return new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e,
                            ErrorView.HTTP_ERR_500_BEFORE_ERR);
                }
            }
        }
        return null;
    }

    private View doAfterThrow(HttpServletRequest request, HttpServletResponse response, Throwable t) {
        List<MvcAfterThrowBeanDefinition> tempIntercs = null;
        if (MvcUtils.CollectionUtil.isNotEmpty(afterThrowIntercList)) {
            // afterThrowIntercList 后执行
            tempIntercs = new ArrayList<MvcAfterThrowBeanDefinition>(
                    meta.getAfterThrowIntercList());
            tempIntercs.addAll(afterThrowIntercList);
        }
        else {
            tempIntercs = meta.getAfterThrowIntercList();
        }

        if (MvcUtils.CollectionUtil.isNotEmpty(tempIntercs)) {

            int pos = MvcUtils.CollectionUtil.checkDuplication(tempIntercs);
            if (pos != -1) {
                LOG.warn("\"{}\" in [{}] is duplicate~! Framework auto removed the first one.",
                        tempIntercs.get(pos), meta.getObject().getClass());
                tempIntercs.remove(pos);
            }

            View ret = null;
            for (MvcAfterThrowBeanDefinition mbd : tempIntercs) {
                LOG.trace("[Execute AOP AfterThrow Method] - {}", mbd.getClassName());
                Method mt = mbd.getDisposeMethod();
                try {
                    ret = (View) mt.invoke(mbd.getObject(), new Object[] { request, response, t });
                    if (ret != null) {
                        return ret;
                    }
                }
                catch (InvocationTargetException e) {
                    return new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getTargetException(), ErrorView.HTTP_ERR_500_BEFORE_ERR);
                }
                catch (Throwable e) {
                    return new ErrorView(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e,
                            ErrorView.HTTP_ERR_500_BEFORE_ERR);
                }
            }
        }
        return null;
    }

    @Override
    public void doAtfer(HttpServletRequest request, HttpServletResponse response) {
        List<MvcAfterBeanDefinition> tempIntercs = null;
        if (MvcUtils.CollectionUtil.isNotEmpty(afterIntercList)) {
            // afterIntercList 后执行
            tempIntercs = new ArrayList<MvcAfterBeanDefinition>(meta.getAfterIntercList());
            tempIntercs.addAll(afterIntercList);
        }
        else {
            tempIntercs = meta.getAfterIntercList();
        }

        if (MvcUtils.CollectionUtil.isNotEmpty(tempIntercs)) {

            int pos = MvcUtils.CollectionUtil.checkDuplication(tempIntercs);
            if (pos != -1) {
                LOG.warn("\"{}\" in [{}] is duplicate~! Framework auto removed the first one.",
                        tempIntercs.get(pos), meta.getObject().getClass());
                tempIntercs.remove(pos);
            }

            for (MvcAfterBeanDefinition mbd : tempIntercs) {
                LOG.trace("[Execute AOP After Method] - {}", mbd.getClassName());
                Method mt = mbd.getDisposeMethod();
                try {
                    mt.invoke(mbd.getObject(), new Object[] { request, response });
                }
                catch (InvocationTargetException e) {
                    LOG.error(e.getTargetException());
                    return;
                }
                catch (Throwable e) {
                    LOG.error(e);
                    return;
                }
            }
        }
    }

    // doAround(mvcContext, args, beforeInvoke.length);
    // 最开始执行最后一个Invoke，在这个Invoke执行proceed时，会调用倒数第二个Invoke，
    // 在倒数第二个Invoke执行proceed时，会调用倒数第三个Invoke，……，在第一个Invoke执行proceed时，会调用method的invoke。
    // 参数，最初由外部传入mvcContext和原始args，如果后面没有定义新的args则沿用上级的args。
    private View doAround(final List<MvcAroundBeanDefinition> aroundIntercs,
            final HttpServletRequest request, final HttpServletResponse response,
            final Object[] args, final int i) throws Throwable {
        MvcAroundBeanDefinition mbd = aroundIntercs.get(i - 1);
        Method mt = mbd.getDisposeMethod();

        LOG.trace("[Execute AOP Around Method] - {}", mbd.getClassName());

        if (i > 1) {
            return (View) mt.invoke(mbd.getObject(), new Object[] { request, response,
                    new MvcJoinPoint() {
                        @Override
                        public View proceed(Object[] args) throws Throwable {
                            return doAround(aroundIntercs, request, response, args, i - 1);
                        }

                        @Override
                        public View proceed() throws Throwable {
                            return doAround(aroundIntercs, request, response, args, i - 1);
                        }
                    } });
        }

        return (View) mt.invoke(mbd.getObject(), new Object[] { request, response,
                new MvcJoinPoint() {
                    @Override
                    public View proceed(Object[] args) throws Throwable {
                        return (View) meta.getMethod().invoke(meta.getObject(), args);
                    }

                    @Override
                    public View proceed() throws Throwable {
                        return (View) meta.getMethod().invoke(meta.getObject(), args);
                    }
                } });

    }

    @Override
    public String toString() {
        return "[meta=" + meta + ", params=" + paramsMap + "]";
    }

    @SuppressWarnings("unchecked")
    private Object[] getInvokeParams(HttpServletRequest request, HttpServletResponse response) {

        PrimParamMeta[] paramMetaPrims = meta.getParamMetaPrims();
        BeanParamMeta[] paramMetaBeans = meta.getParamMetaBeans();

        ControllerMethodParamType[] paramType = meta.getParamType();
        Object[] p = new Object[paramType.length];

        for (int i = 0; i < p.length; i++) {
            switch (paramType[i]) {
            case HttpServletRequest:
                p[i] = request;
                break;
            case HttpServletResponse:
                p[i] = response;
                break;
            case HttpParamBean:
                // 请求参数封装到javabean
                Enumeration<String> enumeration = request.getParameterNames();
                BeanParamMeta paramMetaInfo = paramMetaBeans[i];
                p[i] = paramMetaInfo.newParamInstance();

                // 把http参数赋值给参数对象
                while (enumeration.hasMoreElements()) {
                    String httpParamName = enumeration.nextElement();
                    String paramValue = request.getParameter(httpParamName);
                    paramMetaInfo.setParam(p[i], httpParamName, paramValue);
                }
                if (MvcUtils.StringUtil.isNotBlank(paramMetaInfo.getAttribute())) {
                    request.setAttribute(paramMetaInfo.getAttribute(), p[i]);
                }
                break;
            case URIParam:
                PrimParamMeta pb = paramMetaPrims[i];
                String sval = paramsMap.get(pb.getAttribute());
                p[i] = MvcUtils.ConvertUtil.convert(sval, pb.getParamClass());
                if (pb.isSetAttr()) {
                    request.setAttribute(pb.getAttribute(), p[i]); // 默认行为：注入到request中
                }
                break;
            case HttpParamBasic:
                PrimParamMeta b = paramMetaPrims[i];
                p[i] = b.getValue(request);
                if (b.isSetAttr()) {
                    request.setAttribute(b.getAttribute(), p[i]); // 默认行为：注入到request中
                }
                break;
            }
        }
        return p;
    }
    
    /**
     * 递归获取反射invoke的真实异常（TargetException）
     */
    private static Throwable getTargetException(Throwable e) {
        if (e instanceof InvocationTargetException) {
            return getTargetException(((InvocationTargetException) e).getTargetException());
        }
        return e;
    }
}