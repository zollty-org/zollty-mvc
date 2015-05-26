package org.zollty.framework.core.config.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.zollty.framework.core.config.IServletContextFileConfig;
import org.zollty.framework.util.MvcUtils;

public class DefaultTextFileConfig extends AbstractTextConfig implements IServletContextFileConfig {

    private ServletContext servletContext;
    
    
    public DefaultTextFileConfig() {
        super();
    }

    public DefaultTextFileConfig(String configLocation) {
        super(configLocation);
    }

    public DefaultTextFileConfig(String configLocation, ClassLoader classLoader) {
        super(configLocation, classLoader);
    }
    
    public DefaultTextFileConfig(String configLocation, ServletContext servletContext) {
        super(configLocation);
        this.servletContext = servletContext;
    }
    
    public DefaultTextFileConfig(String configLocation, ClassLoader classLoader, ServletContext servletContext) {
        super(configLocation, classLoader);
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
