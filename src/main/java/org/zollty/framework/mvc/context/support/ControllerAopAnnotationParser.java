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
 * Create by ZollTy on 2015-2-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.context.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.mvc.annotation.Controller;
import org.zollty.framework.mvc.aop.ControllerMethodAopMeta;
import org.zollty.framework.mvc.aop.annotation.CAfter;
import org.zollty.framework.mvc.aop.annotation.CAfterThrow;
import org.zollty.framework.mvc.aop.annotation.CAround;
import org.zollty.framework.mvc.aop.annotation.CBefore;
import org.zollty.framework.mvc.aop.annotation.CBeforeRender;
import org.zollty.framework.mvc.aop.bean.MvcAfterBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAfterThrowBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAroundBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeRenderBeanDefinition;
import org.zollty.framework.mvc.context.ControllerBeanDefinition;
import org.zollty.framework.util.MvcUtils;
import org.zollty.util.BasicRuntimeException;

/**
 * 
 * 解析Controller的AOP定义并将AOP实例与Controller的Method绑定
 * 
 * @author zollty
 * @since 2015-2-15
 */
class ControllerAopAnnotationParser {

    private List<BeanDefinition> beanDefinitions;

    public ControllerAopAnnotationParser(List<BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;

        // 执行解析
        doParse();

        this.beanDefinitions = null;
    }

    private void doParse() {

        for (BeanDefinition bean : beanDefinitions) {

            if (!bean.getObject().getClass().isAnnotationPresent(Controller.class)) {
                continue;
            }

            // 解析 CBefore
            parseCBefore((ControllerBeanDefinition) bean);
            // 解析 CAround
            parseCAround((ControllerBeanDefinition) bean);
            // 解析 CBeforeRender
            parseCBeforeRender((ControllerBeanDefinition) bean);
            // 解析 CAfterThrow
            parseCAfterThrow((ControllerBeanDefinition) bean);
            // 解析 CAfter
            parseCAfter((ControllerBeanDefinition) bean);

        }
    }

    private void parseCBefore(ControllerBeanDefinition controllerBeanDef) {

        Map<Method, ControllerMethodAopMeta> mm = controllerBeanDef.getReqMethodsAopMap();

        // 首先第一步，解析class上面的AOP注解
        CBefore cb = controllerBeanDef.getObject().getClass().getAnnotation(CBefore.class);
        if (cb != null) {
            Class<?>[] clss = cb.value();

            if (clss.length > 0) {

                if (MvcUtils.CollectionUtil.checkDuplication(clss) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }

                List<MvcBeforeBeanDefinition> aopBeans = new ArrayList<MvcBeforeBeanDefinition>();
                for (int i = 0; i < clss.length; i++) {
                    Class<?> cls = clss[i];
                    // 找出对应的beans，放入List容器中
                    for (BeanDefinition bd : beanDefinitions) {
                        if (bd.getObject().getClass() == cls) {
                            aopBeans.add((MvcBeforeBeanDefinition) bd);
                            break;
                        }
                    }
                }

                if (mm == null) {
                    mm = new HashMap<Method, ControllerMethodAopMeta>();
                }
                // 将该Conntroller类下面的所有ReqMethods与类上面全局的AOP Beans关联
                for (Method method : controllerBeanDef.getReqMethods()) {
                    if (mm.get(method) != null) {
                        mm.get(method).getBeforeIntercList().addAll(aopBeans);
                    }
                    else {
                        ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                        mam.setBeforeIntercList(new ArrayList<MvcBeforeBeanDefinition>(aopBeans));
                        mm.put(method, mam);
                    }
                }
                // 将最后的结果关联到controller bean上
                controllerBeanDef.setReqMethodsAopMap(mm);
            }
        }

        // 第二步，解析ReqMethod上面的AOP注解，得到一个List，并与相应的Method相关联。
        for (Method method : controllerBeanDef.getReqMethods()) {

            if (method.getAnnotation(CBefore.class) == null) {
                continue;
            }

            Class<?>[] clss = method.getAnnotation(CBefore.class).value();
            if (clss.length == 0) {
                continue;
            }

            List<MvcBeforeBeanDefinition> aopBeans = new ArrayList<MvcBeforeBeanDefinition>();
            for (int i = 0; i < clss.length; i++) {
                Class<?> cls = clss[i];
                for (BeanDefinition bd : beanDefinitions) {
                    if (bd.getObject().getClass() == cls) {
                        aopBeans.add((MvcBeforeBeanDefinition) bd);
                        break;
                    }
                }
            }

            if (mm == null) {
                mm = new HashMap<Method, ControllerMethodAopMeta>();
            }
            if (mm.get(method) != null) {
                mm.get(method).getBeforeIntercList().addAll(aopBeans);

                if (MvcUtils.CollectionUtil.checkDuplication(mm.get(method).getBeforeIntercList()) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }
            }
            else {
                ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                mam.setBeforeIntercList(new ArrayList<MvcBeforeBeanDefinition>(aopBeans));
                mm.put(method, mam);

                if (MvcUtils.CollectionUtil.checkDuplication(aopBeans) != -1) {
                    throw new BasicRuntimeException("{}#{}() annotation definition duplication!",
                            controllerBeanDef.getObject().getClass().getName(), method.getName());
                }
            }

        }

        // 将最后的结果关联到controller bean上
        controllerBeanDef.setReqMethodsAopMap(mm);
    }

