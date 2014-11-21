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
 * Zollty Framework MVC Source Code - Since v1.0
 * Author(s): 
 * Zollty Tsou (zolltytsou@gmail.com, http://blog.zollty.com)
 */
package org.zollty.framework.core.support.annotation;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.zollty.framework.core.support.BeanDefinition;

/**
 * @author zollty
 * @since 2014-1-4
 */
public class AnnotationBeanReaderTest {

    @Test
    public void testInject() {

        AbstractAnnotationBeanReader beanReader = new AbstractAnnotationBeanReader() {
            @Override
            protected BeanDefinition getBeanDefinition(Class<?> c) {
                return null;
            }
        };

        try {
            List<Method> list = beanReader.getInjectMethod(SubClass.class);
            for (Method m : list) {
                System.out.println(m.getName());
                System.out.println(m.getDeclaringClass());
            }

            List<Field> list1 = beanReader.getInjectField(SubClass.class);
            for (Field m : list1) {
                System.out.println(m.getName());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            fail("getInjectMethod and getInjectField error!");
        }

    }

}
