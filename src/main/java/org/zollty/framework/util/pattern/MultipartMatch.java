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
public class MultipartMatch extends Pattern {
	
	private final boolean startWith, endWith;
	private final String[] parts;
	private int num;

	public MultipartMatch(boolean startWith, boolean endWith, String[] parts) {
		super();
		this.startWith = startWith;
		this.endWith = endWith;
		this.parts = parts;
		num = parts.length - 1;
		if(startWith)
			num++;
		if(endWith)
			num++;
	}

	@Override
	public String[] match(String str) {
		int currentIndex = -1;
		int lastIndex = -1;
		String[] ret = new String[num];
		
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			int j = startWith ? i : i - 1;
			currentIndex = str.indexOf(part, lastIndex + 1);
			
			if (currentIndex > lastIndex) {
				if(i != 0 || startWith)
					ret[j] = str.substring(lastIndex + 1, currentIndex);
				
				lastIndex = currentIndex + part.length() - 1;
				continue;
			}
			return null;
		}
		
		if(endWith)
			ret[num - 1] = str.substring(lastIndex + 1);
		return ret;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		for(String s: parts){
			result += s.hashCode();
		}
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
		MultipartMatch other = (MultipartMatch) obj;
		if (parts == null) {
			if (other.parts != null)
				return false;
		} else {
			if( other.parts.length!=parts.length )
				return false;
			else{
				for(int i=0;i<parts.length; i++){
					if( !parts[i].equals(other.parts[i]) )
						return false;
				}
			}
		}
		return true;
	}

}