    private void parseCAround(ControllerBeanDefinition controllerBeanDef) {

        Map<Method, ControllerMethodAopMeta> mm = controllerBeanDef.getReqMethodsAopMap();
        // 第一步，解析ReqMethod上面的AOP注解，得到一个List，并与相应的Method相关联。
        for (Method method : controllerBeanDef.getReqMethods()) {

            if (method.getAnnotation(CAround.class) == null) {
                continue;
            }

            Class<?>[] clss = method.getAnnotation(CAround.class).value();
            if (clss.length == 0) {
                continue;
            }

            List<MvcAroundBeanDefinition> aopBeans = new ArrayList<MvcAroundBeanDefinition>();
            for (int i = 0; i < clss.length; i++) {
                Class<?> cls = clss[i];
                for (BeanDefinition bd : beanDefinitions) {
                    if (bd.getObject().getClass() == cls) {
                        aopBeans.add((MvcAroundBeanDefinition) bd);
                        break;
                    }
                }
            }

            // 反转顺序
            Collections.reverse(aopBeans);

            if (mm == null) {
                mm = new HashMap<Method, ControllerMethodAopMeta>();
            }
            if (mm.get(method) != null) {
                mm.get(method).getAroundIntercList().addAll(aopBeans);

                if (MvcUtils.CollectionUtil.checkDuplication(mm.get(method).getAroundIntercList()) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }
            }
            else {
                ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                mam.setAroundIntercList(new ArrayList<MvcAroundBeanDefinition>(aopBeans));
                mm.put(method, mam);

                if (MvcUtils.CollectionUtil.checkDuplication(aopBeans) != -1) {
                    throw new BasicRuntimeException("{}#{}() annotation definition duplication!",
                            controllerBeanDef.getObject().getClass().getName(), method.getName());
                }
            }

        }
        
        // 第二步，解析class上面的AOP注解
        CAround cb = controllerBeanDef.getObject().getClass().getAnnotation(CAround.class);
        if (cb != null) {
            Class<?>[] clss = cb.value();

            if (clss.length > 0) {

                if (MvcUtils.CollectionUtil.checkDuplication(clss) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }

                List<MvcAroundBeanDefinition> aopBeans = new ArrayList<MvcAroundBeanDefinition>();
                for (int i = 0; i < clss.length; i++) {
                    Class<?> cls = clss[i];
                    // 找出对应的beans，放入List容器中
                    for (BeanDefinition bd : beanDefinitions) {
                        if (bd.getObject().getClass() == cls) {
                            aopBeans.add((MvcAroundBeanDefinition) bd);
                            break;
                        }
                    }
                }

                // 反转顺序
                Collections.reverse(aopBeans);

                if (mm == null) {
                    mm = new HashMap<Method, ControllerMethodAopMeta>();
                }
                // 将该Conntroller类下面的所有ReqMethods与类上面全局的AOP Beans关联
                for (Method method : controllerBeanDef.getReqMethods()) {
                    if (mm.get(method) != null) {
                        mm.get(method).getAroundIntercList().addAll(aopBeans);
                    }
                    else {
                        ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                        mam.setAroundIntercList(new ArrayList<MvcAroundBeanDefinition>(aopBeans));
                        mm.put(method, mam);
                    }
                }
                // 将最后的结果关联到controller bean上
                controllerBeanDef.setReqMethodsAopMap(mm);
            }
        }

        // 将最后的结果关联到controller bean上
        controllerBeanDef.setReqMethodsAopMap(mm);
    }

