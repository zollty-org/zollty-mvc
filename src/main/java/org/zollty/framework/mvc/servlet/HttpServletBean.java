/* @(#)HttpServletBean.java 
 * Copyright (C) 2013-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by zollty on 2013-6-02 [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.zollty.framework.core.config.ConfigReader;
import org.zollty.framework.mvc.context.ContextLoader;
import org.zollty.framework.mvc.context.WebApplicationContext;
import org.zollty.framework.mvc.context.WebApplicationContextUtils;
import org.zollty.framework.mvc.context.support.WebAnnotationAndXmlApplicationContext;
import org.zollty.framework.mvc.handler.HandlerMapping;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty 
 * @since 2013-6-02
 */
@SuppressWarnings("serial")
abstract public class HttpServletBean extends HttpServlet {
	
	private Logger log = LogFactory.getLogger(HttpServletBean.class);
	
	/** WebApplicationContext for this servlet */
	protected WebApplicationContext webApplicationContext;
	
	protected WebContainerDefaultServlet defaultServlet;
	
	protected String encoding;
	
	protected HandlerMapping handlerMapping;
	
//	/** 排除拦截 的前缀 */
//	protected Set<String> excludePrefixes = new HashSet<String>();
//	
//	/** 排除拦截 的后缀 */
//	protected Set<String> excludeSuffixes = new HashSet<String>();
	
	@Override
	public final void init() throws ServletException {
		
		log.debug("Initializing servlet '" + getServletName() + "'");
		
		if(webApplicationContext == null){
			webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			if(webApplicationContext == null){
				new ContextLoader().initWebApplicationContext(getServletContext());
				webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			}
		}
		
		defaultServlet = new WebContainerDefaultServlet(getServletContext());
		
		encoding = ConfigReader.getInstance().getConfig().getEncoding();
		
		initDispatcher();
		
		finalInit();
		
		log.debug("Servlet '" + getServletName() + "' configured successfully");
	}
	
	
	private void initDispatcher() {
		
		WebAnnotationAndXmlApplicationContext awac = (WebAnnotationAndXmlApplicationContext) webApplicationContext;
		
		handlerMapping = awac.getHandlerMapping();
		
//		// 前缀，为空则不拦截
//		boolean bt = false;
//		String[] tarry = null;
//		String exclude = handlerMapping.getExcludePrefix();
//		if( exclude != null ){
//			exclude = exclude.replaceAll(" ", "");
//			if(exclude.length()!=0){
//				bt = true;
//			}
//		}
//		
//		if(bt){
//			tarry = MvcUtils.StringSplitUtil.split(exclude,',');
//			// 检测前缀
//			String add = "/"; String tmp = null;
//			for(int i=0;i<tarry.length;i++){
//				tmp = tarry[i];
//				if( tmp.charAt(0)=='/' )
//					excludePrefixes.add(tmp);
//				else
//					excludePrefixes.add( add + tmp);
//			}
//		}
//		
//		// 后缀，取配置值或者默认值
//		bt = false;
//		String exclude2 = handlerMapping.getExcludeSuffix();
//		if( exclude2 != null ){
//			exclude2 = exclude2.replaceAll(" ", "");
//			if(exclude2.length()!=0){
//				bt = true;
//			}
//		}
//		if( bt ){
//			tarry =  MvcUtils.StringSplitUtil.split(exclude2,',');
//			excludeSuffixes.addAll(MvcConvertUtils.arrayToSet(tarry));
//		}
//		
//		if(LogFactory.isDebugEnabled()){
//			StringBuilder sb = new StringBuilder(40);
//			sb.append("exclude = [{");
//			for(String prefix: this.excludePrefixes){
//				sb.append(prefix).append("},{");
//			}
//			for(String suffix: this.excludeSuffixes){
//				sb.append(suffix).append("},{");
//			}
//			sb.append("}]");
//			int index = sb.lastIndexOf(",{}]");
//			if(index!=-1){
//				sb = new StringBuilder(sb.substring(0,index));
//				sb.append("]");
//			}
//			
//			log.debug("no-intercept - "+sb.toString());
//		}
	}
	
	
	abstract public void finalInit();

}
