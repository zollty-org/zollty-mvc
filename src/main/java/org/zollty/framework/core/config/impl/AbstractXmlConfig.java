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
 * Create by ZollTy on 2014-5-21 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.config.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.NestedRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zollty.framework.core.Const;
import org.zollty.framework.core.config.ConfigTools;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.DefaultDom;
import org.zollty.framework.util.dom.Dom;

/**
 * 
 * @author zollty
 * @since 2014-5-21
 */
public abstract class AbstractXmlConfig extends AbstractFileConfig {

    private Logger logger = LogFactory.getLogger(AbstractXmlConfig.class);

    private Dom dom;

    public AbstractXmlConfig() {
        super(Const.DEFAULT_CONFIG_LOCATION_XML);
        this.dom = new DefaultDom();
    }

    public AbstractXmlConfig(String configLocation) {
        super(configLocation);
        this.dom = new DefaultDom();
    }

    public AbstractXmlConfig(String configLocation, ClassLoader classLoader) {
        super(configLocation, classLoader);
        this.dom = new DefaultDom();
    }

    public AbstractXmlConfig(String configLocation, Dom dom) {
        super(configLocation);
        this.dom = dom;
    }

    public AbstractXmlConfig(String configLocation, ClassLoader classLoader, Dom dom) {
        super(configLocation, classLoader);
        this.dom = dom;
    }

    public abstract InputStream getResourceInputStream() throws IOException;

    protected void loadConfig() {
        String configPath = getConfigLocation();
        if (configPath == null || !configPath.endsWith(".xml")) {
            throw new IllegalArgumentException("config location assume be a xml file but get: "
                    + configPath);
        }

        InputStream in = null;
        try {
            in = getResourceInputStream();
        }
        catch (IOException e) {
            MvcUtils.IOUtil.closeIO(in);
            throw new NestedRuntimeException(e);
        }

        // 获得Xml文档对象
        Document doc = dom.getDocument(in);
        // 得到根节点
        Element root = dom.getRoot(doc);
        
        parseComponentScan(root);

        parseNoIntercept(root);
        
        parseInterceptor(root);

        Element mvc = dom.element(root, "mvc");
        if (mvc != null) {
            String viewPath = mvc.getAttribute("view-path");
            String encoding = mvc.getAttribute("view-encoding");
            logger.info("mvc viewPath [" + viewPath + "] encoding [" + encoding + "]");

            if (MvcUtils.StringUtil.isNotBlank(viewPath)) {
                this.setViewPath(viewPath);
            }
            if (MvcUtils.StringUtil.isNotBlank(encoding)) {
                this.setEncoding(encoding);
            }
        }
        
        Element logger = dom.element(root, "logger");
        if (null != logger) {
            String logName = logger.getAttribute("class");
            String level = logger.getAttribute("level");
            this.setInitLogger(logName, level);
        }

        Element errorPage = dom.element(root, "error-page");
        if (null != errorPage) {
            String path = errorPage.getAttribute("path");
            if (null != path) {
                this.setErrorPagePath(path);
            }
        }
    }

    public void setDom(Dom dom) {
        this.dom = dom;
    }

    public Dom getDom() {
        return dom;
    }
    
    protected void parseComponentScan(Element root) {
        // 得到所有scan节点
        List<Element> scanList = dom.elements(root, "component-scan");

        if (scanList != null) {
            List<String> paths = new LinkedList<String>();
            for (int i = 0; i < scanList.size(); i++) {
                Element ele = scanList.get(i);
                String path = ele.getAttribute("base-package");
                if (MvcUtils.StringUtil.isNotEmpty(path)) {
                    paths.add(path);
                }
            }
            this.setScanningPackages(paths.toArray(new String[0]));
        } else {
            this.setScanningPackages(new String[0]);
        }
    }
    
    protected void parseNoIntercept(Element root) {
        List<Element> nointers = dom.elements(root, "no-intercept");
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        String str = null;
        for (Element nointc : nointers) {
            str = nointc.getAttribute("prefix");
            if (MvcUtils.StringUtil.isNotEmpty(str)) {
                prefix.append(str).append(',');
            }
            str = nointc.getAttribute("suffix");
            if (MvcUtils.StringUtil.isNotEmpty(str)) {
                suffix.append(str).append(',');
            }
        }
        if (MvcUtils.StringUtil.isNotEmpty(prefix)) {
            this.setExcludePrefixes(ConfigTools.parseExcludePrefix(prefix.toString()));
        }
        if (MvcUtils.StringUtil.isNotEmpty(suffix)) {
            this.setExcludeSuffixes(ConfigTools.parseExcludeSuffix(suffix.toString()));
        }
    }
    
    protected void parseInterceptor(Element root) {
        List<Element> incep = dom.elements(root, "before-refresh");
        Set<String> beforeRefresh = new HashSet<String>();
        String str;
        for (Element nointc : incep) {
            str = nointc.getAttribute("class");
            beforeRefresh.add(str);
        }
        
        incep = dom.elements(root, "after-close");
        Set<String> afterClose = new HashSet<String>();
        for (Element nointc : incep) {
            str = nointc.getAttribute("class");
            afterClose.add(str);
        }
        
        if (beforeRefresh.size() > 0) {
            this.setBeforeRefreshInterceptors(beforeRefresh);
        }
        if (afterClose.size() > 0) {
            this.setAfterCloseInterceptors(afterClose);
        }
    }

}