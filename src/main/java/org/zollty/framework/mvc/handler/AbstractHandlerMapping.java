/* 
 * Copyright (C) 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by ZollTy on 2013-9-15 (http://blog.zollty.com, zollty@163.com)
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