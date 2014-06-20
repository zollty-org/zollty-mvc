/* 
 * Copyright (C) 2012-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by Zollty Tsou [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.core.beans.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zollty.framework.core.BeanFactoryHelper;
import org.zollty.framework.core.annotation.Inject;
import org.zollty.framework.core.beans.ConfigurableBeanFactory;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.annotation.AnnotationBeanDefinition;
import org.zollty.framework.core.support.exception.BeanDefinitionParsingException;
import org.zollty.framework.core.support.xml.ManagedArray;
import org.zollty.framework.core.support.xml.ManagedList;
import org.zollty.framework.core.support.xml.ManagedMap;
import org.zollty.framework.core.support.xml.ManagedRef;
import org.zollty.framework.core.support.xml.ManagedValue;
import org.zollty.framework.core.support.xml.XmlBeanDefinition;
import org.zollty.framework.util.MvcConvertUtils;
import org.zollty.framework.util.MvcReflectUtils;
import org.zollty.framework.util.MvcReflectUtils.BeanMethodFilter;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty 
 * @since 2013-10-11
 */
abstract public class AbstractBeanFactory implements ConfigurableBeanFactory {
    
    /** ClassLoader to resolve bean class names with, if necessary */
    private ClassLoader beanClassLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
    
    protected List<BeanDefinition> beanDefinitions;
    
