/**
 * @(#)AbStractAnnotationBeanReader.java 
 * Build-JDK: SUN-1.6.0_10-rc2 
 * Create by Zollty_Tsow on 2013-9-21 
 * Contact: http://blog.csdn.net/zollty 
 */
package org.zollty.framework.core.support.annotation;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;

import org.zollty.framework.core.annotation.Inject;
import org.zollty.framework.core.config.ApplicationConfig;
import org.zollty.framework.core.config.ConfigReader;
import org.zollty.framework.core.support.AbstractBeanReader;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty 
 * @since 2013-9-21
 */
abstract public class AbstractAnnotationBeanReader extends AbstractBeanReader {
	
	private Logger log = LogFactory.getLogger(AbstractAnnotationBeanReader.class);
	
	/** ClassLoader to resolve bean class names with, if necessary */
	private ClassLoader beanClassLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
	
	protected void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : MvcUtils.ClassUtil.getDefaultClassLoader());
	}
	
	public ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}
	
	protected void init(){
		beanDefinitions = new ArrayList<BeanDefinition>();
		ApplicationConfig config = ConfigReader.getInstance().getConfig();//ConfigReader.getInstance().load(file);
		for (String pack : config.getPaths()) {
			log.info("componentPath = " + pack);
			scan(pack.trim());
		}
	}

    private void scan(String packageName) {
        String packageDirName = packageName.replace('.', '/');
        URL[] urls = findAllClassPathResources(packageDirName);
        if (urls == null) {
            error(packageName + " can not be found");
        }
        for (URL url : urls) {
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                parseFile(url, packageDirName);
            }
            else if ("jar".equals(protocol)) {
                parseJar(url, packageDirName);
            }
        }
    }
    
//    private void scan(String packageName) {
//        String packageDirName = packageName.replace('.', '/');
//        URL url = getBeanClassLoader().getResource(packageDirName);
//        if (url == null)
//            error(packageName + " can not be found");
//        String protocol = url.getProtocol();
//        if ("file".equals(protocol)) {
//            parseFile(url, packageDirName);
//        } else if ("jar".equals(protocol)) {
//            parseJar(url, packageDirName);
//        }
//    }

    protected URL[] findAllClassPathResources(String location) {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Enumeration<URL> resourceUrls = null;
        try {
            resourceUrls = getBeanClassLoader().getResources(path);
        }
        catch (IOException e) {
            log.error(e, "find resources error");
            return null;
        }
        Set<URL> result = new LinkedHashSet<URL>(16);
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(url);
        }
        return result.toArray(new URL[result.size()]);
    }

	private void parseFile(URL url, final String packageDirName) {
		File path = null;
		try {
			path = new File(url.toURI());
		} catch (Throwable t) {
			log.error(t, "parse file error");
			return;
		}
        path.listFiles(new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                if (name.endsWith(".class") && !name.contains("$")) {
                    parseClass(packageDirName.replace('/', '.') + "." + name.substring(0, file.getName().length() - 6));
                } else if (file.isDirectory()) {
                    try {
                        parseFile(file.toURI().toURL(), packageDirName + "/" + name);
                    } catch (Throwable t) {
                        log.error(t, "parse file error");
                    }
                }
                return false;
            }
        });
    }

	private void parseJar(URL url, String packageDirName) {
		Enumeration<JarEntry> entries = null;
        try {
            entries = ((JarURLConnection) url.openConnection()).getJarFile().entries();
        } catch (Throwable t) {
            log.error(t, "parse jar error");
            return;
        }
		while (entries.hasMoreElements()) {
			String name = entries.nextElement().getName();
			if (!name.endsWith(".class") || name.contains("$")
					|| !name.startsWith(packageDirName + "/"))
				continue;
			parseClass(name.substring(0, name.length() - 6).replace('/', '.'));
		}

	}

	private void parseClass(String className) {
		
		Class<?> c = null;
		try {
			c = getBeanClassLoader().loadClass(className);
		} catch (Throwable t) {
			log.error(t, "parse class error");
			return;
		}
		
		BeanDefinition beanDefinition = null;
		try{
			beanDefinition = getBeanDefinition(c);
		}catch (Throwable e) {
			log.error(e, "get bean definition error: class="+c.getName());
		}
		if (beanDefinition != null){
			beanDefinitions.add(beanDefinition);
		}
		
	}
	
	// let subclass override it
	abstract protected BeanDefinition getBeanDefinition(Class<?> c);
	
	protected List<Field> getInjectField(Class<?> c) {
		Field[] fields = c.getDeclaredFields();
		List<Field> list = new ArrayList<Field>();
		for (Field field : fields) {
			if (field.getAnnotation(Inject.class) != null) {
				list.add(field);
			}
		}
		return list;
	}

	protected List<Method> getInjectMethod(Class<?> c) {
		Method[] methods = c.getMethods(); //[包括父类的方法]，getDeclaredMethods[不包括父类的方法]
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.getAnnotation(Inject.class) != null) {
				list.add(m);
			}
		}
		return list;
	}
}
