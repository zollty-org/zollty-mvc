package org.zollty.framework.mvc.context.support;

import org.zollty.framework.mvc.context.ControllerBeanDefinition;

public class ControllerBeanBuilder {
    
    
    public static ControllerBeanDefinition getControllerBeanDef() {
        return new ControllerAnnotatedBeanDefinition();
    }

}
