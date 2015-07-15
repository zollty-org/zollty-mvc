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
package org.zollty.framework.core.config.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.zollty.framework.core.config.ConfigTools;
import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.core.config.InitByConfig;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.Dom;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2014-5-22
 */
public class DefaultXmlApplicationConfigImpl implements IApplicationConfig {
    
    // SYS
    private Logger log = LogFactory.getLogger(DefaultXmlApplicationConfigImpl.class);
    
    /**
     * 
     */
    public DefaultXmlApplicationConfigImpl(Element root, Dom dom) {
        super();
       
        // 得到所有scan节点
        List<Element> scanList = dom.elements(root, "component-scan");

        if (scanList != null) {
            List<String> paths = new LinkedList<String>();
            for (int i = 0; i < scanList.size(); i++) {
                Element ele = scanList.get(i);
                String path = ele.getAttribute("base-package");
                if( MvcUtils.StringUtil.isNotEmpty(path) )
                    paths.add(path);
            }
            this.setPaths(paths.toArray(new String[0]));
        } else {
            this.setPaths(new String[0]);
        }

        Element mvc = dom.element(root, "mvc");
        if (mvc != null) {
            String viewPath = mvc.getAttribute("view-path");
            String encoding = mvc.getAttribute("view-encoding");
            log.info("mvc viewPath ["+viewPath+"] encoding [" + encoding + "]");
            
            if( MvcUtils.StringUtil.isNotBlank(viewPath))
                this.setViewPath(viewPath);
            if( MvcUtils.StringUtil.isNotBlank(encoding))
                this.setEncoding(encoding);
        }
        
        List<Element> nointers = dom.elements(root, "no-intercept");
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        String str = null;
        for (Element nointc : nointers) {
            str = nointc.getAttribute("prefix");
            if(MvcUtils.StringUtil.isNotEmpty(str))
                prefix.append(str).append(',');
            str = nointc.getAttribute("suffix");
            if(MvcUtils.StringUtil.isNotEmpty(str))
                suffix.append(str).append(',');
        }
        if( MvcUtils.StringUtil.isNotEmpty(prefix) ) {
            this.setExcludePrefixes(ConfigTools.parseExcludePrefix(prefix.toString()));
        }
        if( MvcUtils.StringUtil.isNotEmpty(suffix) ) {
            this.setExcludeSuffixes(ConfigTools.parseExcludeSuffix(suffix.toString()));
        }
        
        Element logger = dom.element(root, "logger");
        if( null != logger ){
            String logName = logger.getAttribute("class");
            String level = logger.getAttribute("level");
            if( null != logName ){
                this.setLogLevel(level);
                InitByConfig.initLogFactory(logName, level);
            }
        }
        
        Element errorPage = dom.element(root, "errorPage");
        if( null != errorPage ){
            String path = errorPage.getAttribute("path");
            if( null != path ){
                this.setErrorPagePath(path);
            }
        }
    }

    private String viewPath;
    private String encoding;
    private String errorPagePath;
    private String logLevel;
    
    /** 排除拦截 的前缀 */
    private Set<String> excludePrefixes = new HashSet<String>();
    
    /** 排除拦截 的后缀 */
    private Set<String> excludeSuffixes = new HashSet<String>();
    
    private String[] paths;

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public String getErrorPagePath() {
        return errorPagePath;
    }

    public void setErrorPagePath(String errorPagePath) {
        this.errorPagePath = errorPagePath;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public Set<String> getExcludeSuffixes() {
        return excludeSuffixes;
    }

    @Override
    public Set<String> getExcludePrefixes() {
        return excludePrefixes;
    }

    public void setExcludePrefixes(Set<String> excludePrefixes) {
        this.excludePrefixes = excludePrefixes;
    }

    public void setExcludeSuffixes(Set<String> excludeSuffixes) {
        this.excludeSuffixes = excludeSuffixes;
    }

}
