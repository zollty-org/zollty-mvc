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
 * Create by ZollTy on 2013-10-28 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.util;

import java.io.Closeable;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jretty.util.ArrayUtils;
import org.jretty.util.ClassUtils;
import org.jretty.util.CollectionUtils;
import org.jretty.util.DateFormatUtils;
import org.jretty.util.ExceptionUtils;
import org.jretty.util.FileUtils;
import org.jretty.util.IOUtils;
import org.jretty.util.PathUtils;
import org.jretty.util.RandomUtils;
import org.jretty.util.ReflectionUtils;
import org.jretty.util.StringSplitUtils;
import org.jretty.util.StringUtils;
import org.jretty.util.ThreadUtils;
import org.jretty.util.WebResourceUtils;
import org.jretty.util.WebUtils;

/**
 * @author zollty 
 * @since 2013-10-28
 */
public class MvcUtils {
    
    /** yyyy-MM-dd HH:mm:ss格式 */
    public static final MvcUtils.DateFormatUtil DATEFORMAT = new MvcUtils.DateFormatUtil();
    
    public static class PathUtil extends PathUtils {
    }
    
    public static class StringUtil extends StringUtils {
    }
    
    public static class ConvertUtil extends MvcConvertUtils {
    }
    
    public static class ReflectUtil extends ReflectionUtils {
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
                if (!method.getName().startsWith("set") 
                        || Modifier.isStatic(method.getModifiers()) 
                        || !method.getReturnType().equals(Void.TYPE)
                        || method.getParameterTypes().length != 1) {
                    continue;
                }
                String propertyName = getPropertyNameBySetterMethod(method);
                makeAccessible(method);

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
    }
    
    public static class VerifyUtil extends MvcVerifyUtils {
    }
    
    public static class StringSplitUtil extends StringSplitUtils {
    }

    public static class FileUtil extends FileUtils {
    }

    public static class IOUtil extends IOUtils {
    }

    public static class RandomUtil extends RandomUtils {
    }

    public static class ResourceUtil extends WebResourceUtils {
    }

    public static class ThreadUtil extends ThreadUtils {
    }
    
    public static class DateFormatUtil extends DateFormatUtils {
        public DateFormatUtil() {
            super();
        }

        public DateFormatUtil(String datePattern) {
            super(datePattern);
        }
    }
    

    public static class ExceptionUtil extends ExceptionUtils {

        public static String getStackTraceStr(Throwable e) {
            return ExceptionUtils.getStackTraceStr(eleFilter, e, null);
        }

        public static String getStackTraceStr(Throwable e, String prompt) {
            return ExceptionUtils.getStackTraceStr(eleFilter, e, prompt);
        }

        private static HttpServerLineChecker eleFilter = new HttpServerLineChecker();

        static class HttpServerLineChecker implements StackTraceFilter {
//                // false 过滤，true 不过滤
//                return !(line.startsWith("\tat org.zollty.framework.mvc.handler") 
//                        || line.startsWith("\tat sun.reflect")
//                        || line.startsWith("\tat org.apache.catalina")
//                        || line.startsWith("\tat org.apache.coyote.http11")
//                        || line.startsWith("\tat com.ibm.ws")
//                        || line.startsWith("\tat com.ibm.io"));
            
            @Override
            public boolean exclude(String className) {
                return excludeMvc(className) 
                        || excludeTomcat(className) 
                        || excludeJava(className)
                        || excludeDubbo(className) 
                        || excludeWs(className);
            }
            
            private static boolean excludeJava(String className) {
                return className.startsWith("sun.reflect");
            }

            private static boolean excludeTomcat(String className) {
                return className.startsWith("org.apache.catalina")
                        || className.startsWith("org.apache.coyote");
            }

            private static boolean excludeMvc(String className) {
                return className.startsWith("org.zollty.framework.mvc.handler");
            }
            
            private static boolean excludeDubbo(String className) {
                return className.startsWith("com.alibaba.dubbo");
            }
            
            private static boolean excludeWs(String className) {
                return className.startsWith("com.ibm.ws") || className.startsWith("com.ibm.io");
            }
        }

    }
    
    public static class ClassUtil extends ClassUtils {
        /**
         * @see {@link ClassUtils#findAllAssignableClass(Class, ClassLoader)}
         */
        public static String[] getInterfaceNames(Class<?> c, ClassLoader classLoader) {
            Set<Class<?>> interfaces = MvcUtils.ClassUtil.findAllAssignableClass(c, classLoader);
            interfaces.remove(c);
            // 去掉常用的一些接口，以便减少AbstractBeanFactory.errorConflict的size
            interfaces.remove(Serializable.class);
            interfaces.remove(Closeable.class);
            List<String> names = new ArrayList<String>();
            for (Class<?> i : interfaces) {
                names.add(i.getName());
            }
            return names.toArray(new String[names.size()]);
        }
    }
    
    public static class ReflectionUtil extends ReflectionUtils {
    }
    
    public static class CollectionUtil extends CollectionUtils {
    }
    
    public static class ArrayUtil extends ArrayUtils {
    }
    
    public static class WebUtil extends WebUtils {
    }

}
