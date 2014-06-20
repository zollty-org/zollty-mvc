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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.zollty.util.ClassUtils;
import org.zollty.util.DateFormatUtils;
import org.zollty.util.ExceptionUtils;
import org.zollty.util.FileUtils;
import org.zollty.util.IOUtils;
import org.zollty.util.RandomUtils;
import org.zollty.util.ReflectionUtils;
import org.zollty.util.ResourceUtils;
import org.zollty.util.StringSplitUtils;
import org.zollty.util.StringUtils;
import org.zollty.util.ThreadUtils;

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
    
    
    public static class StringSplitUtil extends StringSplitUtils {
        
        /**
         * Tokenize the given String into a String array via a StringTokenizer.
         * Trims tokens and omits empty tokens.
         * <p>The given delimiters string is supposed to consist of any number of
         * delimiter characters. Each of those characters can be used to separate
         * tokens. A delimiter is always a single character; for multi-character
         * delimiters, consider using <code>delimitedListToStringArray</code>
         * @param str the String to tokenize
         * @param delimiters the delimiter characters, assembled as String
         * (each of those characters is individually considered as delimiter).
         * @return an array of the tokens
         * @see java.util.StringTokenizer
         * @see java.lang.String#trim()
         * @see #delimitedListToStringArray
         */
        public static String[] tokenizeToStringArray(String str, String delimiters) {
            return tokenizeToStringArray(str, delimiters, true, true);
        }

        /**
         * Tokenize the given String into a String array via a StringTokenizer.
         * <p>The given delimiters string is supposed to consist of any number of
         * delimiter characters. Each of those characters can be used to separate
         * tokens. A delimiter is always a single character; for multi-character
         * delimiters, consider using <code>delimitedListToStringArray</code>
         * @param str the String to tokenize
         * @param delimiters the delimiter characters, assembled as String
         * (each of those characters is individually considered as delimiter)
         * @param trimTokens trim the tokens via String's <code>trim</code>
         * @param ignoreEmptyTokens omit empty tokens from the result array
         * (only applies to tokens that are empty after trimming; StringTokenizer
         * will not consider subsequent delimiters as token in the first place).
         * @return an array of the tokens (<code>null</code> if the input String
         * was <code>null</code>)
         * @see java.util.StringTokenizer
         * @see java.lang.String#trim()
         * @see #delimitedListToStringArray
         */
        public static String[] tokenizeToStringArray(
                String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

            if (str == null) {
                return null;
            }
            StringTokenizer st = new StringTokenizer(str, delimiters);
            List<String> tokens = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (trimTokens) {
                    token = token.trim();
                }
                if (!ignoreEmptyTokens || token.length() > 0) {
                    tokens.add(token);
                }
            }
            return (String[])MvcConvertUtils.convert(tokens, String[].class);
        }
    }

    public static class FileUtil extends FileUtils {
    }

    public static class IOUtil extends IOUtils {
    }

    public static class RandomUtil extends RandomUtils {
    }

    public static class ResourceUtil extends ResourceUtils {
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

}
