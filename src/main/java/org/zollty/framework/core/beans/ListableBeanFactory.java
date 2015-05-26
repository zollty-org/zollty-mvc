package org.zollty.framework.core.beans;

import java.util.List;
import java.util.Map;

import org.zollty.framework.core.support.BeanDefinition;

public interface ListableBeanFactory extends BeanFactory {

    List<BeanDefinition> getBeanDefinitions();

    Map<String, Object> getBeanMap();

    <T> Map<String, T> getBeansOfType(Class<T> type);

}
