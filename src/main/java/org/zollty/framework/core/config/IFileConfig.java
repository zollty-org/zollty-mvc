package org.zollty.framework.core.config;

public interface IFileConfig extends IApplicationConfig {
    
    ClassLoader getClassLoader();
    
    String getConfigLocation();

}