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
 * Create by ZollTy on 2013-9-20 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.beans.annotation;

import org.zollty.framework.core.annotation.Component;
import org.zollty.framework.core.beans.BeanDefinition;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.resource.support.ResourcePatternResolver;

/**
 * 读取Bean信息
 * 
 * @author zollty
 * @since 2013-9-20
 */
public class AnnotationBeanReader extends AbstractAnnotationBeanReader {

    private Logger log = LogFactory.getLogger(AnnotationBeanReader.class);

    public AnnotationBeanReader(String[] scanningPackages, ClassLoader beanClassLoader,
            ResourcePatternResolver resourcePatternResolver) {

        super(scanningPackages, beanClassLoader, resourcePatternResolver);
    }

    @Override
    protected BeanDefinition getBeanDefinition(Class<?> c) {
        if (c.isAnnotationPresent(Component.class)) {
            log.info("classes - " + c.getName());
            return componentParser(c);
        }
        return null;
    }

}