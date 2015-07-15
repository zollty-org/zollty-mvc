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
 * Zollty Framework MVC Source Code - Since v1.0
 * Author(s): 
 * Zollty Tsou (zolltytsou@gmail.com, http://blog.zollty.com)
 */
package org.zollty.framework.mvc.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.mvc.annotation.RequestMapping;
import org.zollty.framework.mvc.aop.annotation.AopMapping;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.handler.support.ControllerResource;
import org.zollty.framework.mvc.support.ControllerBeanDefinition;
import org.zollty.framework.mvc.support.ControllerMetaInfo;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.BasicRuntimeException;
import org.zollty.util.NestedRuntimeException;

/**
 * @author zollty
 * @since 2013-9-16
 */
public class HttpServletDispatcherHandler extends AbstractHandlerMapping {

    private Logger log = LogFactory.getLogger(HttpServletDispatcherHandler.class);

    protected final ControllerResource controllerResource;

    public HttpServletDispatcherHandler(List<BeanDefinition> beanDefinitions, IApplicationConfig config) {
        super(config);

        controllerResource = new ControllerResource();

        initContext(beanDefinitions);
    }

    @Override
    public WebHandler match(String servletURI, HttpServletRequest request) {
        return controllerResource.getHandler(servletURI, request);
    }

    private void initContext(List<BeanDefinition> beanDefinitions) {
        // List<InterceptorMetaInfo> interceptorList = new LinkedList<InterceptorMetaInfo>();
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
                    String[] array = MvcUtils.StringSplitUtil.splitByWholeSeparatorIgnoreEmpty(value, ":");

                    String[] allowHttpMethods = null;
                    String uri = null;
                    if (array.length == 1) {
                        uri = value;
                        allowHttpMethods = new String[] { "GET", "POST", "PUT", "DELETE" };
                    }
                    else { // length==2
                        allowHttpMethods = MvcUtils.StringSplitUtil.splitByWholeSeparatorIgnoreEmpty(array[0], "|");
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
                        controllerResource.addController(
                                new ControllerMetaInfo(beanDefinition.getObject(), method, 
                                        allowHttpMethods, uri, 
                                        parseBeforeValueMethod(beanDefinition,method, beanDefinitions)));
                        log.debug("controller uri: {} {}", uri, Arrays.toString(allowHttpMethods));
                    }
                    catch (BasicRuntimeException e) {
                        throw new NestedRuntimeException(e, 
                                "[Bean={}, method={}]", beanDefinition.getClassName(), method.getName());
                    }
                }
            }
        }
    }

    private MvcBeforeBeanDefinition[] parseBeforeValueMethod(ControllerBeanDefinition beanDefinition, Method method,
            List<BeanDefinition> beanDefinitions) {

        for (BeanDefinition bean : beanDefinitions) {
            if (!(bean instanceof MvcBeforeBeanDefinition)
                    || !bean.getObject().getClass().isAnnotationPresent(AopMapping.class)) {
                continue;
            }
            MvcBeforeBeanDefinition bd = (MvcBeforeBeanDefinition) bean;
            // 首先第一步，解析class上面的AOP注解
            String[] uriMatch = bean.getObject().getClass().getAnnotation(AopMapping.class).value();
            for (String value : uriMatch) {
                String[] array = MvcUtils.StringSplitUtil.splitByWholeSeparatorIgnoreEmpty(value, ":");
                // TODO
                String uri = null;
                if (array.length == 1) {
                    uri = value;
                    bd.setOrder(0);
                }
                else { // length==2
                    uri = array[1].trim();
                    bd.setOrder(Integer.valueOf(array[0].trim()));
                }
            }

        }

        List<MvcBeforeBeanDefinition> mb = beanDefinition.getReqMethodsAOP() == null ? null : beanDefinition
                .getReqMethodsAOP().get(method);

        if (mb == null) {
            return null;
        }

        int len = mb.size();
//        // check duplication
//        for (int i = 0; i < len; i++) {
//            for (int j = i + 1; j < len; j++) {
//                if (mb.get(i).equals(mb.get(j))) {
//                    log.warn(mb.get(i).getClassName() + " duplicate at " + beanDefinition.getClassName() + "#"
//                            + method.getName());
//                }
//            }
//        }

        // // 排序：从小到大
        // MvcBeforeBeanDefinition temp;
        // int fpos;
        // for (int i = 0; i < len; i++) {
        // for (int j = i + 1; j < len; j++) {
        // fpos = mb[i].getOrder();
        // if (fpos > mb[j].getOrder()) {
        // temp = mb[i];
        // mb[i] = mb[j];
        // mb[j] = temp;
        // }
        // }
        // }

        return mb.toArray(new MvcBeforeBeanDefinition[len]);
    }

}
