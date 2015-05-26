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

import java.util.Set;

import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.mvc.handler.support.ErrorHandler;
import org.zollty.framework.mvc.view.HtmlView;
import org.zollty.framework.mvc.view.JsonView;
import org.zollty.framework.mvc.view.JspView;
import org.zollty.framework.mvc.view.TextView;

/**
 * @author zollty
 * @since 2013-9-15
 */
abstract public class AbstractHandlerMapping implements HandlerMapping {
    
    private Set<String> excludePrefix;
    
    private Set<String> excludeSuffix;
    
    private String encoding;

    public AbstractHandlerMapping(IApplicationConfig config) {
        String encoding = config.getEncoding();
        TextView.setEncoding(encoding);
        HtmlView.setEncoding(encoding);
        JsonView.setEncoding(encoding);
        this.encoding = encoding;
        
        JspView.setViewPath(config.getViewPath());
        ErrorHandler.setErrorPage(config.getErrorPagePath());
        
        this.excludePrefix = config.getExcludePrefixes();
        this.excludeSuffix = config.getExcludeSuffixes();
    }

    /**
     * @return the excludeprefix
     */
    @Override
    public Set<String> getExcludePrefixes() {
        return excludePrefix;
    }

    /**
     * @return the excludeSuffix
     */
    @Override
    public Set<String> getExcludeSuffixes() {
        return excludeSuffix;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

}
