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
package org.zollty.framework.core.beans;

import java.util.List;
import java.util.Map;

import org.zollty.framework.core.support.BeanDefinition;

/**
 * 
 * @author zollty
 * @since 2015-10-11
 */
public interface ListableBeanFactory extends BeanFactory {

    List<BeanDefinition> getBeanDefinitions();

    Map<String, Object> getBeanMap();

    <T> Map<String, T> getBeansOfType(Class<T> type);

}