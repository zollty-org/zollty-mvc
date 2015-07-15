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

/**
 * @author zollty 
 * @since 2013-10-23
 */
public class HeadAndTailMatch extends Pattern {

	private final String part;

	public HeadAndTailMatch(String part) {
		this.part = part;
	}
	
	@Override
	public String[] match(String str) {
		int currentIndex = str.indexOf(part);
		if(currentIndex >= 0) {
			String[] ret = new String[]{str.substring(0, currentIndex),
					str.substring(currentIndex + part.length(), str.length()) };
			return ret;
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((part == null) ? 0 : part.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeadAndTailMatch other = (HeadAndTailMatch) obj;
		if (part == null) {
			if (other.part != null)
				return false;
		} else if (!part.equals(other.part))
			return false;
		return true;
	}

}
