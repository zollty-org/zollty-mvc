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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.zollty.util.ArrayUtils;
import org.zollty.util.ClassUtils;
import org.zollty.util.CollectionUtils;
import org.zollty.util.DateFormatUtils;
import org.zollty.util.ExceptionUtils;
import org.zollty.util.FileUtils;
import org.zollty.util.IOUtils;
import org.zollty.util.RandomUtils;
import org.zollty.util.ReflectionUtils;
import org.zollty.util.StringSplitUtils;
import org.zollty.util.StringUtils;
import org.zollty.util.ThreadUtils;
import org.zollty.util.WebResourceUtils;
import org.zollty.util.WebUtils;

/**
 * @author zollty 
 * @since 2013-10-28
 */
public class MvcUtils {
    
    /** yyyy-MM-dd HH:mm:ss格式 */
    public static final MvcUtils.DateFormatUtil DATEFORMAT = new MvcUtils.DateFormatUtil();

    public static class StringUtil extends StringUtils {

        public static List<String> splitURL(String uri) {
            List<String> ret = new ArrayList<String>();
            int start = 1;
            int max = uri.length() - 1;

            for (int i = 1; i <= max; i++) {
                if (uri.charAt(i) == '/') {
                    ret.add(uri.substring(start, i));
                    start = i + 1;
                }
            }

            if (uri.charAt(max) != '/')
                ret.add(uri.substring(start));
            return ret;
        }

    }
    
    
    public static class ConvertUtil extends MvcConvertUtils {
    }
    
    public static class ReflectUtil extends MvcReflectUtils {
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
            return ExceptionUtils.getStackTraceStr(lineChecker, e, null);
        }

        public static String getStackTraceStr(Throwable e, String prompt) {
            return ExceptionUtils.getStackTraceStr(lineChecker, e, prompt);
        }

        private static HttpServerLineChecker lineChecker = new HttpServerLineChecker();

        static class HttpServerLineChecker implements LineChecker {
            @Override
            public boolean checkLine(String line) {
                boolean result = false;
                result = line.startsWith("\tat org.zollty.framework.mvc.handler");
                if (result)
                    return false;
                result = line.startsWith("\tat sun.reflect");
                if (result)
                    return false;
                result = line.startsWith("\tat com.ibm.ws");
                if (result)
                    return false;
                result = line.startsWith("\tat com.ibm.io");
                if (result)
                    return false;
                result = line.startsWith("\tat org.apache.catalina");
                if (result)
                    return false;
                result = line.startsWith("\tat org.apache.coyote.http11");
                if (result)
                    return false;
                return true;
            }
        }

    }
    
    public static class ClassUtil extends ClassUtils {
    }
    
    public static class ReflectionUtil extends ReflectionUtils {
    }
    
    public static class CollectionUtil extends CollectionUtils {
        /**
         * 把集合转换为指定类型的数组，zolltyMVC专用
         * <p>
         * 仅用于类型不确定的情况，如果类型确定，推荐用 collection.toArray(new T[0])方式，例如list.toArray()
         * 
         * @param collection
         * @param arrayType
         * @return Array newInstance
         */
        public static Object toArrayObj(Collection<?> collection, Class<?> arrayType) {
            Class<?> componentType = null;
            if (arrayType == null) {
                componentType = Object.class;
            }
            else {
                if (!arrayType.isArray())
                    throw new IllegalArgumentException("type is not a array");
                componentType = arrayType.getComponentType();
            }

            int size = collection.size();
            // Allocate a new Array
            Object newArray = Array.newInstance(componentType, size);
            Iterator<?> iterator = collection.iterator();
            // Convert and set each element in the new Array
            for (int i = 0; i < size; i++) {
                Object element = iterator.next();
                Array.set(newArray, i, element);
            }
            return newArray;
        }
    }
    
    public static class ArrayUtil extends ArrayUtils {
    }
    
    public static class WebUtil extends WebUtils {
    }

}
