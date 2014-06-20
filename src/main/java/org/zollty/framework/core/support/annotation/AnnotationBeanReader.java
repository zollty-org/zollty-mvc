package org.zollty.framework.core.support.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.zollty.framework.core.annotation.Component;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.util.MvcReflectUtils;

/**
 * 读取Bean信息
 * @author zollty 
 * @since 2013-9-20
 */
public class AnnotationBeanReader extends AbstractAnnotationBeanReader {
	private Logger log = LogFactory.getLogger(AnnotationBeanReader.class);

	public AnnotationBeanReader(ClassLoader beanClassLoader) {
//		beanDefinitions = new ArrayList<BeanDefinition>();
//		AnnotationConfig config = ConfigReader.getInstance().getConfig();//ConfigReader.getInstance().load(file);
//		for (String pack : config.getPaths()) {
//			log.info("componentPath = " + pack);
//			scan(pack.trim());
//		}
		super.setBeanClassLoader(beanClassLoader);
		super.init();
	}

	@Override
	protected BeanDefinition getBeanDefinition(Class<?> c) {
		
		if (c.isAnnotationPresent(Component.class)) {
			log.info("classes - " + c.getName());
			return componentParser(c);
		} else
			return null;
	}

	protected BeanDefinition componentParser(Class<?> c) {
		
		AnnotationBeanDefinition annotationBeanDefinition = new AnnotatedBeanDefinition();
		annotationBeanDefinition.setClassName(c.getName());

		Component component = c.getAnnotation(Component.class);
		String id = component.value();
		annotationBeanDefinition.setId(id);

		String[] names = MvcReflectUtils.getInterfaceNames(c);
		annotationBeanDefinition.setInterfaceNames(names);

		List<Field> fields = getInjectField(c);
		annotationBeanDefinition.setInjectFields(fields);

		List<Method> methods = getInjectMethod(c);
		annotationBeanDefinition.setInjectMethods(methods);

		try {
			Object object = c.newInstance();
			annotationBeanDefinition.setObject(object);
		} catch (Throwable t) {
			log.error(t, "component parser error");
		}
		return annotationBeanDefinition;
		
	}
	
}
