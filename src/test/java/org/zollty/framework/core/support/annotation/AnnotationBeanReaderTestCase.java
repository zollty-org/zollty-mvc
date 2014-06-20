/*
 * @(#)AnnotationBeanReaderTestCase.java
 * Create by Zollty_Tsou on 2014-1-4 
 * you may find ZollTy at csdn, github, oschina, stackoverflow...
 * e.g. https://github.com/zollty  http://blog.csdn.net/zollty 
 */
package org.zollty.framework.core.support.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.zollty.framework.core.annotation.Inject;

/**
 * @author zollty 
 * @since 2014-1-4
 */
public class AnnotationBeanReaderTestCase {
	
	public static List<Method> getInjectMethod(Class<?> c) {
		Method[] methods = c.getMethods();
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.getAnnotation(Inject.class) != null) {
				list.add(m);
			}
		}
		return list;
	}
	
	public static List<Field> getInjectField(Class<?> c) {
		Field[] fields = c.getDeclaredFields();
		List<Field> list = new ArrayList<Field>();
		for (Field field : fields) {
			if (field.getAnnotation(Inject.class) != null) {
				list.add(field);
			}
		}
		return list;
	}
	
	public static void main(String[] args) throws Exception {
		List<Method> list = getInjectMethod(SubClass.class);
		for(Method m: list){
			System.out.println(m.getName());
			System.out.println(m.getDeclaringClass());
		}
		
		List<Field> list1 = getInjectField(SubClass.class);
		for(Field m: list1){
			System.out.println(m.getName());
		}
	}

}
