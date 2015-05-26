package org.zollty.framework.core.config.impl;

import org.zollty.framework.core.config.IFileConfig;
import org.zollty.framework.util.MvcUtils;

public abstract class AbstractFileConfig extends AbstractApplicationConfig implements IFileConfig {
    
    /** ClassLoader to resolve bean class names with, if necessary */
    private ClassLoader classLoader;
    
    private String configLocation;
    
    
    public AbstractFileConfig(String configLocation) {
        this.configLocation = configLocation;
        this.classLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
    }
    
    
    public AbstractFileConfig(String configLocation, ClassLoader classLoader) {
        this.configLocation = configLocation;
        this.classLoader = classLoader;
    }
    

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

}
