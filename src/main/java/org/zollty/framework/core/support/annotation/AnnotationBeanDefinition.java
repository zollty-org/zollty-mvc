package org.zollty.framework.core.support.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import org.zollty.framework.core.support.BeanDefinition;

public interface AnnotationBeanDefinition extends BeanDefinition {

	List<Field> getInjectFields();

	void setInjectFields(List<Field> fields);

	List<Method> getInjectMethods();

	void setInjectMethods(List<Method> methods);
}
