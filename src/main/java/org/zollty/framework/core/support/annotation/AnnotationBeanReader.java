package org.zollty.framework.core.support.annotation;

import org.zollty.framework.core.annotation.Component;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.resource.support.ResourcePatternResolver;

/**
 * 读取Bean信息
 * 
 * @author zollty
 * @since 2013-9-20
 */
public class AnnotationBeanReader extends AbstractAnnotationBeanReader {

    private Logger log = LogFactory.getLogger(AnnotationBeanReader.class);
    
    public AnnotationBeanReader(String[] scanningPackages, 
            ClassLoader beanClassLoader, ResourcePatternResolver resourcePatternResolver) {
        
        super(scanningPackages, beanClassLoader, resourcePatternResolver);
    }

    @Override
    protected BeanDefinition getBeanDefinition(Class<?> c) {
        if (c.isAnnotationPresent(Component.class)) {
            log.info("classes - " + c.getName());
            return componentParser(c);
        }
        return null;
    }

}