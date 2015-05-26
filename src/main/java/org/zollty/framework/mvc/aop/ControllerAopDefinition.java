package org.zollty.framework.mvc.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;

public interface ControllerAopDefinition {
    
    Map<Method, List<MvcBeforeBeanDefinition>> getReqMethodsAOP();
    
    /** list必须按顺序先后排列 */
    void setReqMethodsAOP(Map<Method, List<MvcBeforeBeanDefinition>> reqMethodsAOP);

}
