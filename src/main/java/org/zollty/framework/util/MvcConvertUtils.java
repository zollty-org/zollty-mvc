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
 * Create by ZollTy on 2013-9-11 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换工具类
 * 
 * @author zollty
 * @since 2013-9-11
 */
class MvcConvertUtils {

    /**
     * check if the class can be convert by this utils
     * 
     * @return ParseValue instance for convert using, null if can't be convert.
     */
    public static ParseValue canConvert(Class<?> clazz) {
        return clazz == null ? null : commonTypeClassMap.get(clazz);
    }

    /**
     * Convert string to class instance.
     * 
     * @param classType
     *            the target class type name, e.g. Integer.class
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(String value, Class<T> classType) {
        Object ret = null;
        ParseValue p = classType == null ? null : commonTypeClassMap.get(classType);
        if (p != null)
            ret = p.parse(value);
        else {
            if (MvcUtils.VerifyUtil.isInteger(value)) {
                ret = Integer.parseInt(value);
            }
            else if (MvcUtils.VerifyUtil.isLong(value)) {
                ret = Long.parseLong(value);
            }
            else if (MvcUtils.VerifyUtil.isDouble(value)) {
                ret = Double.parseDouble(value);
            }
            else if (MvcUtils.VerifyUtil.isFloat(value)) {
                ret = Float.parseFloat(value);
            }
            else
                ret = value;
        }
        return (T) ret;
    }

    /**
     * Convert string to class instance.
     * 
     * @param classTypeName
     *            the target class type name, e.g. "java.lang.Integer"
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(String value, String classTypeName) {
        Object ret = null;
        ParseValue p = classTypeName == null ? null : commonTypeStringMap.get(classTypeName);
        if (p != null)
            ret = p.parse(value);
        else {
            if (MvcUtils.VerifyUtil.isInteger(value)) {
                ret = Integer.parseInt(value);
            }
            else if (MvcUtils.VerifyUtil.isLong(value)) {
                ret = Long.parseLong(value);
            }
            else if (MvcUtils.VerifyUtil.isDouble(value)) {
                ret = Double.parseDouble(value);
            }
            else if (MvcUtils.VerifyUtil.isFloat(value)) {
                ret = Float.parseFloat(value);
            }
            else
                ret = value;
        }
        return (T) ret;
    }

    // ~~ helper method for this util

    private static final Map<Class<?>, ParseValue> commonTypeClassMap = new HashMap<Class<?>, ParseValue>();
    private static final Map<String, ParseValue> commonTypeStringMap = new HashMap<String, ParseValue>();

    public enum COMMON_TYPE {

        INT("java.lang.Integer"), LONG("java.lang.Long"), DOUBLE("java.lang.Double"), FLOAT(
                "java.lang.Float"), SHORT("java.lang.Short"), BYTE("java.lang.Byte"), CHAR(
                "java.lang.Character"), BOOLEAN("java.lang.Boolean"), STRING("java.lang.String");

        private final String value;

        private COMMON_TYPE(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getLowerCaseName() {
            return this.name().toLowerCase();
        }

        public static COMMON_TYPE findByName(String name) {
            for (COMMON_TYPE c : COMMON_TYPE.values()) {
                if (c.name().equalsIgnoreCase(name)) {
                    return c;
                }
            }
            return null;
        }
    }

    static {
        ParseValue p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return Integer.parseInt(value);
            }
        };
        commonTypeClassMap.put(int.class, p);
        commonTypeClassMap.put(Integer.class, p);
        commonTypeStringMap.put(COMMON_TYPE.INT.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.INT.getValue(), p);

        p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return Long.parseLong(value);
            }
        };
        commonTypeClassMap.put(long.class, p);
        commonTypeClassMap.put(Long.class, p);
        commonTypeStringMap.put(COMMON_TYPE.LONG.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.LONG.getValue(), p);

        p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return Double.parseDouble(value);
            }
        };
        commonTypeClassMap.put(double.class, p);
        commonTypeClassMap.put(Double.class, p);
        commonTypeStringMap.put(COMMON_TYPE.DOUBLE.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.DOUBLE.getValue(), p);

        p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return Float.parseFloat(value);
            }
        };
        commonTypeClassMap.put(float.class, p);
        commonTypeClassMap.put(Float.class, p);
        commonTypeStringMap.put(COMMON_TYPE.FLOAT.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.FLOAT.getValue(), p);

        p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return Short.parseShort(value);
            }
        };
        commonTypeClassMap.put(short.class, p);
        commonTypeClassMap.put(Short.class, p);
        commonTypeStringMap.put(COMMON_TYPE.SHORT.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.SHORT.getValue(), p);

        p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return Byte.parseByte(value);
            }
        };
        commonTypeClassMap.put(byte.class, p);
        commonTypeClassMap.put(Byte.class, p);
        commonTypeStringMap.put(COMMON_TYPE.BYTE.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.BYTE.getValue(), p);

        p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return value.charAt(0);
            }
        };
        commonTypeClassMap.put(char.class, p);
        commonTypeClassMap.put(Character.class, p);
        commonTypeStringMap.put(COMMON_TYPE.CHAR.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.CHAR.getValue(), p);

        p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return Boolean.parseBoolean(value);
            }
        };
        commonTypeClassMap.put(boolean.class, p);
        commonTypeClassMap.put(Boolean.class, p);
        commonTypeStringMap.put(COMMON_TYPE.BOOLEAN.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.BOOLEAN.getValue(), p);

        p = new ParseValue() {
            @Override
            public Object parse(String value) {
                return value;
            }
        };
        commonTypeClassMap.put(String.class, p);
        commonTypeStringMap.put(COMMON_TYPE.STRING.getLowerCaseName(), p);
        commonTypeStringMap.put(COMMON_TYPE.STRING.getValue(), p);
    }

    private interface ParseValue {
        Object parse(String value);
    }

}