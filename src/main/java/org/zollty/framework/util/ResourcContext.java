package org.zollty.framework.util;

import javax.servlet.ServletContext;

public class ResourcContext {
    
    private String location;
    
    private ClassLoader classLoader;
    
    private ServletContext servletContext;
    
    public ResourcContext(String location) {
        super();
        this.location = location;
        this.classLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
    }

    public ResourcContext(String location, ClassLoader classLoader) {
        super();
        this.location = location;
        this.classLoader = (classLoader != null ? classLoader : MvcUtils.ClassUtil.getDefaultClassLoader());
    }

    public ResourcContext(String location, ServletContext servletContext) {
        super();
        this.location = location;
        this.servletContext = servletContext;
        this.classLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
    }

    public ResourcContext(String location, ClassLoader classLoader, ServletContext servletContext) {
        super();
        this.location = location;
        this.classLoader = (classLoader != null ? classLoader : MvcUtils.ClassUtil.getDefaultClassLoader());
        this.servletContext = servletContext;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
}
