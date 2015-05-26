package org.zollty.framework.core.config;

import javax.servlet.ServletContext;

public interface IServletContextFileConfig extends IFileConfig {
    
    ServletContext getServletContext();

}
