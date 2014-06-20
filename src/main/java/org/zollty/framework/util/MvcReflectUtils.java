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
package org.zollty.framework.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zollty 
 * @since 2013-9-11
 */
public class MvcReflectUtils {
	
    /**
     * 获取所有接口名称
     */
    public static String[] getInterfaceNames(Class<?> c) {
        Class<?>[] interfaces = c.getInterfaces();
        List<String> names = new ArrayList<String>();
        for (Class<?> i : interfaces) {
            names.add(i.getName());
        }
        return names.toArray(new String[0]);
    }

    public static interface BeanMethodFilter {
        boolean accept(String propertyName, Method method);
    }

    public static Map<String, Method> getSetterMethods(Class<?> clazz) {
        return getSetterMethods(clazz, null);
    }

    public static Map<String, Method> getSetterMethods(Class<?> clazz, BeanMethodFilter filter) {
        Map<String, Method> beanSetMethod = new HashMap<String, Method>();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (!method.getName().startsWith("set") || Modifier.isStatic(method.getModifiers())
                    || !method.getReturnType().equals(Void.TYPE) || method.getParameterTypes().length != 1) {
                continue;
            }
            String propertyName = getPropertyNameBySetterMethod(method);
            method.setAccessible(true);

            if (filter == null || filter.accept(propertyName, method))
                beanSetMethod.put(propertyName, method);
        }
        return beanSetMethod;
    }

    public static String getPropertyNameBySetterMethod(Method method) {
        String methodName = method.getName();
        String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        return propertyName;
    }
	
	
	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied name
	 * and no parameters. Searches all superclasses up to <code>Object</code>.
	 * <p>Returns <code>null</code> if no {@link Method} can be found.
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 * @return the Method object, or <code>null</code> if none found
	 */
	public static Method findMethod(Class<?> clazz, String name) {
		return findMethod(clazz, name, new Class[0]);
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied name
	 * and parameter types. Searches all superclasses up to <code>Object</code>.
	 * <p>Returns <code>null</code> if no {@link Method} can be found.
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 * @param paramTypes the parameter types of the method
	 * (may be <code>null</code> to indicate any signature)
	 * @return Method the Method object, or <code>null</code> if none found
	 */
    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (Method method : methods) {
                if (name.equals(method.getName())
                        && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

}
