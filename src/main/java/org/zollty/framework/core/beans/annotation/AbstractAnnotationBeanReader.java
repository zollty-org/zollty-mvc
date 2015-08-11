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
 * Create by ZollTy on 2013-9-21 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.beans.annotation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.zollty.framework.core.annotation.Component;
import org.zollty.framework.core.annotation.Inject;
import org.zollty.framework.core.beans.AbstractBeanReader;
import org.zollty.framework.core.beans.BeanDefinition;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.Assert;
import org.zollty.util.ResourceUtils;
import org.zollty.util.resource.Resource;
import org.zollty.util.resource.support.PathMatchingResourcePatternResolver;
import org.zollty.util.resource.support.ResourcePatternResolver;

/**
 * @author zollty
 * @since 2013-9-21
 */
abstract public class AbstractAnnotationBeanReader extends AbstractBeanReader {

    private Logger log = LogFactory.getLogger(AbstractAnnotationBeanReader.class);

    /** ClassLoader to resolve bean class names with, if necessary */
    private ClassLoader beanClassLoader;

    private ResourcePatternResolver resourcePatternResolver;

    private String[] scanningPackages;

    public AbstractAnnotationBeanReader(String[] scanningPackages, ClassLoader beanClassLoader,
            ResourcePatternResolver resourcePatternResolver) {
        Assert.notNull(scanningPackages);
        this.scanningPackages = scanningPackages;
        this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : MvcUtils.ClassUtil
                .getDefaultClassLoader());
        this.resourcePatternResolver = (resourcePatternResolver != null ? resourcePatternResolver
                : new PathMatchingResourcePatternResolver(this.beanClassLoader));
    }

    @Override
    public List<BeanDefinition> loadBeanDefinitions() {
        init();
        return beanDefinitions;
    }

    private void init() {
        beanDefinitions = new ArrayList<BeanDefinition>();
        for (String pack : scanningPackages) {
            log.info("------------------------------componentPath = [{}]", pack);
            scan(pack.trim());
        }
    }

    protected void scan(String packageName) {
        String packageDirName = packageName.replace('.', '/');
        String packageSearchPath = ResourceUtils.CLASSPATH_ALL_URL_PREFIX + packageDirName
                + "/**/*.class";
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

            log.debug("resources size under [{}] = {}", packageSearchPath, resources.length);
            log.debug("resources type = [{}]", resources[0].getClass().getName());

            for (Resource r : resources) {
                String org = r.getURL().toString();
                String className = org.substring(org.indexOf(packageDirName), org.length() - 6)
                        .replace('/', '.'); // length-6 去掉“.class”
                parseClass(className);
            }
        }
        catch (IOException e) {
            error("Pattern = [" + packageSearchPath + "] can not be found.");
        }
    }

    private void parseClass(String className) {

        Class<?> c = null;
        try {
            c = getBeanClassLoader().loadClass(className);
        }
        catch (Throwable t) {
            log.error(t, "parse class error");
            return;
        }

        BeanDefinition beanDefinition = null;
        try {
            beanDefinition = getBeanDefinition(c);
        }
        catch (Throwable e) {
            log.error(e, "get bean definition error: class=" + c.getName());
        }
        if (beanDefinition != null) {
            beanDefinitions.add(beanDefinition);
        }

    }

    // let subclass override it
    abstract protected BeanDefinition getBeanDefinition(Class<?> c);

    protected BeanDefinition componentParser(Class<?> c) {
        AnnotationBeanDefinition annotationBeanDefinition = new GenericAnnotationBeanDefinition();
        annotationBeanDefinition.setClassName(c.getName());

        Component component = c.getAnnotation(Component.class);
        String id = component.value();
        id = id.length() != 0 ? id : MvcUtils.DateFormatUtil.getShortUniqueDate_TimeMillis();
        annotationBeanDefinition.setId(id);

        String[] names = MvcUtils.ReflectUtil.getInterfaceNames(c);
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

    protected List<Field> getInjectField(Class<?> c) {
        Field[] fields = c.getDeclaredFields();
        List<Field> list = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.getAnnotation(Inject.class) != null) {
                list.add(field);
            }
        }
        return list;
    }

    protected List<Method> getInjectMethod(Class<?> c) {
        Method[] methods = c.getMethods(); // [包括父类的方法]，getDeclaredMethods[不包括父类的方法]
        List<Method> list = new ArrayList<Method>();
        for (Method m : methods) {
            if (m.getAnnotation(Inject.class) != null) {
                list.add(m);
            }
        }
        return list;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    public ResourcePatternResolver getResourcePatternResolver() {
        return resourcePatternResolver;
    }

    public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public String[] getScanningPackages() {
        return scanningPackages;
    }

    public void setScanningPackages(String[] scanningPackages) {
        this.scanningPackages = scanningPackages;
    }
}