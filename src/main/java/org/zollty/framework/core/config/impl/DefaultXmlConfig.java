package org.zollty.framework.core.config.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.zollty.framework.core.config.IServletContextFileConfig;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.Dom;

public class DefaultXmlConfig extends AbstractXmlConfig implements IServletContextFileConfig {
    
    private ServletContext servletContext;
    
    public DefaultXmlConfig() {
        super();
    }
    
    public DefaultXmlConfig(String configLocation, ClassLoader classLoader) {
        super(configLocation, classLoader);
    }

    public DefaultXmlConfig(String configLocation, ClassLoader classLoader, Dom dom) {
        super(configLocation, classLoader, dom);
    }

    public DefaultXmlConfig(String configLocation, Dom dom) {
        super(configLocation, dom);
    }

    public DefaultXmlConfig(String configLocation) {
        super(configLocation);
    }

    public DefaultXmlConfig(String configLocation, ServletContext servletContext) {
        super(configLocation);
        this.servletContext = servletContext;
    }
    
    public DefaultXmlConfig(String configLocation, ClassLoader classLoader, Dom dom, ServletContext servletContext) {
        super(configLocation, classLoader, dom);
        this.servletContext = servletContext;
    }

    @Override
    public InputStream getResourceInputStream() throws IOException {
        return MvcUtils.ResourceUtil.getResourceInputStream(getConfigLocation(), getClassLoader(), getServletContext());
    }
    
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

}
