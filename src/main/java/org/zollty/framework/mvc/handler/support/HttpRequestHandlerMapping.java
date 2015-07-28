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
package org.zollty.framework.mvc.handler.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.zollty.framework.core.Const;
import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.mvc.ViewHandler;
import org.zollty.framework.mvc.annotation.RequestMapping;
import org.zollty.framework.mvc.aop.annotation.AopMapping;
import org.zollty.framework.mvc.aop.bean.AopBeanDefinition;
import org.zollty.framework.mvc.context.ControllerBeanDefinition;
import org.zollty.framework.mvc.handler.AbstractHandlerMapping;
import org.zollty.framework.mvc.handler.ControllerMeta;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.BasicRuntimeException;
import org.zollty.util.NestedRuntimeException;

/**
 * @author zollty
 * @since 2013-9-16
 */
public class HttpRequestHandlerMapping extends AbstractHandlerMapping {

    private Logger log = LogFactory.getLogger(HttpRequestHandlerMapping.class);

    private ViewHandlerFactory viewHandlerFactory = new ViewHandlerFactory();

    public HttpRequestHandlerMapping(List<BeanDefinition> beanDefinitions, IApplicationConfig config) {
        super(config);

        this.initContext(beanDefinitions);
    }

    @Override
    public ViewHandler match(String servletURI, HttpServletRequest request) {
        return viewHandlerFactory.getHandler(servletURI, request);
    }

    
    private void initContext(List<BeanDefinition> beanDefinitions) {

        addAopIntercList(beanDefinitions);

        for (BeanDefinition beanDef : beanDefinitions) {
            if (beanDef instanceof ControllerBeanDefinition) {
                ControllerBeanDefinition beanDefinition = (ControllerBeanDefinition) beanDef;
                List<Method> list = beanDefinition.getReqMethods();

                if (list == null) {
                    continue;
                }

                String uriPrefix = beanDefinition.getUriPrefix();

                for (Method method : list) {
                    method.setAccessible(true);

                    String value = method.getAnnotation(RequestMapping.class).value();
                    String[] array = MvcUtils.StringSplitUtil.splitByWholeSeparatorIgnoreEmpty(
                            value, ":");

                    String[] allowHttpMethods = null;
                    String uri = null;
                    if (array.length == 1) {
                        uri = value;
                        allowHttpMethods = new String[] { "GET", "POST", "PUT", "DELETE" };
                    }
                    else { // length==2
                        allowHttpMethods = MvcUtils.StringSplitUtil
                                .splitByWholeSeparatorIgnoreEmpty(array[0], "|");
                        uri = array[1].trim();
                    }
                    if (MvcUtils.StringUtil.isBlank(uri)) {
                        throw new IllegalArgumentException("controller uri can't be blank! Bean="
                                + beanDefinition.getClassName() + ", method=" + method.getName());
                    }
                    if (MvcUtils.StringUtil.isNotEmpty(uriPrefix)) {
                        uri = uriPrefix + uri;
                    }
                    try {
                        viewHandlerFactory.addControllerMeta(new ControllerMeta(beanDefinition,
                                method, allowHttpMethods, uri));
                        log.debug("controller uri: {} {}", uri, Arrays.toString(allowHttpMethods));
                    }
                    catch (BasicRuntimeException e) {
                        throw new NestedRuntimeException(e, "[Bean={}, method={}]",
                                beanDefinition.getClassName(), method.getName());
                    }
                }
            }
        }
    }

    public void addAopIntercList(List<BeanDefinition> beanDefinitions) {

        List<AopBeanDefinition> aopIntercList = new ArrayList<AopBeanDefinition>();
        for (BeanDefinition bean : beanDefinitions) {
            if (!(bean instanceof AopBeanDefinition)
                    || !bean.getObject().getClass().isAnnotationPresent(AopMapping.class)) {
                continue;
            }
            AopBeanDefinition bd = (AopBeanDefinition) bean;
            // 首先第一步，解析class上面的AOP注解
            String[] uriMatch = bean.getObject().getClass().getAnnotation(AopMapping.class).value();
            for (String value : uriMatch) {
                String[] array = MvcUtils.StringSplitUtil.splitByWholeSeparatorIgnoreEmpty(value, ":");
                String uriPattern = null;
                if (array.length == 1) {
                    uriPattern = value;
                    bd.setOrder(Const.DEFAULT_MVC_AOP_MAPPING_ORDER);
                    bd.setUriPattern(uriPattern);
                }
                else { // length==2
                    uriPattern = array[1].trim();
                    bd.setOrder(Integer.valueOf(array[0].trim()));
                    bd.setUriPattern(uriPattern);
                }
            }
            aopIntercList.add(bd);
            log.info(bd);
        }

        // 排序：从小到大
        int len = aopIntercList.size();
        int order;
        AopBeanDefinition temp;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                order = aopIntercList.get(i).getOrder();
                if (order > aopIntercList.get(j).getOrder()) {
                    temp = aopIntercList.get(i);
                    aopIntercList.set(i, aopIntercList.get(j));
                    aopIntercList.set(j, temp);
                }
            }
        }

        viewHandlerFactory.setAopIntercList(aopIntercList);
    }

}