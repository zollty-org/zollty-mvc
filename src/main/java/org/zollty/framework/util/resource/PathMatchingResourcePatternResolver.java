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
package org.zollty.framework.util.resource;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.zollty.framework.core.config.ConfigReader;
import org.zollty.framework.util.MvcUtils;

/**
 * @author zollty 
 * @since 2013-10-12
 */
public class PathMatchingResourcePatternResolver {

	public static final String CLASSPATH_URL_PREFIX   = "classpath:";
	public static final String CONTEXTPATH_URL_PREFIX = "contextpath:";
	public static final String LOCALPATH_URL_PREFIX   = "localpath:";
	
	public static InputStream getResourceInputStream(String resourceLocation) {
		return getResourceInputStream(resourceLocation, MvcUtils.ClassUtil.getDefaultClassLoader());
	}
	/**
	 * 支持:
	 * <import resource="classpath:bean/test.xml" />
	 * <import resource="localpath:D:/database/schema.xml"/>
	 * <import resource="contextpath:WEB-INF/database/schema.xml"/>
	 * 
	 * <import resource="WEB-INF/database/schema.xml"/>
	 * <import resource="bean/test.xml"/>
	 * 
	 * 当不指定前缀时,解析顺序为: 
	 * classpath--first 
	 * contextpath---second
	 * localpath---last
	 * 
	 * 暂不支持：
	 * <import resource="file:../dev-project/src/main/webconfig/*.xml />
	 * <import resource="relative:../database/schema-*.xml"/>
	 * see Spring PathMatchingResourcePatternResolver
	 * 
	 * @author zollty
	 */
	public static InputStream getResourceInputStream(String resourceLocation, ClassLoader classLoader) {
		if( null== resourceLocation){
			return null;
		}
		String path = null;
		InputStream is = null;
		if(resourceLocation.startsWith(CLASSPATH_URL_PREFIX)){
			path = resourceLocation.substring( CLASSPATH_URL_PREFIX.length() ).trim();
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			return classLoader.getResourceAsStream(path);
		}
		if(resourceLocation.startsWith(CONTEXTPATH_URL_PREFIX)){
			path = resourceLocation.substring( CONTEXTPATH_URL_PREFIX.length() ).trim().replaceAll("%20", " ");
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			path = ConfigReader.getInstance().getContextRealPath() + path;
			try{
				return new BufferedInputStream( new FileInputStream(path) );
			}catch (IOException e) {
				throw new RuntimeException(resourceLocation+ " can't be resovle! ");
			}
		}
		if(resourceLocation.startsWith(LOCALPATH_URL_PREFIX)){
			path = resourceLocation.substring( LOCALPATH_URL_PREFIX.length() ).trim();
			try{
				return new BufferedInputStream( new FileInputStream(path) );
			}catch (IOException e) {
				throw new RuntimeException(resourceLocation+ " can't be resovle! ");
			}
		}
		
		path = resourceLocation.trim();
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		
		// first find it at classpath
		is = classLoader.getResourceAsStream(path);
		if( is!=null ){
			return is;
		}
		
		// second find it at contextPath
		try{
			String contextPath = ConfigReader.getInstance().getContextRealPath();
			if( null!= contextPath ){
				return new BufferedInputStream( new FileInputStream(contextPath + path) );
			}
		}catch (IOException e) {
			// egnore
		}
		
		// third find it at absolute local path
		try{
			return new BufferedInputStream( new FileInputStream(path) );
		}catch (IOException e) {
			// egnore
		}
		
		throw new RuntimeException(resourceLocation+ " can't be resovle! ");
	}

}
