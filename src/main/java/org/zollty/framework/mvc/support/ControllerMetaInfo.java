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
 * Zollty Framework MVC Source Code - Since v1.0
 * Author(s): 
 * Zollty Tsou (zolltytsou@gmail.com, http://blog.zollty.com)
 */
package org.zollty.framework.mvc.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.annotation.HttpParam;
import org.zollty.framework.mvc.annotation.URIParam;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.handler.HandlerMetaInfo;
import org.zollty.framework.util.MvcConvertUtils;
import org.zollty.framework.util.MvcReflectUtils;

/**
 * @author zollty 
 * @since 2013-9-21
 */
public class ControllerMetaInfo extends HandlerMetaInfo {

	// @HttpParam标注的类的元信息
	private final ParamMetaInfo[] paramMetaInfos;
	private final BasicParamMetaInfo[] bParamMetas;
	private final String[] allowHttpMethods;
	
    private final String servletURI;
	
	private List<String> paramsName;
	
	public String getServletURI(){
	    return servletURI;
	}
	
	
	public ControllerMetaInfo(Object object, Method method, String[] allowHttpMethods, String servletURI, MvcBeforeBeanDefinition[] beforeInvoke) {
		super(object, method);
		this.allowHttpMethods = allowHttpMethods;
		this.servletURI = servletURI;
		this.beforeInvoke = beforeInvoke;
		
		Class<?>[] paraTypes = method.getParameterTypes();
		
		// 构造参数对象
		paramMetaInfos = new ParamMetaInfo[paraTypes.length];
		bParamMetas =  new BasicParamMetaInfo[paraTypes.length];
		Annotation[][] annotations = method.getParameterAnnotations();
		for (int i = 0; i < paraTypes.length; i++) {
			Annotation anno = getAnnotation(annotations[i]);
			if (anno != null) {
				if(anno.annotationType().equals(HttpParam.class)) {
					HttpParam httpParam = (HttpParam) anno;
                    if (MvcConvertUtils.canConvert(paraTypes[i]) != null) {
                        // 数据类型为主要类型，可以直接转换，比如int、Long等
                        bParamMetas[i] = new BasicParamMetaInfo(paraTypes[i], httpParam.value(), httpParam.setAttr());
                        paramType[i] = BasicParamMetaInfo.HTTP_SIMPLE_VALUE;
                    } else{
                        // 数据类型为复合类型，需要调用标准setter赋值
						ParamMetaInfo paramMetaInfo = new ParamMetaInfo(paraTypes[i], 
								MvcReflectUtils.getSetterMethods(paraTypes[i]), 
								httpParam.value());
						paramMetaInfos[i] = paramMetaInfo;
						paramType[i] = BasicParamMetaInfo.HTTP_BEAN;
					}
				} else if(anno.annotationType().equals(URIParam.class)) {
				    if ( MvcConvertUtils.canConvert(paraTypes[i])!=null ){
				        URIParam pv = (URIParam) anno;
                        bParamMetas[i] = new BasicParamMetaInfo(paraTypes[i], pv.value(), false);
                        paramType[i] = BasicParamMetaInfo.URI_PARAM;
                    }
				}
			} else {
				if (paraTypes[i].equals(HttpServletRequest.class)){
				    paramType[i] = BasicParamMetaInfo.REQUEST;
				} else if (paraTypes[i].equals(HttpServletResponse.class)) {
				    paramType[i] = BasicParamMetaInfo.RESPONSE;
				}
			}
		}
	}
	
    private Annotation getAnnotation(Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (a.annotationType().equals(HttpParam.class) || a.annotationType().equals(URIParam.class))
                return a;
        }
        return null;
    }

    public ParamMetaInfo[] getParamMetaInfos() {
        return paramMetaInfos;
    }

    public boolean allowMethod(String method) {
        for (String s : allowHttpMethods) {
            if (s.equals(method))
                return true;
        }
        return false;
    }
    
    public String[] getAllowHttpMethods() {
        return allowHttpMethods;
    }
	
    public String getAllowMethod() {
        StringBuilder s = new StringBuilder();
        for (String m : allowHttpMethods) {
            s.append(m).append(',');
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

	@Override
	public String toString() {
	    return "ControllerMetaInfo [Class="+object.getClass().getSimpleName()+"#"+method.getName()+", URI="+servletURI+", Method=" + getAllowMethod() + "]";
	}

	/**
	 * @return the bParamMetas
	 */
	public BasicParamMetaInfo[] getbParamMetas() {
		return bParamMetas;
	}


    public List<String> getParamsName() {
        return paramsName;
    }


    public void setParamsName(List<String> paramsName) {
        this.paramsName = paramsName;
    }

}
