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

/**
 * 
 * @author zollty
 * @since 2012-12-26
 */
abstract public class MvcVerifyUtils {

	public static boolean isNumeric(String str) {
		if (isEmpty(str))
			return false;
		
		char first = str.charAt(0);
		int i = first == '-' ? 1 : 0;
		for (; i < str.length(); i++) {
			if (isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isInteger(String str) {
		if (isEmpty(str))
			return false;
		
		char first = str.charAt(0);
		int i = first == '-' ? 1 : 0;
		for (; i < str.length(); i++) {
			if (isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		
		Long t = Long.parseLong(str);
		return t <= Integer.MAX_VALUE && t >= Integer.MIN_VALUE;
	}
	
	public static boolean isLong(String str) {
		if (isEmpty(str))
			return false;
		
		char first = str.charAt(0);
		char end = str.charAt(str.length() - 1);
		boolean j = end == 'l' || end == 'L';
		int i = first == '-' ? 1 : 0;
		int len = j ? str.length() - 1 : str.length();
		for (; i < len; i++) {
			if (isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		
		if(!j) {
			Long t = Long.parseLong(str);
			return t > Integer.MAX_VALUE || t < Integer.MIN_VALUE;
		} else {
			return true;
		}
	}
	
	public static boolean isFloat(String str) {
		if (isEmpty(str))
			return false;
		
		char end = str.charAt(str.length() - 1);
		if(!(end == 'f' || end == 'F' ))
			return false;
		
		int point = 0;
		int i = str.charAt(0) == '-' ? 1 : 0;
		for (; i < str.length() - 1; i++) {
			char c = str.charAt(i);
			if(c == '.') {
				point++;
			} else if (MvcVerifyUtils.isDigit(c) == false) {
				return false;
			}
		}
		return point == 1 || point == 0;
	}
	
	public static boolean isDouble(String str) {
		if (isEmpty(str))
			return false;

		int point = 0;
		int i = str.charAt(0) == '-' ? 1 : 0;
		for (; i < str.length(); i++) {
			char c = str.charAt(i);
			if(c == '.') {
				point++;
			} else if (isDigit(c) == false) {
				return false;
			}
		}
		
		return point == 1;
	}

	public static boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}
	
	private static boolean isEmpty(String o) {
		return 	MvcUtils.StringUtil.isBlank(o);
	}

}
