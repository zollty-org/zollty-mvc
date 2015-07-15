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
package org.zollty.framework.core.context.support;

import java.util.List;

import org.zollty.framework.core.Const;
import org.zollty.framework.core.config.ConfigReader;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.annotation.AnnotationBeanReader;
import org.zollty.framework.core.support.xml.XmlBeanReader;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty 
 * @since 2013-10-11
 */
public class ClassPathAnnotationAndXmlApplicationContext extends AbstractApplicationContext {

	private Logger log;
	
	public ClassPathAnnotationAndXmlApplicationContext(){
	    this(Const.DEFAULT_CONFIG_LOCATION, null);
	}
	
	public ClassPathAnnotationAndXmlApplicationContext(ClassLoader beanClassLoader){
	    this(Const.DEFAULT_CONFIG_LOCATION, beanClassLoader);
	}
	
	public ClassPathAnnotationAndXmlApplicationContext(String configLocation){
		this(configLocation, null);
	}

	public ClassPathAnnotationAndXmlApplicationContext(String configLocation, ClassLoader beanClassLoader){
	    super(configLocation, beanClassLoader);
//	    long begin = System.currentTimeMillis();
//		ConfigReader.getInstance().load(configLocation, beanClassLoader);
//		setBeanClassLoader(beanClassLoader);
//		refresh();
//		BeanFactoryHelper.setBeanFactory(this);
//		if(LogFactory.isTraceEnabled())
//			log.trace("ClassPathAnnotationAndXmlApplicationContext completed in "+ (System.currentTimeMillis()-begin) +" ms.");
	    refresh();
	}
	
	private long beginTimeMs;
    @Override
    protected void doBeforeRefresh() {
        beginTimeMs = System.currentTimeMillis();
        log = LogFactory.getLogger(getClass());
        if (LogFactory.isDebugEnabled()) {
            log.debug("load {} ...", getClass().getSimpleName());
        }
        ConfigReader.getInstance().load(getConfigLocation(), getBeanClassLoader());
    }

    @Override
    protected void doAfterRefresh() {
        if (LogFactory.isDebugEnabled()) {
            log.debug("{} completed in {} ms.", getClass().getSimpleName(), (System.currentTimeMillis() - beginTimeMs));
        }
    }
	
	@Override
	protected List<BeanDefinition> loadBeanDefinitions() {
		List<BeanDefinition> list1 = new AnnotationBeanReader( getBeanClassLoader() ).loadBeanDefinitions(); 
		List<BeanDefinition> list2 = new XmlBeanReader( getBeanClassLoader() ).loadBeanDefinitions();
		if (list1 != null && list2 != null) {
			log.trace(" [AnnotationBean & XmlBean] ");
			list1.addAll(list2);
			return list1;
		} else if (list1 != null) {
			log.trace(" [-- Annotation beans --] ");
			return list1;
		} else if (list2 != null) {
			log.trace(" [-- Xml beans --] ");
			return list2;
		}
		return null;
	}

}