    protected Map<String, Object> map = new HashMap<String, Object>();
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz) {
        return (T) map.get(clazz.getName());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String id) {
        return (T) map.get(id);
    }
    
    @Override
    public List<BeanDefinition> getBeanDefinitions(){
        return this.beanDefinitions;
    }
    
    @Override
    public String[] getBeanDefinitionNames(){
        // 暂未实现
        return null;
    }
    
    @Override
    public void refresh(){
        doBeforeRefresh();
        
        beanDefinitions = loadBeanDefinitions();
        check(); //冲突检测
        addObjectToContext();
        
        doAfterRefresh();
        BeanFactoryHelper.setBeanFactory(this);
    }
    
    @Override
    public void close(){
        // 暂时未用到
    }
    
    // 交给子类去实现
    abstract protected List<BeanDefinition> loadBeanDefinitions();
    abstract protected void doBeforeRefresh();
    abstract protected void doAfterRefresh();
    
    @Override
    public final ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }
    
    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : MvcUtils.ClassUtil.getDefaultClassLoader());
    }
    
    protected void addObjectToContext() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            inject(beanDefinition);
        }
    }
    
    private Object inject(BeanDefinition beanDef) {
        if( !beanDef.isFinished() ){
            
            if (beanDef instanceof XmlBeanDefinition)
                return xmlInject(beanDef);
            else if (beanDef instanceof AnnotationBeanDefinition)
                return annotationInject(beanDef);
            else
                return null;
            
        }
        return beanDef.getObject();
    }
    
    
    protected void addObjectToContext(BeanDefinition beanDefinition) {
        // 增加声明的组件到 ApplicationContext
        Object object = beanDefinition.getObject();
        // 把id作为key
        String id = beanDefinition.getId();
        if ( MvcUtils.StringUtil.isNotBlank(id))
            map.put(id, object);

        // 把类名作为key
        map.put(beanDefinition.getClassName(), object);

        // 把接口名作为key
        String[] keys = beanDefinition.getInterfaceNames();
        for (String k : keys) {
            map.put(k, object);
        }
    }
    
    protected void check() {
        // 1.id相同的抛异常
        // 2.className或者interfaceName相同，但其中一个没有定义id，抛异常
        // 3.className或者interfaceName相同，且都定义的id，需要保存备忘，按类型或者接口自动注入的时候抛异常
        for (int i = 0; i < beanDefinitions.size(); i++) {
            for (int j = i + 1; j < beanDefinitions.size(); j++) {
                BeanDefinition b1 = beanDefinitions.get(i);
                BeanDefinition b2 = beanDefinitions.get(j);
                if ( MvcUtils.StringUtil.isNotBlank(b1.getId())
                        && MvcUtils.StringUtil.isNotBlank(b2.getId())
                        && b1.getId().equals(b2.getId())) {
                    error("bean " + b1.getClassName() + " and bean " + b2.getClassName() + " have duplicate id ");
                }
                if (b1.getClassName().equals(b2.getClassName())) {
                    if ( MvcUtils.StringUtil.isNullOrEmpty(b1.getId())
                            || MvcUtils.StringUtil.isNullOrEmpty(b2.getId())) {
                        error("class " + b1.getClassName() + " duplicate definition");
                    } else {
                        errorMemo.add(b1.getClassName());
                    }
                }
                for (String iname1 : b1.getInterfaceNames()) {
                    for (String iname2 : b2.getInterfaceNames()) {
                        if ( iname1.equals(iname2)) {
                            if ( MvcUtils.StringUtil.isNullOrEmpty(b1.getId())
                                    || MvcUtils.StringUtil.isNullOrEmpty(b2.getId())) {
                                error("class " + b1.getClassName() + " duplicate definition");
                            } else {
                                errorMemo.add(iname1);
                            }
                        }
                    }
                }
            }
        }
    }
    

    private Object xmlInject(BeanDefinition beanDef) {
        XmlBeanDefinition beanDefinition = (XmlBeanDefinition) beanDef;
        // 取得对象所有的属性
        final Map<String, Object> properties = beanDefinition.getProperties();
        Iterator<String> it = properties.keySet().iterator();
        while( it.hasNext() ){
            Object value = properties.get(it.next());
            if (value instanceof ManagedRef) {
                ManagedRef ref = (ManagedRef)value;
                BeanDefinition bean = null;
                BeanDefinition b = findBeanDefinition(ref.getBeanName());
                if (b != null){
                    bean = b;
                    if( !bean.isFinished() ) {
                        inject(bean);
                    }
                }else{
                    error(ref.getBeanName()+": bean is null");
                }
            }
        }
        
        // 取得需要注入的对象
        final Object object = beanDefinition.getObject();
        Class<?> clazz = object.getClass();
        // 遍历所有注册的set方法注入
        MvcReflectUtils.getSetterMethods(clazz, new BeanMethodFilter(){
            @Override
            public boolean accept(String propertyName, Method method) {
                Object value = properties.get(propertyName);
                if (value != null) {
                    try {
                        method.invoke(object, getInjectArg(value, method));
                    } catch (Throwable t) {
                        log.error(t, "xml inject error "+object.getClass().getName());
                    }
                }
                return false;
        }});
    
        if( BeanDefinition.METHOD_BEAN_TYPE.equals(beanDefinition.getBeanType()) ){
            try {
                Method method = clazz.getDeclaredMethod(beanDefinition.getMethodName(), new Class[]{});
                method.setAccessible(true);
                Object result = method.invoke(object, new Object[]{});
                
                beanDefinition.setObject(result);
                beanDefinition.setClassName(result.getClass().getName());
                // 取得接口名称
                String[] names = MvcReflectUtils.getInterfaceNames(result.getClass());
                beanDefinition.setInterfaceNames(names);
            } catch (Throwable e) {
                log.error(e, "xml inject error");
            }
        }
        
        beanDefinition.setFinished(true);
        addObjectToContext(beanDefinition);
        return beanDefinition.getObject();
    }

    /**
     * @param value 属性值的元信息
     * @param method 该属性的set方法
     * @return
     */
    private Object getInjectArg(Object value, Method method) {
        if (value instanceof ManagedValue) { // value
            return getValueArg(value, method);
        } else if (value instanceof ManagedRef) { // ref
            return getRefArg(value);
        } else if (value instanceof ManagedList) { // list
            return getListArg(value, method);
        } else if (value instanceof ManagedArray) { // array
            return getArrayArg(value, method);
        } else if (value instanceof ManagedMap) { // map
            return getMapArg(value, method);
        } else
            return null;
    }

    
    private Object getValueArg(Object value, Method method) {
        ManagedValue managedValue = (ManagedValue) value;
        String typeName = null;
        if (method == null) {
            typeName = MvcUtils.StringUtil.isNullOrEmpty(managedValue.getTypeName()) ? null
                    : managedValue.getTypeName();
        } else {
            typeName = MvcUtils.StringUtil.isNullOrEmpty(managedValue.getTypeName()) ? method
                    .getParameterTypes()[0].getName() : managedValue.getTypeName();
        }

        //log.debug("value type - " + typeName);
        return MvcConvertUtils.convert(managedValue.getValue(), typeName);
    }

    private Object getRefArg(Object value) {
        ManagedRef ref = (ManagedRef) value;
        Object instance = map.get(ref.getBeanName());
        if (instance == null) {
            BeanDefinition b = findBeanDefinition(ref.getBeanName());
            if (b != null)
                instance = inject(b);
        }
        return instance;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getListArg(Object value, Method method) {
        Class<?> setterParamType = null;
        if (method != null) {
            setterParamType = method.getParameterTypes()[0];
        }
        ManagedList<Object> values = (ManagedList<Object>) value;
        Collection collection = null;

        if ( MvcUtils.StringUtil.isNotBlank(values.getTypeName()) ) { // 指定了list的类型
            try {
                collection = (Collection) getBeanClassLoader().loadClass(values.getTypeName()).newInstance();
            } catch (Throwable t) {
                log.error(t, "list inject error");
            }
        } else { // 根据set方法参数类型获取list类型
            collection = (setterParamType == null ? new ArrayList()
                    : MvcConvertUtils.getCollectionObj(setterParamType));
        }

        for (Object item : values) {
            Object listValue = getInjectArg(item, null);
            collection.add(listValue);
        }

        return collection;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getArrayArg(Object value, Method method) {
        Class<?> setterParamType = null;
        if (method != null) {
            setterParamType = method.getParameterTypes()[0];
        }
        ManagedArray<Object> values = (ManagedArray<Object>) value;
        
        Collection collection = new ArrayList();// 指定为ArrayList
        
        for (Object item : values) {
            Object listValue = getInjectArg(item, null);
            collection.add(listValue);
        }
        return MvcConvertUtils.convert(collection, setterParamType);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getMapArg(Object value, Method method) {
        Class<?> setterParamType = null;
        if (method != null) {
            setterParamType = method.getParameterTypes()[0];
        }
        ManagedMap<Object, Object> values = (ManagedMap<Object, Object>) value;
        Map m = null;
        if ( MvcUtils.StringUtil.isNotBlank(values.getTypeName())) {
            try {
                m = (Map) getBeanClassLoader().loadClass(values.getTypeName()).newInstance();
            } catch (Throwable t) {
                log.error(t, "map inject error");
            }
        } else { // 根据set方法参数类型获取map类型
            m = (setterParamType == null ? new HashMap() : MvcConvertUtils
                    .getMapObj(setterParamType));
            //log.debug("map ret - " + m.getClass().getName());
        }
        for (Object o : values.keySet()) {
            Object k = getInjectArg(o, null);
            Object v = getInjectArg(values.get(o), null);
            m.put(k, v);
        }
        return m;
    }

    
    private Object annotationInject(BeanDefinition beanDef) {
        AnnotationBeanDefinition beanDefinition = (AnnotationBeanDefinition) beanDef;
        
        fieldInject(beanDefinition);
        methodInject(beanDefinition);
        
        beanDefinition.setFinished(true);
        addObjectToContext(beanDefinition);
        return beanDefinition.getObject();
    }

    private void fieldInject(AnnotationBeanDefinition beanDefinition) {
        Object object = beanDefinition.getObject();

        // 属性注入
        for (Field field : beanDefinition.getInjectFields()) {
            field.setAccessible(true);
            Class<?> clazz = field.getType();
            String id = field.getAnnotation(Inject.class).value();
            String key = MvcUtils.StringUtil.isNotBlank(id) ? id : clazz.getName();
            Object instance = map.get(key);
            if (instance == null) {
                
                BeanDefinition bean = findBeanDefinition( key );
                if (bean != null){
                    if( !bean.isFinished() ) instance = inject(bean);
                }else{
                    error( key +": bean is null");
                }
            }
            if (instance != null) {
                try {
                    field.set(object, instance);
                } catch (Throwable t) {
                    log.error(t, "field inject error");
                }
            }
        }
    }

    private void methodInject(AnnotationBeanDefinition beanDefinition) {
        Object object = beanDefinition.getObject();
        // 从方法注入
        for (Method method : beanDefinition.getInjectMethods()) {
            method.setAccessible(true);
            Class<?>[] params = method.getParameterTypes();
            Object[] p = new Object[params.length];
            for (int i = 0; i < p.length; i++) {
                String key = params[i].getName();
                Object instance = map.get(key);
                if (instance != null) {
                    p[i] = instance;
                } else {
                    
                    BeanDefinition bean = findBeanDefinition( key );
                    if (bean != null){
                        if( !bean.isFinished() ) p[i] = inject(bean);
                    }else{
                        error( key +": bean is null");
                    }
                }
            }
            try {
                method.invoke(object, p);
            } catch (Throwable t) {
                log.error(t, "method inject error");
            }
        }
    }

    private BeanDefinition findBeanDefinition(String key) {
        check(key);
        BeanDefinition ret = null;
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (key.equals(beanDefinition.getId())) {
                ret = beanDefinition;
                break;
            } else if (key.equals(beanDefinition.getClassName())) {
                ret = beanDefinition;
                break;
            } else {
                for (String interfaceName : beanDefinition.getInterfaceNames()) {
                    if (key.equals(interfaceName)) {
                        ret = beanDefinition;
                        break;
                    }
                }
            }
        }
        return ret;
    }
    
    private void check(String key) {
        if(errorMemo.contains(key)) {
            error(key + " auto inject failure!");
        }
    }
    
    protected Set<String> errorMemo = new HashSet<String>();
    
    private Logger log = LogFactory.getLogger(AbstractBeanFactory.class);
    
    /**
     * 处理异常
     * @param msg 异常信息
     */
    protected void error(String msg) {
        log.error(msg);
        throw new BeanDefinitionParsingException(msg);
    }
}