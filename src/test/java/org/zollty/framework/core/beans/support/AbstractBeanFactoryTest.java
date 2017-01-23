package org.zollty.framework.core.beans.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jretty.util.NestedRuntimeException;
import org.junit.Test;
import org.zollty.framework.core.annotation.Inject;
import org.zollty.framework.core.beans.Foo;
import org.zollty.framework.core.beans.FooBuilder;
import org.zollty.framework.core.beans.xml.XmlBeanReader;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.ResourceContext;

public class AbstractBeanFactoryTest {

    private static final String XML_PATH = "classpath:org/zollty/framework/core/beans/support/bean-test2.xml";
    
    private SimpleBeanFactory sbf;

    @org.junit.Before
    public void initSimpleBeanFactory() {
        ResourceContext resourcContext = new ResourceContext(Thread.currentThread().getContextClassLoader(), XML_PATH);
        SimpleBeanFactory sbf = new SimpleBeanFactory(new XmlBeanReader(resourcContext));
        this.sbf = sbf;
    }
    
    @Test
    public void testNullValueInject() {
        FooBuilder b = (FooBuilder) sbf.getBean(FooBuilder.class);
        
        assertNotNull(b);
        assertNull(b.getName());
    }
    
    @Test
    public void testConstructorInject() {
        Foo b = (Foo) sbf.getBean("foo");
        
        assertNotNull(b);
        assertEquals("jdbc/zollty", b.getName());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testFieldInject() {
        Class cl = FooBuilder.class;
        FooBuilder object = new FooBuilder();

        List<Field> fields = getInjectField(cl);

        // 属性注入
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> clazz = field.getType();
            String id = field.getAnnotation(Inject.class).value();

            String key = null;
            // id为空时按类型注入，调用checkConflict检查是否冲突
            if (MvcUtils.StringUtil.isBlank(id)) {
                key = clazz.getName();
            } else {
                key = id;
            }
            Foo instance = (Foo) sbf.getBean(key);
            assertNotNull(instance);
            if (instance != null) {
                try {
                    field.set(object, instance);
                } catch (Throwable t) {
                    throw new NestedRuntimeException(t, "field inject error [{}]", object.getClass().getName());
                }
            }

            assertEquals(object.bean, instance);
        }

    }

    static List<Field> getInjectField(Class<?> c) {
        Field[] fields = c.getDeclaredFields();
        List<Field> list = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.getAnnotation(Inject.class) != null) {
                list.add(field);
            }
        }
        return list;
    }
    
    
    @SuppressWarnings("rawtypes")
    @Test
    // test muti parameters
    public void methodInvoke1() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        Class clazz = FooBuilder.class;
        String methodName = "build";
        String[] vals = new String[]{"18", "zollty"};
        Class[] paramTypes = new Class[vals.length];
        Object[] vaos = new Object[vals.length];
        for(int i=0; i<vals.length; i++) {
            vaos[i] = MvcUtils.ConvertUtil.convert(vals[i], (String) null);
            paramTypes[i] = vaos[i].getClass();
        }
        
        Object object = new FooBuilder();

        Method method = ClassTools.findMethod(clazz,  methodName, paramTypes);
        method.setAccessible(true);
        Foo result = (Foo) method.invoke(object, vaos);
        assertNotNull(result);
        assertEquals("zollty", result.getName());
        assertEquals(18, result.getNum());
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    // test null parameter
    public void methodInvoke2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        Class clazz = FooBuilder.class;
        String methodName = "setName";
        String[] vals = new String[]{null};
        Class[] paramTypes = new Class[vals.length];
        Object[] vaos = new Object[vals.length];
        for (int i = 0; i < vals.length; i++) {
            vaos[i] = MvcUtils.ConvertUtil.convert(vals[i], (String) null);
            if (vaos[i] != null) {
                paramTypes[i] = vaos[i].getClass();
            } else {
                paramTypes[i] = null;
            }
        }
        
        FooBuilder object = new FooBuilder();

        Method method = ClassTools.findMethod(clazz,  methodName, paramTypes);
        method.setAccessible(true);
        Foo result = (Foo) method.invoke(object, vaos);
        assertNull(result);
        assertNull(object.getName());
    }
    
    

}