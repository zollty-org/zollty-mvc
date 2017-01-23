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
 * Create by ZollTy on 2013-10-11 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.beans.support;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.Assert;
import org.zollty.framework.core.beans.ConfigurableBeanFactory;
import org.zollty.framework.core.beans.annotation.AnnotationBeanDefinition;
import org.zollty.framework.core.beans.xml.XmlBeanDefinition;
import org.zollty.framework.util.MvcUtils;

/**
 * 
 * @author zollty
 * @since 2013-10-11
 */
abstract public class AbstractBeanFactory implements ConfigurableBeanFactory {

    private Logger log = LogFactory.getLogger(AbstractBeanFactory.class);

    /** ClassLoader to resolve bean class names with, if necessary */
    private ClassLoader beanClassLoader;

    private Map<String, Object> beanMap;

    /**
     * Using MvcUtils.ClassUtil.getDefaultClassLoader() for beanClassLoader
     */
    public AbstractBeanFactory() {
        this.beanClassLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
    }

    /**
     * Using the given beanClassLoader
     */
    public AbstractBeanFactory(ClassLoader beanClassLoader) {
        if (beanClassLoader != null) {
            this.beanClassLoader = beanClassLoader;
        } else {
            this.beanClassLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
        }
    }

    @Override
    public void refresh() {
        // ~初始化工作,刷新之前把旧的close
        close();

        // 刷新之前执行个性化操作
        doBeforeRefresh();
        // ~End

        BeansLoader beansLoader = new BeansLoader(getBeanClassLoader());
        beansLoader.refresh(loadXmlBeanDefinitions(), loadAnnoBeanDefinitions());

        this.beanMap = beansLoader.getBeanMap();
        beansLoader = null;

        // 把当前的BeanFactory或者ApplicationContext转存一份到BeanFactoryHelper静态方法中，以便外部调用
        BeanFactoryHelper.refreshBeanFactory(this);

        log.debug("BeanFactory refresh success! bean size = {}", beanMap.size());
        // 刷新之后执行个性化操作
        doAfterRefresh();
    }

    @Override
    public void close() {
        doAfterClose();
        
        beanMap = null;
    }

    // 交给子类去实现
    /** 加载xml beans的定义 */
    abstract protected List<XmlBeanDefinition> loadXmlBeanDefinitions();

    /** 加载annotation beans的定义 */
    abstract protected List<AnnotationBeanDefinition> loadAnnoBeanDefinitions();

    /** 在刷新前做的一些个性化操作 */
    abstract protected void doBeforeRefresh();

    /** 在刷新后做的一些个性化操作 */
    abstract protected void doAfterRefresh();

    /** 关闭之后，执行一些个性化操作 */
    abstract protected void doAfterClose();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz) {
        return (T) beanMap.get(clazz.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String id) {
        return (T) beanMap.get(id);
    }

    @Override
    public Map<String, Object> getBeanMap() {
        return beanMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Assert.notNull(type, "Class must not be null");
        Map<String, T> result = new LinkedHashMap<String, T>();
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object obj = entry.getValue();
            if (obj.getClass().equals(type)) {
                result.put(entry.getKey(), (T) obj);
            }
        }
        return result;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

}