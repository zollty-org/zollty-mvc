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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.zollty.framework.core.annotation.Component;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.annotation.AbstractAnnotationBeanReader;
import org.zollty.framework.core.support.annotation.AnnotationBeanDefinition;
import org.zollty.framework.mvc.annotation.Controller;
import org.zollty.framework.mvc.annotation.RequestMapping;
import org.zollty.framework.mvc.aop.MvcBefore;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.util.MvcReflectUtils;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.resource.support.ResourcePatternResolver;

/**
 * @author zollty
 * @since 2013-9-15
 */
public class WebAnnotationBeanReader extends AbstractAnnotationBeanReader {

    private Logger log = LogFactory.getLogger(WebAnnotationBeanReader.class);
    
    public WebAnnotationBeanReader(String[] scanningPackages, 
            ClassLoader beanClassLoader, ResourcePatternResolver resourcePatternResolver) {
        
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
        // else if (c.isAnnotationPresent(Interceptor.class)) {
        // ret = interceptorParser(c);
        // }
        else if (MvcBefore.class.isAssignableFrom(c)) {
            ret = aopParser(c);
        }
        if (ret != null) {
            log.info("classes - " + c.getName());
        }
        return ret;
    }

    protected BeanDefinition controllerParser(Class<?> c) {
        ControllerBeanDefinition beanDefinition = new ControllerAnnotatedBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);

        List<Method> reqMethods = getReqMethods(c);
        beanDefinition.setReqMethods(reqMethods);

        String uriPrefix = c.getAnnotation(Controller.class).value();
        beanDefinition.setUriPrefix(uriPrefix);

        return beanDefinition;
    }

    private BeanDefinition aopParser(Class<?> c) {
        MvcBeforeBeanDefinition beanDefinition = new MvcBeforeBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);

        for (Method m : c.getMethods()) {
            if (m.getName().equals("aspect")) {
                beanDefinition.setDisposeMethod(m);
                break;
            }
        }

        return beanDefinition;
    }

    private void setWebBeanDefinition(AnnotationBeanDefinition beanDefinition, Class<?> c) {
        beanDefinition.setClassName(c.getName());

        String id = getId(c);
        beanDefinition.setId(id);

        String[] names = MvcReflectUtils.getInterfaceNames(c);
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

    private String getId(Class<?> c) {
        if (c.isAnnotationPresent(Controller.class))
            // return c.getAnnotation(Controller.class).value();
            // modified by zollty 12/15/2014 contoller bean don't need exact id
            return MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        // else if (c.isAnnotationPresent(Interceptor.class))
        // return c.getAnnotation(Interceptor.class).value();
        else if (c.isAnnotationPresent(Component.class))
            return c.getAnnotation(Component.class).value();
        else if (MvcBefore.class.isAssignableFrom(c))
            return MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        else
            return "";
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
