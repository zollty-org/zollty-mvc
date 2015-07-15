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
 * Create by ZollTy on 2014-12-9 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.context.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.mvc.annotation.Controller;
import org.zollty.framework.mvc.aop.annotation.CBefore;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.support.ControllerBeanDefinition;
import org.zollty.util.BasicRuntimeException;
import org.zollty.util.CollectionUtils;

/**
 * 
 * @author zollty
 * @since 2014-12-9
 */
public class BeanAopAnnotationParser {

    public BeanAopAnnotationParser(List<BeanDefinition> beanDefinitions) {
        doParse(beanDefinitions);
    }

    private void doParse(List<BeanDefinition> beanDefinitions) {

        for (BeanDefinition bean : beanDefinitions) {

            if (!bean.getObject().getClass().isAnnotationPresent(Controller.class)) {
                continue;
            }
            ControllerBeanDefinition controllerBeanDef = (ControllerBeanDefinition) bean;

            // 首先第一步，解析class上面的AOP注解
            CBefore cb = bean.getObject().getClass().getAnnotation(CBefore.class);
            if (cb != null) {
                Class<?>[] clss = cb.value();

                if (clss.length > 0) {

                    if (CollectionUtils.checkDuplication(clss) != -1) {
                        throw new BasicRuntimeException(bean.getObject().getClass()
                                + " annotation definition duplication!");
                    }

                    List<MvcBeforeBeanDefinition> befores = new ArrayList<MvcBeforeBeanDefinition>();
                    for (int i = 0; i < clss.length; i++) {
                        Class<?> cls = clss[i];
                        // 找出对应的beans，放入List容器中
                        for (BeanDefinition bd : beanDefinitions) {
                            if (bd.getObject().getClass() == cls) {
                                befores.add((MvcBeforeBeanDefinition) bd);
                                break;
                            }
                        }
                    }
                    Map<Method, List<MvcBeforeBeanDefinition>> mm = new HashMap<Method, List<MvcBeforeBeanDefinition>>();
                    // 将该Conntroller类下面的所有ReqMethods与类上面全局的AOP Beans关联
                    for (Method method : controllerBeanDef.getReqMethods()) {
                        mm.put(method, new ArrayList<MvcBeforeBeanDefinition>(befores));
                    }
                    // 将最后的结果关联到controller bean上
                    controllerBeanDef.setReqMethodsAOP(mm);
                }
            }

            Map<Method, List<MvcBeforeBeanDefinition>> mm = controllerBeanDef.getReqMethodsAOP();
            // 第二步，解析ReqMethod上面的AOP注解，得到一个List，并与相应的Method相关联。
            for (Method method : controllerBeanDef.getReqMethods()) {

                if (method.getAnnotation(CBefore.class) == null) {
                    continue;
                }

                Class<?>[] clss = method.getAnnotation(CBefore.class).value();
                if (clss.length == 0) {
                    continue;
                }

                List<MvcBeforeBeanDefinition> befores = new ArrayList<MvcBeforeBeanDefinition>();
                for (int i = 0; i < clss.length; i++) {
                    Class<?> cls = clss[i];
                    for (BeanDefinition bd : beanDefinitions) {
                        if (bd.getObject().getClass() == cls) {
                            befores.add((MvcBeforeBeanDefinition) bd);
                            break;
                        }
                    }
                }

                if (mm == null) {
                    mm = new HashMap<Method, List<MvcBeforeBeanDefinition>>();
                }
                if (mm.get(method) != null) {
                    mm.get(method).addAll(befores);

                    if (CollectionUtils.checkDuplication(mm.get(method)) != -1) {
                        throw new BasicRuntimeException(bean.getObject().getClass()
                                + " annotation definition duplication!");
                    }
                }
                else {
                    mm.put(method, befores);

                    if (CollectionUtils.checkDuplication(befores) != -1) {
                        throw new BasicRuntimeException(
                                "{}#{}() annotation definition duplication!", bean.getObject()
                                        .getClass().getName(), method.getName());
                    }
                }

            }

            // 将最后的结果关联到controller bean上
            controllerBeanDef.setReqMethodsAOP(mm);
        }
    }

}