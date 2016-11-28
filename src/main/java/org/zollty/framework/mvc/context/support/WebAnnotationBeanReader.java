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
 * Create by ZollTy on 2013-9-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.context.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.zollty.framework.core.annotation.Component;
import org.zollty.framework.core.beans.BeanDefinition;
import org.zollty.framework.core.beans.annotation.AbstractAnnotationBeanReader;
import org.zollty.framework.core.beans.annotation.AnnotationBeanDefinition;
import org.zollty.framework.mvc.annotation.Controller;
import org.zollty.framework.mvc.annotation.RequestMapping;
import org.zollty.framework.mvc.aop.MvcAfter;
import org.zollty.framework.mvc.aop.MvcAfterThrow;
import org.zollty.framework.mvc.aop.MvcAround;
import org.zollty.framework.mvc.aop.MvcBefore;
import org.zollty.framework.mvc.aop.MvcBeforeRender;
import org.zollty.framework.mvc.aop.bean.MvcAfterBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAfterThrowBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAroundBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeRenderBeanDefinition;
import org.zollty.framework.mvc.context.ControllerBeanDefinition;
import org.zollty.framework.util.MvcUtils;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.resource.support.ResourcePatternResolver;

/**
 * @author zollty
 * @since 2013-9-15
 */
class WebAnnotationBeanReader extends AbstractAnnotationBeanReader {

    private Logger log = LogFactory.getLogger(WebAnnotationBeanReader.class);

    public WebAnnotationBeanReader(String[] scanningPackages, ClassLoader beanClassLoader,
            ResourcePatternResolver resourcePatternResolver) {

        super(scanningPackages, beanClassLoader, resourcePatternResolver);

    }

    @Override
    protected BeanDefinition getBeanDefinition(Class<?> c) {
        BeanDefinition ret = null;
        if (c.isAnnotationPresent(Component.class)) {
            ret = componentParser(c);
        }
        else if (c.isAnnotationPresent(Controller.class)) {
            ret = controllerParser(c);
        }
        else if (MvcBefore.class.isAssignableFrom(c)) {
            ret = aopBeforeParser(c);
        }
        else if (MvcAround.class.isAssignableFrom(c)) {
            ret = aopAroundParser(c);
        }
        else if (MvcBeforeRender.class.isAssignableFrom(c)) {
            ret = aopBeforeRenderParser(c);
        }
        else if (MvcAfterThrow.class.isAssignableFrom(c)) {
            ret = aopAfterThrowParser(c);
        }
        else if (MvcAfter.class.isAssignableFrom(c)) {
            ret = aopAfterParser(c);
        }
        if (ret != null) {
            log.info("classes - " + c.getName());
        }
        return ret;
    }

    protected BeanDefinition controllerParser(Class<?> c) {
        ControllerBeanDefinition beanDefinition = new ControllerAnnotatedBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);
        
        // modified by zollty 12/15/2014 contoller bean don't need exact id
        String id = MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        beanDefinition.setId(id);

        List<Method> reqMethods = getReqMethods(c);
        beanDefinition.setReqMethods(reqMethods);

        String uriPrefix = c.getAnnotation(Controller.class).value();
        beanDefinition.setUriPrefix(uriPrefix);

        return beanDefinition;
    }
    
    
    private BeanDefinition aopBeforeParser(Class<?> c) {
        MvcBeforeBeanDefinition beanDefinition = new MvcBeforeBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);
        
        String id = MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        beanDefinition.setId(id);

        for (Method m : c.getMethods()) {
            if (m.getName().equals("doBefore")) {
                beanDefinition.setDisposeMethod(m);
                break;
            }
        }

        return beanDefinition;
    }
    
    private BeanDefinition aopAroundParser(Class<?> c) {
        MvcAroundBeanDefinition beanDefinition = new MvcAroundBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);
        
        String id = MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        beanDefinition.setId(id);

        for (Method m : c.getMethods()) {
            if (m.getName().equals("doAround")) {
                beanDefinition.setDisposeMethod(m);
                break;
            }
        }

        return beanDefinition;
    }
    
    
    private BeanDefinition aopBeforeRenderParser(Class<?> c) {
        MvcBeforeRenderBeanDefinition beanDefinition = new MvcBeforeRenderBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);
        
        String id = MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        beanDefinition.setId(id);

        for (Method m : c.getMethods()) {
            if (m.getName().equals("doBeforeRender")) {
                beanDefinition.setDisposeMethod(m);
                break;
            }
        }

        return beanDefinition;
    }
    
    private BeanDefinition aopAfterThrowParser(Class<?> c) {
        MvcAfterThrowBeanDefinition beanDefinition = new MvcAfterThrowBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);
        
        String id = MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        beanDefinition.setId(id);

        for (Method m : c.getMethods()) {
            if (m.getName().equals("doAfterThrow")) {
                beanDefinition.setDisposeMethod(m);
                break;
            }
        }

        return beanDefinition;
    }
    
    private BeanDefinition aopAfterParser(Class<?> c) {
        MvcAfterBeanDefinition beanDefinition = new MvcAfterBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);
        
        String id = MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        beanDefinition.setId(id);

        for (Method m : c.getMethods()) {
            if (m.getName().equals("doAfter")) {
                beanDefinition.setDisposeMethod(m);
                break;
            }
        }

        return beanDefinition;
    }

    private void setWebBeanDefinition(AnnotationBeanDefinition beanDefinition, Class<?> c) {
        beanDefinition.setClassName(c.getName());

        String[] names = MvcUtils.ReflectUtil.getInterfaceNames(c);
        beanDefinition.setInterfaceNames(names);

        List<Field> fields = getInjectField(c);
        beanDefinition.setInjectFields(fields);

        List<Method> methods = getInjectMethod(c);
        beanDefinition.setInjectMethods(methods);

        try {
            Object object = c.newInstance();
            beanDefinition.setObject(object);
        }
        catch (Throwable t) {
            log.error(t, "set web bean error");
        }
    }

    private List<Method> getReqMethods(Class<?> c) {
        Method[] methods = c.getMethods();
        List<Method> list = new ArrayList<Method>();
        for (Method m : methods) {
            if (m.isAnnotationPresent(RequestMapping.class)) {
                list.add(m);
            }
        }
        return list;
    }

}