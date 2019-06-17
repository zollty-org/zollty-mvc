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
package org.zollty.framework.core.context.support;

import java.util.LinkedList;
import java.util.List;

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.AnnotationAwareOrderComparator;
import org.jretty.util.NestedRuntimeException;
import org.jretty.util.OrderComparator;
import org.zollty.framework.core.beans.support.AbstractBeanFactory;
import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.core.context.ApplicationContext;
import org.zollty.framework.core.interceptor.AfterRefresh;
import org.zollty.framework.core.interceptor.BeforeClose;
import org.zollty.framework.core.interceptor.MvcInterceptor;
import org.zollty.framework.util.MvcUtils;

/**
 * @author zollty
 * @since 2013-10-11
 */
public abstract class AbstractApplicationContext extends AbstractBeanFactory implements
        ApplicationContext {

    private IApplicationConfig config;
    
    private List<MvcInterceptor> beforeRefreshInterceptors = new LinkedList<MvcInterceptor>();
    
    private List<MvcInterceptor> afterCloseInterceptors = new LinkedList<MvcInterceptor>();
    
    private List<MvcInterceptor> afterRefreshInterceptors = new LinkedList<MvcInterceptor>();
    
    private List<MvcInterceptor> beforeCloseInterceptors = new LinkedList<MvcInterceptor>();
    
    private static transient Logger logger;

    public AbstractApplicationContext(IApplicationConfig config) {
        super();

        // 配置好了，才能刷新
        this.config = config;
        
        initMvcInterceptor(this.getClass().getClassLoader());

        refresh();
    }
    
    public AbstractApplicationContext(IApplicationConfig config, ClassLoader beanClassLoader) {
        super(beanClassLoader);

        // 配置好了，才能刷新
        this.config = config;
        
        initMvcInterceptor(this.getClass().getClassLoader());

        refresh();
    }
    
    @Override
    protected void doBeforeRefresh() {
        for(MvcInterceptor intcp: beforeRefreshInterceptors) {
            intcp.onEnvent();
        }
    }
    
    @Override
    protected void doAfterRefresh() {
        afterRefreshInterceptors.addAll(getBeansOfType(AfterRefresh.class));
        AnnotationAwareOrderComparator.sort(afterRefreshInterceptors);
        for(MvcInterceptor intcp: afterRefreshInterceptors) {
            intcp.onEnvent();
        }
    }
    
    @Override
    protected void doBeforeClose() {
        beforeCloseInterceptors.addAll(getBeansOfType(BeforeClose.class));
        AnnotationAwareOrderComparator.sort(beforeCloseInterceptors);
        for(MvcInterceptor intcp: beforeCloseInterceptors) {
            try {
                intcp.onEnvent();
            } catch (Exception e) {
                getLogger().error(e);
            }
        }
    }
    
    @Override
    protected void doAfterClose() {
        for(MvcInterceptor intcp: afterCloseInterceptors) {
            try {
                intcp.onEnvent();
            } catch (Exception e) {
                getLogger().error(e);
            }
        }
    }
    
    protected void initMvcInterceptor(ClassLoader loader) {
        if (config.getBeforeRefreshInterceptors() != null) {
            for (String clazz : config.getBeforeRefreshInterceptors()) {
                try {
                    beforeRefreshInterceptors.add(
                            (MvcInterceptor) MvcUtils.ReflectionUtil.newInstance(
                                    Class.forName(clazz, true, loader)));
                    OrderComparator.sort(beforeRefreshInterceptors);
                } catch (ClassNotFoundException e) {
                    throw new NestedRuntimeException(e);
                }
            }
        }
        if (config.getAfterCloseInterceptors() != null) {
            for (String clazz : config.getAfterCloseInterceptors()) {
                try {
                    afterCloseInterceptors.add(
                            (MvcInterceptor) MvcUtils.ReflectionUtil.newInstance(
                                    Class.forName(clazz, true, loader)));
                    OrderComparator.sort(afterCloseInterceptors);
                } catch (ClassNotFoundException e) {
                    throw new NestedRuntimeException(e);
                }
            }
        }
    }
    
    private static Logger getLogger() {
        if (logger == null) {
            logger = LogFactory.getLogger(AbstractApplicationContext.class);
        }
        return logger;
    }

    public IApplicationConfig getConfig() {
        return config;
    }

    public void setConfig(IApplicationConfig config) {
        this.config = config;
    }

}