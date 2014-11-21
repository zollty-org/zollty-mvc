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
package org.zollty.framework.mvc.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zollty.framework.core.config.ConfigReader;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.mvc.annotation.RequestMapping;
import org.zollty.framework.mvc.handler.support.ControllerResource;
import org.zollty.framework.mvc.handler.support.ErrorHandler;
import org.zollty.framework.mvc.handler.support.InterceptorResource;
import org.zollty.framework.mvc.support.ControllerBeanDefinition;
import org.zollty.framework.mvc.support.ControllerMetaInfo;
import org.zollty.framework.mvc.support.InterceptorBeanDefinition;
import org.zollty.framework.mvc.support.InterceptorMetaInfo;
import org.zollty.framework.mvc.view.HtmlView;
import org.zollty.framework.mvc.view.JsonView;
import org.zollty.framework.mvc.view.JspView;
import org.zollty.framework.mvc.view.TextView;
import org.zollty.framework.util.MvcRuntimeException;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty 
 * @since 2013-9-15
 */
abstract public class AbstractHandlerMapping implements HandlerMapping {

	private Logger log = LogFactory.getLogger(AbstractHandlerMapping.class);
	
	protected final ControllerResource controllerResource;
	
	protected final InterceptorResource interceptorResource;
	
	public AbstractHandlerMapping(List<BeanDefinition> beanDefinitions){
		String encoding = ConfigReader.getInstance().getEncoding();
		interceptorResource = new InterceptorResource();
		controllerResource = new ControllerResource();
		initContext(beanDefinitions, encoding);
	}
	
	private void initContext(List<BeanDefinition> beanDefinitions, String encoding) {
	    List<InterceptorMetaInfo> interceptorList = new LinkedList<InterceptorMetaInfo>();
		for (BeanDefinition beanDef : beanDefinitions) {
			if (beanDef instanceof ControllerBeanDefinition) {
				ControllerBeanDefinition beanDefinition = (ControllerBeanDefinition) beanDef;
				List<Method> list = beanDefinition.getReqMethods();
				if (list != null) {
					for (Method m : list) {
						m.setAccessible(true);
						
						String value = m.getAnnotation(RequestMapping.class).value();
						String[] array = value.split(":");
						
						String[] allowHttpMethods = null;
						String uri = null;
						if(array.length==1){
							uri = value;
							allowHttpMethods = new String[]{"GET","POST","PUT","DELETE"};
						}else{ //length==2
							allowHttpMethods = array[0].split("\\|");
							uri = array[1].trim();
						}
						if(MvcUtils.StringUtil.isBlank(uri)){
                            throw new IllegalArgumentException("controller uri can't be blank! Bean="+beanDefinition.getClassName()+", method="+m.getName());
                        }
						try {
                            controllerResource.addController( new ControllerMetaInfo(beanDefinition.getObject(), m, allowHttpMethods, uri) );
                            if (LogFactory.isDebugEnabled()) {
                                log.debug("controller uri: {} {}", uri, Arrays.toString(allowHttpMethods));
                            }
						} catch (MvcRuntimeException e){
						    throw new MvcRuntimeException(e, "[Bean="+beanDefinition.getClassName()+", method="+m.getName()+"] ");
						}
					}
				}
			} else if (beanDef instanceof InterceptorBeanDefinition) {
				InterceptorBeanDefinition beanDefinition = (InterceptorBeanDefinition) beanDef;
				if(beanDefinition.getDisposeMethod() != null) {
					beanDefinition.getDisposeMethod().setAccessible(true);
					InterceptorMetaInfo interceptor = new InterceptorMetaInfo(beanDefinition.getObject(), 
							beanDefinition.getDisposeMethod(),
							beanDefinition.getUriPattern(), 
							beanDefinition.getOrder());
					interceptorList.add(interceptor);
					if(LogFactory.isDebugEnabled()){
						for(String uri: beanDefinition.getUriPattern()){
							log.debug("interceptor uri: {} ({})",uri,beanDefinition.getClassName());
						}
					}
				}
			}
		}
		interceptorResource.addInterceptor(interceptorList);
        // if(interceptorList.size() > 0)
        // Collections.sort(interceptorList);
		
		TextView.setEncoding(encoding);
		HtmlView.setEncoding(encoding);
		JsonView.setEncoding(encoding);
		JspView.setViewPath(ConfigReader.getInstance().getConfig().getViewPath());
		ErrorHandler.setErrorPage(ConfigReader.getInstance().getConfig().getErrorPagePath());
	}
	
    /**
     * @return the excludeprefix
     */
    @Override
    public Set<String> getExcludePrefixes() {
        return ConfigReader.getInstance().getConfig().getExcludePrefixes();
    }
    
    /**
     * @return the excludeSuffix
     */
    @Override
    public Set<String> getExcludeSuffixes() {
        return ConfigReader.getInstance().getConfig().getExcludeSuffixes();
    }

}
