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
package org.zollty.framework.util.pattern;

import org.zollty.framework.util.MvcUtils;

/**
 * @author zollty 
 * @since 2013-9-11
 */
public abstract class Pattern {
	
	private static final AllMatch ALL_MATCH = new AllMatch();
	
	/**
	 * 根据模式匹配字符串
	 * @param str 进行匹配的字符串
	 * @return 返回null表示匹配失败，否则返回匹配的字符串数组
	 */
	abstract public String[] match(String str);
	
	public static Pattern compile(String pattern, String wildcard) {
		final boolean startWith = pattern.startsWith(wildcard);
		final boolean endWith = pattern.endsWith(wildcard);
		
		final String[] array = MvcUtils.StringSplitUtil.split(pattern, wildcard);
		
		switch (array.length) {
		case 0:
			return ALL_MATCH; // pattern全是通配符
		case 1:
			if (startWith && endWith)
				return new HeadAndTailMatch(array[0]);
			
			if (startWith)
				return new HeadMatch(array[0]);
			
			if (endWith)
				return new TailMatch(array[0]);
			
			return new EqualsMatch(pattern); // pattern不包含通配符
		default:
			return new MultipartMatch(startWith, endWith, array);
		}
	}

}
