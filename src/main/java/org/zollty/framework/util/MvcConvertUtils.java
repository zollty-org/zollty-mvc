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

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.zollty.framework.util.collection.IdentityHashMap;
import org.zollty.util.NestedRuntimeException;


/**
 * 
 * @author zollty
 * @since 2013-9-11
 */
public class MvcConvertUtils {

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, Class<T> c) {
		Object ret = null;
		ParseValue p = c == null ? null : commonTypeClassMap.get(c);
		if (p != null)
			ret = p.parse(value);
		else {
			if (MvcVerifyUtils.isInteger(value)) {
				ret = Integer.parseInt(value);
			} else if (MvcVerifyUtils.isLong(value)) {
				ret = Long.parseLong(value);
			} else if (MvcVerifyUtils.isDouble(value)) {
				ret = Double.parseDouble(value);
			} else if (MvcVerifyUtils.isFloat(value)) {
				ret = Float.parseFloat(value);
			} else
				ret = value;
		}
		return (T) ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, String typeName) {
		Object ret = null;
		ParseValue p = typeName == null ? null : commonTypeStringMap.get(typeName);
		if (p != null)
			ret = p.parse(value);
		else {
			if (MvcVerifyUtils.isInteger(value)) {
				ret = Integer.parseInt(value);
			} else if (MvcVerifyUtils.isLong(value)) {
				ret = Long.parseLong(value);
			} else if (MvcVerifyUtils.isDouble(value)) {
				ret = Double.parseDouble(value);
			} else if (MvcVerifyUtils.isFloat(value)) {
				ret = Float.parseFloat(value);
			} else
				ret = value;
		}
		return (T) ret;
	}
	
	
	/**
	 * 根据类型自动返回一个集合
	 */
	@SuppressWarnings("rawtypes")
	//  static <T> Collection<T> newCollection(Class<?> clazz) {
	public static Collection getCollectionObj(Class<?> clazz) {
		if (clazz.isInterface()) {
			if (clazz.isAssignableFrom(List.class))
				return new ArrayList();
			else if (clazz.isAssignableFrom(Set.class))
				return new HashSet();
			else if (clazz.isAssignableFrom(Queue.class))
				return new ArrayDeque();
			else if (clazz.isAssignableFrom(SortedSet.class))
				return new TreeSet();
			else if (clazz.isAssignableFrom(BlockingQueue.class))
				return new LinkedBlockingDeque();
			else
				return null;
		} else {
			Collection collection = null;
			try {
				collection = (Collection) clazz.newInstance();
			} catch (Exception e) {
				throw new NestedRuntimeException(e);
			}
			return collection;
		}
	}
	
	/**
	 * 根据类型自动返回一个Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map getMapObj(Class<?> clazz) {
		if (clazz.isInterface()) {
			if (clazz.isAssignableFrom(Map.class))
				return new HashMap();
			else if (clazz.isAssignableFrom(ConcurrentMap.class))
				return new ConcurrentHashMap();
			else if (clazz.isAssignableFrom(SortedMap.class))
				return new TreeMap();
			else if (clazz.isAssignableFrom(NavigableMap.class))
				return new TreeMap();
			else if (clazz.isAssignableFrom(ConcurrentNavigableMap.class))
				return new ConcurrentSkipListMap();
			else
				return null;
		} else {
			Map map = null;
			try {
				map = (Map) clazz.newInstance();
			} catch (Exception e) {
				throw new NestedRuntimeException(e);
			}
			return map;
		}
	}
	
	public static <T> Set<T> arrayToSet(T... array) {
        Set<T> set = new HashSet<T>();
        for (int i = 0; i < array.length; i++) {
			set.add(array[i]);
		}
        return set;
	 }

//	public static <T> Enumeration<T> enumeration(Collection<T> col) {
//		final Iterator<T> it = col.iterator();
//		return new Enumeration<T>() {
//			public boolean hasMoreElements() {
//				return it.hasNext();
//			}
//
//			public T nextElement() {
//				return it.next();
//			}
//		};
//	}
	
	/**
	 * 把集合转换为指定类型的数组
	 * 
	 * @param collection
	 * @param arrayType
	 */
	public static Object convert(Collection<?> collection, Class<?> arrayType) {
		int size = collection.size();
		// Allocate a new Array
		Iterator<?> iterator = collection.iterator();
		Class<?> componentType = null;

		if (arrayType == null) {
			componentType = Object.class;
		} else {
			if (!arrayType.isArray())
				throw new IllegalArgumentException("type is not a array");
			componentType = arrayType.getComponentType();
		}
		Object newArray = Array.newInstance(componentType, size);

		// Convert and set each element in the new Array
		for (int i = 0; i < size; i++) {
			Object element = iterator.next();
			// log.debug("element value [{}], type [{}]", element, element
			Array.set(newArray, i, element);
		}

		return newArray;
	}
	
	public static ParseValue canConvert(Class<?> clazz){
	    return clazz == null ? null : commonTypeClassMap.get(clazz);
	}
	
	private static final IdentityHashMap<Class<?>, ParseValue> commonTypeClassMap = new IdentityHashMap<Class<?>, ParseValue>();
	private static final Map<String, ParseValue> commonTypeStringMap = new HashMap<String, ParseValue>();

	
    public enum COMMON_TYPE {
        INT("java.lang.Integer"), LONG("java.lang.Long"), DOUBLE("java.lang.Double"), 
        FLOAT("java.lang.Float"), SHORT("java.lang.Short"), BYTE("java.lang.Byte"), 
        CHAR("java.lang.Character"), BOOLEAN("java.lang.Boolean"), STRING("java.lang.String");
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
        
        public static COMMON_TYPE findByName(String name){
            for (COMMON_TYPE c : COMMON_TYPE.values()) {
                if(c.name().equalsIgnoreCase(name)){
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

	interface ParseValue {
		Object parse(String value);
	}
}