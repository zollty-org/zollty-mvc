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
package org.zollty.framework.mvc.context.support;

import java.util.List;

import javax.servlet.ServletContext;

import org.zollty.framework.core.config.ConfigReader;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.xml.XmlBeanReader;
import org.zollty.framework.mvc.handler.HandlerMapping;
import org.zollty.framework.mvc.handler.HttpServletDispatcherHandler;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty 
 * @since 2013-10-11
 */
public class WebXmlApplicationContext extends AbstractWebApplicationContext{

    private Logger log;
    
    private HandlerMapping handlerMapping;
    
    public WebXmlApplicationContext(String configLocation,
            ServletContext servletContext){
        this(configLocation, servletContext, null);
    }
    
    public WebXmlApplicationContext(String configLocation,
            ServletContext servletContext, ClassLoader beanClassLoader){
        super(configLocation, servletContext, beanClassLoader);
        refresh();
    }
    
    private long beginTimeMs;
    @Override
    protected void doBeforeRefresh(){
        beginTimeMs = System.currentTimeMillis();
        log = LogFactory.getLogger(getClass());
        if(LogFactory.isDebugEnabled()){
            log.debug("load {} ...", getClass().getSimpleName());
        }
        ConfigReader.getInstance().load(getConfigLocation(), getServletContext().getRealPath("/"), getBeanClassLoader());
    }
    
    @Override
    protected void doAfterRefresh(){
      if(LogFactory.isDebugEnabled()){
          log.debug("{} completed in {} ms.", getClass().getSimpleName(), (System.currentTimeMillis()-beginTimeMs));
      }
      handlerMapping = new HttpServletDispatcherHandler(beanDefinitions);
    }


    @Override
    protected List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> list1 = new XmlBeanReader( getBeanClassLoader() ).loadBeanDefinitions();
        if (list1 != null) {
            log.debug("-- WebXml bean --");
            return list1;
        }
        return null;
    }
    
    /**
     * @return the handlerMapping
     */
    public final HandlerMapping getHandlerMapping() {
        return handlerMapping;
    }

}