    private void parseCBeforeRender(ControllerBeanDefinition controllerBeanDef) {

        Map<Method, ControllerMethodAopMeta> mm = controllerBeanDef.getReqMethodsAopMap();

        // 首先第一步，解析ReqMethod上面的AOP注解，得到一个List，并与相应的Method相关联。
        for (Method method : controllerBeanDef.getReqMethods()) {

            if (method.getAnnotation(CBeforeRender.class) == null) {
                continue;
            }

            Class<?>[] clss = method.getAnnotation(CBeforeRender.class).value();
            if (clss.length == 0) {
                continue;
            }

            List<MvcBeforeRenderBeanDefinition> aopBeans = new ArrayList<MvcBeforeRenderBeanDefinition>();
            for (int i = 0; i < clss.length; i++) {
                Class<?> cls = clss[i];
                for (BeanDefinition bd : beanDefinitions) {
                    if (bd.getObject().getClass() == cls) {
                        aopBeans.add((MvcBeforeRenderBeanDefinition) bd);
                        break;
                    }
                }
            }

            if (mm == null) {
                mm = new HashMap<Method, ControllerMethodAopMeta>();
            }
            if (mm.get(method) != null) {
                mm.get(method).getBeforeRenderIntercList().addAll(aopBeans);

                if (MvcUtils.CollectionUtil.checkDuplication(mm.get(method)
                        .getBeforeRenderIntercList()) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }
            }
            else {
                ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                mam.setBeforeRenderIntercList(new ArrayList<MvcBeforeRenderBeanDefinition>(aopBeans));
                mm.put(method, mam);

                if (MvcUtils.CollectionUtil.checkDuplication(aopBeans) != -1) {
                    throw new BasicRuntimeException("{}#{}() annotation definition duplication!",
                            controllerBeanDef.getObject().getClass().getName(), method.getName());
                }
            }

        }

        // 第二步，解析class上面的AOP注解
        CBeforeRender cb = controllerBeanDef.getObject().getClass()
                .getAnnotation(CBeforeRender.class);
        if (cb != null) {
            Class<?>[] clss = cb.value();

            if (clss.length > 0) {

                if (MvcUtils.CollectionUtil.checkDuplication(clss) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }

                List<MvcBeforeRenderBeanDefinition> aopBeans = new ArrayList<MvcBeforeRenderBeanDefinition>();
                for (int i = 0; i < clss.length; i++) {
                    Class<?> cls = clss[i];
                    // 找出对应的beans，放入List容器中
                    for (BeanDefinition bd : beanDefinitions) {
                        if (bd.getObject().getClass() == cls) {
                            aopBeans.add((MvcBeforeRenderBeanDefinition) bd);
                            break;
                        }
                    }
                }
                if (mm == null) {
                    mm = new HashMap<Method, ControllerMethodAopMeta>();
                }
                // 将该Conntroller类下面的所有ReqMethods与类上面全局的AOP Beans关联
                for (Method method : controllerBeanDef.getReqMethods()) {
                    if (mm.get(method) != null) {
                        mm.get(method).getBeforeRenderIntercList().addAll(aopBeans);
                    }
                    else {
                        ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                        mam.setBeforeRenderIntercList(new ArrayList<MvcBeforeRenderBeanDefinition>(
                                aopBeans));
                        mm.put(method, mam);
                    }
                }
                // 将最后的结果关联到controller bean上
                controllerBeanDef.setReqMethodsAopMap(mm);
            }
        }

        // 将最后的结果关联到controller bean上
        controllerBeanDef.setReqMethodsAopMap(mm);
    }

