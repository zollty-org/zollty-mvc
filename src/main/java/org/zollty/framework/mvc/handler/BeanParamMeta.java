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
package org.zollty.framework.mvc.handler;

import java.lang.reflect.Method;
import java.util.Map;

import org.jretty.util.NestedRuntimeException;
import org.zollty.framework.util.MvcUtils;

/**
 * @author zollty
 * @since 2013-9-21
 */
public class BeanParamMeta {
    private final Class<?> paramClass; // 要注入的类型
    private final Map<String, Method> beanSetMethod; // 要注入的bean的set方法
    private final String attribute; // 要setAttribute的属性

    public BeanParamMeta(Class<?> paramClass, Map<String, Method> beanSetMethod, String attribute) {
        this.paramClass = paramClass;
        this.beanSetMethod = beanSetMethod;
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    /**
     * 给参数对象的实例赋值
     * 
     * @param o
     *            要赋值的对象
     * @param key
     *            要赋值的属性
     * @param value
     *            要赋的值
     */
    public void setParam(Object o, String key, String value) {
        Method m = beanSetMethod.get(key);
        if (m != null) {
            Class<?> p = m.getParameterTypes()[0];
            try {
                MvcUtils.ReflectionUtil.invokeMethod(m, o, MvcUtils.ConvertUtil.convert(value, p));
            } catch (Exception e) {
                throw new NestedRuntimeException(e, "set param value error.");
            }
        }
    }

    /**
     * 新建一个参数对象实例
     */
    public Object newParamInstance() {
        return MvcUtils.ReflectionUtil.newInstance(paramClass);
    }

}