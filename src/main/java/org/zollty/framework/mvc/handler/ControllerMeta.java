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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.annotation.HttpParam;
import org.zollty.framework.mvc.annotation.URIParam;
import org.zollty.framework.mvc.aop.ControllerMethodAopMeta;
import org.zollty.framework.mvc.aop.bean.MvcAfterBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAfterThrowBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAroundBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeRenderBeanDefinition;
import org.zollty.framework.mvc.context.ControllerBeanDefinition;
import org.zollty.framework.util.MvcUtils;

/**
 * Controller类的元信息
 * 
 * @author zollty
 * @since 2015-7-23
 */
public class ControllerMeta {

    /** controller的实例对象 */
    private Object object;
    private String servletURI;
    /** 请求uri对应的方法 */
    private Method method;

    // private List<String> paramsName;
    private ControllerBeanDefinition controllerBeanDef;

    /**
     * 请求方法参数类型，可以为 1、HttpParam标注 基础数据类型（int、boolean等） 2、HttpParam标注 Bean数据类型（标准POJO对象）
     * 3、HttpServletRequest 4、HttpServletResponse 5、URIParam标注
     */
    private ControllerMethodParamType[] paramType;

    /** HttpParam标注 基础数据类型（int、boolean等） */
    private PrimParamMeta[] paramMetaPrims;
    
    /** HttpParam标注 Bean数据类型（标准POJO对象） */
    private BeanParamMeta[] paramMetaBeans;

    private String[] allowHttpMethods;

    // AOP注解信息
    private ControllerMethodAopMeta controllerMethodAopMeta;

    public ControllerMeta(ControllerBeanDefinition controllerBeanDef, Method method,
            String[] allowHttpMethods, String servletURI) {
        this.object = controllerBeanDef.getObject();
        this.method = method;

        this.allowHttpMethods = allowHttpMethods;
        this.servletURI = servletURI;
        this.controllerBeanDef = controllerBeanDef;

        if (controllerBeanDef.getReqMethodsAopMap() != null) {
            controllerMethodAopMeta = controllerBeanDef.getReqMethodsAopMap().get(method);
        }

        parseMethodParams();
    }

    public ControllerMethodParamType[] getParamType() {
        return paramType;
    }

    public Object getObject() {
        return object;
    }

    public Method getMethod() {
        return method;
    }

    public String getServletURI() {
        return servletURI;
    }

    public PrimParamMeta[] getParamMetaPrims() {
        return paramMetaPrims;
    }

    public BeanParamMeta[] getParamMetaBeans() {
        return paramMetaBeans;
    }

    public ControllerBeanDefinition getControllerBeanDef() {
        return controllerBeanDef;
    }

    public List<MvcBeforeBeanDefinition> getBeforeIntercList() {
        return controllerMethodAopMeta != null ? controllerMethodAopMeta.getBeforeIntercList()
                : null;
    }

    public List<MvcAroundBeanDefinition> getAroundIntercList() {
        return controllerMethodAopMeta != null ? controllerMethodAopMeta.getAroundIntercList()
                : null;
    }

    public List<MvcBeforeRenderBeanDefinition> getBeforeRenderIntercList() {
        return controllerMethodAopMeta != null ? controllerMethodAopMeta
                .getBeforeRenderIntercList() : null;
    }

    public List<MvcAfterThrowBeanDefinition> getAfterThrowIntercList() {
        return controllerMethodAopMeta != null ? controllerMethodAopMeta
                .getAfterThrowIntercList() : null;
    }

    public List<MvcAfterBeanDefinition> getAfterIntercList() {
        return controllerMethodAopMeta != null ? controllerMethodAopMeta.getAfterIntercList()
                : null;
    }

    public String[] getAllowHttpMethods() {
        return allowHttpMethods;
    }

    // ~ extend public method
    public boolean allowMethod(String method) {
        for (String s : allowHttpMethods) {
            if (s.equals(method))
                return true;
        }
        return false;
    }

    // ~ utils -------------------------------------------

    private void parseMethodParams() {
        Class<?>[] paraTypes = method.getParameterTypes();
        this.paramType = new ControllerMethodParamType[paraTypes.length];

        // 构造参数对象
        paramMetaBeans = new BeanParamMeta[paraTypes.length];
        paramMetaPrims = new PrimParamMeta[paraTypes.length];
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < paraTypes.length; i++) {
            Annotation anno = getMethodParamAnnotation(annotations[i]);
            if (anno != null) {
                if (anno.annotationType().equals(HttpParam.class)) {
                    HttpParam httpParam = (HttpParam) anno;
                    if (MvcUtils.ConvertUtil.canConvert(paraTypes[i]) != null) {
                        // 数据类型为主要类型，可以直接转换，比如int、Long等
                        paramMetaPrims[i] = new PrimParamMeta(paraTypes[i], httpParam.value(),
                                httpParam.setAttr());
                        paramType[i] = ControllerMethodParamType.HttpParamBasic;
                    }
                    else {
                        // 数据类型为复合类型，需要调用标准setter赋值
                        BeanParamMeta paramMetaInfo = new BeanParamMeta(paraTypes[i],
                                MvcUtils.ReflectUtil.getSetterMethods(paraTypes[i]),
                                httpParam.value());
                        paramMetaBeans[i] = paramMetaInfo;
                        paramType[i] = ControllerMethodParamType.HttpParamBean;
                    }
                }
                else if (anno.annotationType().equals(URIParam.class)) {
                    if (MvcUtils.ConvertUtil.canConvert(paraTypes[i]) != null) {
                        URIParam pv = (URIParam) anno;
                        paramMetaPrims[i] = new PrimParamMeta(paraTypes[i], pv.value(), false);
                        paramType[i] = ControllerMethodParamType.URIParam;
                    }
                }
            }
            else {
                if (paraTypes[i].equals(HttpServletRequest.class)) {
                    paramType[i] = ControllerMethodParamType.HttpServletRequest;
                }
                else if (paraTypes[i].equals(HttpServletResponse.class)) {
                    paramType[i] = ControllerMethodParamType.HttpServletResponse;
                }
            }
        }
    }

    private Annotation getMethodParamAnnotation(Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (a.annotationType().equals(HttpParam.class)
                    || a.annotationType().equals(URIParam.class))
                return a;
        }
        return null;
    }

}