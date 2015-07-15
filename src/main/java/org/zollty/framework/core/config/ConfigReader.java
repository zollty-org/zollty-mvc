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
package org.zollty.framework.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zollty.framework.core.Const;
import org.zollty.framework.core.config.impl.DefaultTextApplicationConfigImpl;
import org.zollty.framework.core.config.impl.DefaultXmlApplicationConfigImpl;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.DefaultDom;
import org.zollty.framework.util.dom.Dom;
import org.zollty.util.NestedRuntimeException;

/**
 * @author zollty
 * @since 2013-8-21
 */
public class ConfigReader {

    /** ClassLoader to resolve bean class names with, if necessary */
    private ClassLoader classLoader = MvcUtils.ClassUtil.getDefaultClassLoader();

    private String configLocation = Const.DEFAULT_CONFIG_LOCATION;

    /**
     * like <br>
     * D:/C/Java/apache-tomcat-6.0.36/webapps/tre_client_demo/ <br>
     * /opt/app/WebSphere/profiles/AppSrv01/installedApps/vm-vmw231-tCell01/clientDemo.ear/clientDemo.war/
     */
    private String contextRealPath;

    private ApplicationConfig config;

    private ConfigReader() {
    }

    private static class Holder {
        private static ConfigReader instance = new ConfigReader();
    }

    public static ConfigReader getInstance() {
        return Holder.instance;
    }

    public ApplicationConfig load(String configLocation, String contextRealPath) {
        this.setContextRealPath(contextRealPath);
        return load(configLocation);
    }

    public ApplicationConfig load(String configLocation, ClassLoader classLoader) {
        this.setClassLoader(classLoader);
        return load(configLocation);
    }

    public ApplicationConfig load(String configLocation, String contextRealPath, ClassLoader classLoader) {
        this.setClassLoader(classLoader);
        this.setContextRealPath(contextRealPath);
        return load(configLocation);
    }

    private void loadXmlConfig() {
        Dom dom = new DefaultDom();
        // 获得Xml文档对象
        Document doc = dom.getDocument(getConfigLocation());
        // 得到根节点
        Element root = dom.getRoot(doc);
        Element cfClass = dom.element(root, "config-class");
        String configClass = null;
        if (cfClass != null) {
            configClass = cfClass.getAttribute("value");
        }
        if (MvcUtils.StringUtil.isNotEmpty(configClass)) {
            this.config = new ApplicationConfig((IApplicationConfig) MvcUtils.ClassUtil.newInstance(configClass, getClassLoader()));
        }
        else {
            this.config = new ApplicationConfig(new DefaultXmlApplicationConfigImpl(root, dom));
        }
    }
    
    private InputStream getResourceInputStream() {
        try {
            return MvcUtils.ResourceUtil.getResourceInputStream(getConfigLocation(), getClassLoader(), null);
        }
        catch (IOException e) {
            throw new NestedRuntimeException(e);
        }
    }

    private void loadTextConfig() {
        InputStream in = getResourceInputStream();
        Properties props = MvcUtils.ResourceUtil.getProperties(in);
        Map<String, String> propsMap = MvcUtils.ResourceUtil.covertProperties2Map(props);

        String configClass = propsMap.get("config-class");
        if (MvcUtils.StringUtil.isNotEmpty(configClass)) {
            this.config = new ApplicationConfig((IApplicationConfig) MvcUtils.ClassUtil.newInstance(configClass, getClassLoader()));
        }
        else {
            this.config = new ApplicationConfig(new DefaultTextApplicationConfigImpl(propsMap));
        }
    }

    public ApplicationConfig load(String configLocation) {
        this.setConfigLocation(configLocation);

        if (configLocation != null && configLocation.endsWith("xml")) {
            loadXmlConfig();
        }
        else if (configLocation != null && configLocation.endsWith("properties")) {
            loadTextConfig();
        }

        return config;
    }

    /**
     * @param contextRealPath
     */
    public void setContextRealPath(String contextRealPath) {
        if (null == contextRealPath) {
            return;
        }
        String path = contextRealPath.replace('\\', '/');
        if (path.endsWith("/")) {
            this.contextRealPath = path;
        }
        else {
            this.contextRealPath = path + "/";
        }
    }

    /**
     * @param configLocation
     *            the configLocation to set
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = (classLoader != null ? classLoader : MvcUtils.ClassUtil.getDefaultClassLoader());
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public ApplicationConfig getConfig() {
        return config;
    }

    public String getEncoding() {
        return config.getEncoding();
    }

    public String getContextRealPath() {
        return contextRealPath;
    }

    public String getConfigLocation() {
        return configLocation;
    }

}