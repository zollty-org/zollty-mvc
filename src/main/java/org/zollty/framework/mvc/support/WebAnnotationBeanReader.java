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
import org.zollty.framework.core.support.annotation.AnnotatedBeanDefinition;
import org.zollty.framework.core.support.annotation.AnnotationBeanDefinition;
import org.zollty.framework.mvc.annotation.Controller;
import org.zollty.framework.mvc.annotation.RequestMapping;
import org.zollty.framework.util.MvcReflectUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2013-9-15
 */
public class WebAnnotationBeanReader extends AbstractAnnotationBeanReader {

    private Logger log = LogFactory.getLogger(WebAnnotationBeanReader.class);

    public WebAnnotationBeanReader(ClassLoader beanClassLoader) {
        super.setBeanClassLoader(beanClassLoader);
        super.init();
    }

    @Override
    protected BeanDefinition getBeanDefinition(Class<?> c) {
        if (c.isAnnotationPresent(Component.class)) {
            log.info("classes - " + c.getName());
            return componentParser(c);
        }
        else if (c.isAnnotationPresent(Controller.class)) {
            log.info("classes - " + c.getName());
            return controllerParser(c);
        }
        else
            return null;
    }

    protected BeanDefinition controllerParser(Class<?> c) {
        ControllerBeanDefinition beanDefinition = new ControllerAnnotatedBeanDefinition();
        setWebBeanDefinition(beanDefinition, c);

        List<Method> reqMethods = getReqMethods(c);
        beanDefinition.setReqMethods(reqMethods);
        return beanDefinition;
    }

    protected BeanDefinition componentParser(Class<?> c) {
        AnnotationBeanDefinition annotationBeanDefinition = new AnnotatedBeanDefinition();
        annotationBeanDefinition.setClassName(c.getName());

        Component component = c.getAnnotation(Component.class);
        String id = component.value();
        annotationBeanDefinition.setId(id);

        String[] names = MvcReflectUtils.getInterfaceNames(c);
        annotationBeanDefinition.setInterfaceNames(names);

        List<Field> fields = getInjectField(c);
        annotationBeanDefinition.setInjectFields(fields);

        List<Method> methods = getInjectMethod(c);
        annotationBeanDefinition.setInjectMethods(methods);

        try {
            Object object = c.newInstance();
            annotationBeanDefinition.setObject(object);
        }
        catch (Throwable t) {
            log.error(t, "component parser error");
        }
        return annotationBeanDefinition;
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
            return c.getAnnotation(Controller.class).value();
        else if (c.isAnnotationPresent(Component.class))
            return c.getAnnotation(Component.class).value();
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
