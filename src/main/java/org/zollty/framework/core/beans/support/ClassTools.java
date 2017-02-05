package org.zollty.framework.core.beans.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jretty.util.NestedRuntimeException;
import org.zollty.framework.util.MvcUtils;

/**
 * ClassTools under the package use
 * 
 * @author zollty
 * @since 2017-1-22
 */
class ClassTools {

    private ClassTools() {
    }
    
    /**
     * simple to load user class
     */
    static Class<?> loadClass(String className, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new NestedRuntimeException(ex, "Cannot find class [" + className + "]");
        } catch (LinkageError ex) {
            throw new NestedRuntimeException(ex,
                    "Error loading class [" + className + "]: problem with class file or dependent class.");
        }
    }

    /**
     * find Method use a special way.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static Method findMethod(Class clazz, String methodName, Class[] paramTypes) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        int paramNum = paramTypes.length;
        for (Method method : declaredMethods) {
            Class[] mpts = method.getParameterTypes();
            if (methodName.equals(method.getName()) && paramNum == mpts.length) {
                boolean flag = true;
                for (int i = 0; i < mpts.length; i++) {
                    if (paramTypes[i] == null) {
                        continue;
                    }
                    // 将primitive类型转换成包装类型再比较
                    Class mp = MvcUtils.ClassUtil.resolvePrimitiveIfNecessary(mpts[i]);
                    if (!mp.isAssignableFrom(paramTypes[i])) {// && !(mp.isArray() && paramTypes[i].isArray())
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * find Constructor use a special way.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static Constructor findConstructor(Class clazz, Class... paramTypes) {
        Constructor[] constructors = clazz.getConstructors();
        int paramNum = paramTypes.length;
        for (Constructor constru : constructors) {
            Class[] mpts = constru.getParameterTypes();
            if (paramNum == mpts.length) {
                boolean flag = true;
                for (int i = 0; i < mpts.length; i++) {
                    if (paramTypes[i] == null) {
                        continue;
                    }
                    // 将primitive类型转换成包装类型再比较
                    Class mp = MvcUtils.ClassUtil.resolvePrimitiveIfNecessary(mpts[i]);
                    if (!mp.isAssignableFrom(paramTypes[i])) {// && !(mp.isArray() && paramTypes[i].isArray())
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    return constru;
                }
            }
        }
        return null;
    }

}