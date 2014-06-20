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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.handler.support.ControllerHandler;
import org.zollty.framework.util.pattern.MvcUrlPattern;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.BasicRuntimeException;

/**
 * @author zollty
 * @since 2014-5-29
 */
public class PattenControllerHandler {
    
    private static Logger LOG = LogFactory.getLogger(PattenControllerHandler.class);
    
    private final ControllerMetaInfo controller;
    private MvcUrlPattern pattern;
    private final List<String> paramsName;
    
    /**
     * @param controller
     * @param paramsName
     */
    public PattenControllerHandler(ControllerMetaInfo controller, List<String> paramsName) {
        this.controller = controller;
        this.paramsName = paramsName;
        String pstr = controller.getServletURI();
        for(String str: paramsName){
            pstr = pstr.replace("{"+str+"}", "*");
            pstr = pstr.replace("["+str+"]", "**");
        }
        if(pstr.indexOf("***")!=-1){
            throw new BasicRuntimeException("URI definition error, any two variables can't be connected. such as /{v1}{v2}/ is BAD. /{v1}-{v2}/ is OK.");
        }
        pattern = new MvcUrlPattern(pstr);
        if(LogFactory.isDebugEnabled()){
            LOG.debug("URI Real Pattern={}", pstr);
        }
    }
    
    public WebHandler getHandler(String servletURI) {
        List<String> valueList = pattern.match(servletURI);
        if (valueList != null) {
            Map<String, String> paramsMap = new HashMap<String, String>();
            for (int i = 0; i < paramsName.size(); i++) {
                paramsMap.put(paramsName.get(i), valueList.get(i));
            }
            return new ControllerHandler(getController(), paramsMap);
        }
        return null;
    }

    public ControllerMetaInfo getController() {
        return controller;
    }

    public List<String> getParamsName() {
        return paramsName;
    }

}
