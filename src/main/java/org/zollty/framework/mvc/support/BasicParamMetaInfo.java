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
package org.zollty.framework.mvc.support;

import javax.servlet.http.HttpServletRequest;

import org.zollty.framework.util.MvcConvertUtils;

/**
 * @author zollty 
 * @since 2013-9-21
 */
public class BasicParamMetaInfo {
    
    public static final byte REQUEST = 0x00;
    public static final byte RESPONSE = 0x01;
    public static final byte HTTP_BEAN = 0x02;
    public static final byte URI_PARAM = 0x03;
    public static final byte HANDLER_CHAIN = 0x04;
    public static final byte HTTP_SIMPLE_VALUE = 0x05; // 基础数据类型参数
    public static final byte INTERCEPTOR_INFO = 0x06;

	private final Class<?> paramClass;
	private final String attribute;
	private final boolean setAttr;
	private String orgValue;
	
	public BasicParamMetaInfo(Class<?> paramClass, String attribute, boolean setAttr){
		this.paramClass = paramClass;
		this.attribute = attribute;
		this.setAttr = setAttr;
	}

	/**
	 * @return the paramClass
	 */
	public Class<?> getParamClass() {
		return paramClass;
	}

	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}
	
	/**
	 * @return the setAttr
	 */
	public boolean isSetAttr() {
		return setAttr;
	}
	
	/**
	 * @return the orgValue
	 */
	public String getOrgValue() {
		return orgValue;
	}
	
	public Object getValue(HttpServletRequest request){
		this.orgValue = request.getParameter(attribute);
		return MvcConvertUtils.convert(this.orgValue, this.getParamClass());
	}
	
}
