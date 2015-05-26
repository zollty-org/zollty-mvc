package org.zollty.framework.mvc.aop.bean;

import java.lang.reflect.Method;

import org.zollty.framework.core.support.annotation.AnnotationBeanDefinition;

public interface AopBeanDefinition extends AnnotationBeanDefinition {
    
    Method getDisposeMethod();
    
    void setDisposeMethod(Method method);
    
    int getOrder();
    
    void setOrder(int order);

}