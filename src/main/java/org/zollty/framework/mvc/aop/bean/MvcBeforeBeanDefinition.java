package org.zollty.framework.mvc.aop.bean;

import java.lang.reflect.Method;

import org.zollty.framework.core.support.annotation.AnnotatedBeanDefinition;

public class MvcBeforeBeanDefinition extends AnnotatedBeanDefinition implements AopBeanDefinition {

    private Method disposeMethod;
    
    private int order;
    
    @Override
    public Method getDisposeMethod() {
        return disposeMethod;
    }

    @Override
    public void setDisposeMethod(Method disposeMethod) {
        this.disposeMethod = disposeMethod;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }
    
    @Override
    public String toString() {
        return "MvcBeforeBeanDefinition [id=" + getId() + ", className=" + getClassName() + "]";
    }

}