    private void parseCAfterThrow(ControllerBeanDefinition controllerBeanDef) {

        Map<Method, ControllerMethodAopMeta> mm = controllerBeanDef.getReqMethodsAopMap();

        // 第一步，解析ReqMethod上面的AOP注解，得到一个List，并与相应的Method相关联。
        for (Method method : controllerBeanDef.getReqMethods()) {

            if (method.getAnnotation(CAfterThrow.class) == null) {
                continue;
            }

            Class<?>[] clss = method.getAnnotation(CAfterThrow.class).value();
            if (clss.length == 0) {
                continue;
            }

            List<MvcAfterThrowBeanDefinition> aopBeans = new ArrayList<MvcAfterThrowBeanDefinition>();
            for (int i = 0; i < clss.length; i++) {
                Class<?> cls = clss[i];
                for (BeanDefinition bd : beanDefinitions) {
                    if (bd.getObject().getClass() == cls) {
                        aopBeans.add((MvcAfterThrowBeanDefinition) bd);
                        break;
                    }
                }
            }

            if (mm == null) {
                mm = new HashMap<Method, ControllerMethodAopMeta>();
            }
            if (mm.get(method) != null) {
                mm.get(method).getAfterThrowIntercList().addAll(aopBeans);

                if (MvcUtils.CollectionUtil.checkDuplication(mm.get(method)
                        .getAfterThrowIntercList()) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }
            }
            else {
                ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                mam.setAfterThrowIntercList(new ArrayList<MvcAfterThrowBeanDefinition>(
                        aopBeans));
                mm.put(method, mam);

                if (MvcUtils.CollectionUtil.checkDuplication(aopBeans) != -1) {
                    throw new BasicRuntimeException("{}#{}() annotation definition duplication!",
                            controllerBeanDef.getObject().getClass().getName(), method.getName());
                }
            }

        }

        // 第二步，解析class上面的AOP注解
        CAfterThrow cb = controllerBeanDef.getObject().getClass()
                .getAnnotation(CAfterThrow.class);
        if (cb != null) {
            Class<?>[] clss = cb.value();

            if (clss.length > 0) {

                if (MvcUtils.CollectionUtil.checkDuplication(clss) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }

                List<MvcAfterThrowBeanDefinition> aopBeans = new ArrayList<MvcAfterThrowBeanDefinition>();
                for (int i = 0; i < clss.length; i++) {
                    Class<?> cls = clss[i];
                    // 找出对应的beans，放入List容器中
                    for (BeanDefinition bd : beanDefinitions) {
                        if (bd.getObject().getClass() == cls) {
                            aopBeans.add((MvcAfterThrowBeanDefinition) bd);
                            break;
                        }
                    }
                }
                if (mm == null) {
                    mm = new HashMap<Method, ControllerMethodAopMeta>();
                }
                // 将该Conntroller类下面的所有ReqMethods与类上面全局的AOP Beans关联
                for (Method method : controllerBeanDef.getReqMethods()) {
                    if (mm.get(method) != null) {
                        mm.get(method).getAfterThrowIntercList().addAll(aopBeans);
                    }
                    else {
                        ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                        mam.setAfterThrowIntercList(new ArrayList<MvcAfterThrowBeanDefinition>(
                                aopBeans));
                        mm.put(method, mam);
                    }
                }
                // 将最后的结果关联到controller bean上
                controllerBeanDef.setReqMethodsAopMap(mm);
            }
        }

        // 将最后的结果关联到controller bean上
        controllerBeanDef.setReqMethodsAopMap(mm);
    }

    private void parseCAfter(ControllerBeanDefinition controllerBeanDef) {

        Map<Method, ControllerMethodAopMeta> mm = controllerBeanDef.getReqMethodsAopMap();

        // 第一步，解析ReqMethod上面的AOP注解，得到一个List，并与相应的Method相关联。
        for (Method method : controllerBeanDef.getReqMethods()) {

            if (method.getAnnotation(CAfter.class) == null) {
                continue;
            }

            Class<?>[] clss = method.getAnnotation(CAfter.class).value();
            if (clss.length == 0) {
                continue;
            }

            List<MvcAfterBeanDefinition> aopBeans = new ArrayList<MvcAfterBeanDefinition>();
            for (int i = 0; i < clss.length; i++) {
                Class<?> cls = clss[i];
                for (BeanDefinition bd : beanDefinitions) {
                    if (bd.getObject().getClass() == cls) {
                        aopBeans.add((MvcAfterBeanDefinition) bd);
                        break;
                    }
                }
            }

            if (mm == null) {
                mm = new HashMap<Method, ControllerMethodAopMeta>();
            }
            if (mm.get(method) != null) {
                mm.get(method).getAfterIntercList().addAll(aopBeans);

                if (MvcUtils.CollectionUtil.checkDuplication(mm.get(method).getAfterIntercList()) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }
            }
            else {
                ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                mam.setAfterIntercList(new ArrayList<MvcAfterBeanDefinition>(aopBeans));
                mm.put(method, mam);

                if (MvcUtils.CollectionUtil.checkDuplication(aopBeans) != -1) {
                    throw new BasicRuntimeException("{}#{}() annotation definition duplication!",
                            controllerBeanDef.getObject().getClass().getName(), method.getName());
                }
            }

        }

        // 第二步，解析class上面的AOP注解
        CAfter cb = controllerBeanDef.getObject().getClass().getAnnotation(CAfter.class);
        if (cb != null) {
            Class<?>[] clss = cb.value();

            if (clss.length > 0) {

                if (MvcUtils.CollectionUtil.checkDuplication(clss) != -1) {
                    throw new BasicRuntimeException(controllerBeanDef.getObject().getClass()
                            + " annotation definition duplication!");
                }

                List<MvcAfterBeanDefinition> aopBeans = new ArrayList<MvcAfterBeanDefinition>();
                for (int i = 0; i < clss.length; i++) {
                    Class<?> cls = clss[i];
                    // 找出对应的beans，放入List容器中
                    for (BeanDefinition bd : beanDefinitions) {
                        if (bd.getObject().getClass() == cls) {
                            aopBeans.add((MvcAfterBeanDefinition) bd);
                            break;
                        }
                    }
                }
                if (mm == null) {
                    mm = new HashMap<Method, ControllerMethodAopMeta>();
                }
                // 将该Conntroller类下面的所有ReqMethods与类上面全局的AOP Beans关联
                for (Method method : controllerBeanDef.getReqMethods()) {
                    if (mm.get(method) != null) {
                        mm.get(method).getAfterIntercList().addAll(aopBeans);
                    }
                    else {
                        ControllerMethodAopMeta mam = new ControllerMethodAopMeta();
                        mam.setAfterIntercList(new ArrayList<MvcAfterBeanDefinition>(aopBeans));
                        mm.put(method, mam);
                    }
                }
                // 将最后的结果关联到controller bean上
                controllerBeanDef.setReqMethodsAopMap(mm);
            }
        }

        // 将最后的结果关联到controller bean上
        controllerBeanDef.setReqMethodsAopMap(mm);
    }

}