/* 
 * Copyright (C) 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by ZollTy on 2013-10-11 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.beans.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.NestedRuntimeException;
import org.zollty.framework.core.annotation.Inject;
import org.zollty.framework.core.annotation.MethodBeanId;
import org.zollty.framework.core.beans.BeanDefinition;
import org.zollty.framework.core.beans.BeansException;
import org.zollty.framework.core.beans.annotation.AnnotationBeanDefinition;
import org.zollty.framework.core.beans.xml.ManagedArray;
import org.zollty.framework.core.beans.xml.ManagedList;
import org.zollty.framework.core.beans.xml.ManagedMap;
import org.zollty.framework.core.beans.xml.ManagedRef;
import org.zollty.framework.core.beans.xml.ManagedValue;
import org.zollty.framework.core.beans.xml.XmlBeanDefinition;
import org.zollty.framework.util.MvcUtils;

/**
 * Used for beans loading and injection.
 * 
 * @author zollty
 * @since 2013-10-11
 */
class BeansLoader {

    private Logger log = LogFactory.getLogger(BeansLoader.class);

    /** ClassLoader to resolve bean class names with, if necessary */
    private final ClassLoader beanClassLoader;

    private List<XmlBeanDefinition> xmlBeanDefinitions;
    private List<AnnotationBeanDefinition> annoBeanDefinitions;

    private Map<String, Object> beanMap;

    private Set<String> conflictMem;

    /**
     * Using the given beanClassLoader
     */
    BeansLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public void refresh(List<XmlBeanDefinition> xmlBeanDefinitions,
            List<AnnotationBeanDefinition> annoBeanDefinitions) {
        this.xmlBeanDefinitions = xmlBeanDefinitions;
        this.annoBeanDefinitions = annoBeanDefinitions;
        
        // ~刷新之前把旧的重置
        conflictMem = new HashSet<String>();
        beanMap = new HashMap<String, Object>();
        // ~End

        startXmlInject();
        // 冲突检测，之所以在XmlInject之后再进行检测，是因为在XmlInject之前，有些beanDef是没有className和InterfaceName的
        checkConflict();
        startAnnotationInject();
        
        // 释放中间变量
        clearTempProps();
    }

    /**
     * 释放中间变量
     */
    protected void clearTempProps() {
        // 没用了，释放它们
        conflictMem = null;
        xmlBeanDefinitions = null;
        annoBeanDefinitions = null;
    }
    
    /**
     * bean解析完成后放入beanMap中
     */
    protected void addObjectToContext(BeanDefinition beanDefinition) {
        Object object = beanDefinition.getObject();
        // 把id作为key
        String id = beanDefinition.getId();
        if (MvcUtils.StringUtil.isNotBlank(id)) {
            beanMap.put(id, object);
        }

        // 以下副本用于根据类型自动注入，如果多次调用本方法，则后者会覆盖前者，只会保留一份
        // 把类名作为key
        beanMap.put(beanDefinition.getClassName(), object);
        // 把接口名作为key
        String[] keys = beanDefinition.getInterfaceNames();
        for (String k : keys) {
            beanMap.put(k, object);
        }
    }

    /**
     * 处理Xml Bean，这是一个递归处理过程，递归doXmlInject(BeanDefinition beanDef)方法，其逻辑大概如下：
     * 调用doXmlInject方法，判断bean是否已经完成处理，如果完成，调用addObjectToContext(BeanDefinition beanDefinition) 方法，
     *  把它放到map中。如果未完成，判断它是XmlBean还是AnnotationBean，分别进行解析。 以AnnotationBean解析为例，检查
     * fieldInject(beanDefinition) 和 methodInject(beanDefinition)。 检查 它是否有注入的其他bean，如果有，则调用
     * findBeanDefinition(String key)检查是否有此bean实例存在且是否唯一。
     * 如果有这个bean，看它是否已经处理完成，如果未完成，则继续调用doXmlInject方法解析，重复开始的步骤。
     */
    protected void startXmlInject() {
        if (xmlBeanDefinitions == null) {
            return;
        }
        for (XmlBeanDefinition beanDef : xmlBeanDefinitions) {
            doXmlInject(beanDef);
        }
    }

    /**
     * 处理Annotation Bean，类似于startXmlInject()
     */
    protected void startAnnotationInject() {
        if (annoBeanDefinitions == null) {
            return;
        }
        for (AnnotationBeanDefinition beanDef : annoBeanDefinitions) {
            doAnnotationInject(beanDef);
        }
    }

    private Object doXmlInject(BeanDefinition beanDef) {
        if (!beanDef.isFinished()) {
            return xmlInject(beanDef);
        }
        return beanDef.getObject();
    }

    private Object doAnnotationInject(BeanDefinition beanDef) {
        if (!beanDef.isFinished()) {
            return annotationInject(beanDef);
        }
        return beanDef.getObject();
    }

    private Object xmlInject(BeanDefinition beanDef) {
        XmlBeanDefinition beanDefinition = (XmlBeanDefinition) beanDef;
        
        // Pre resolution Ref Node
        lookupRefBean(beanDefinition.getProperties());
        
        // Get paramTypes and values
        LinkedList<Object> args = beanDefinition.getConstructorArgs();
        Class<?>[] paramTypes = new Class[args == null ? 0 : args.size()];
        Object[] values = new Object[paramTypes.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = getInjectArg(args.poll(), null);
            if (values[i] != null) {
                paramTypes[i] = values[i].getClass();
                // log.debug("param type - " + vaos[i].getClass());
            } else {
                paramTypes[i] = null;
            }
        }

        // 如果是method_bean类型，则调用method获取result object.
        if (BeanDefinition.METHOD_BEAN_TYPE.equals(beanDefinition.getBeanType())) {
            refreshMethodBean(beanDefinition, paramTypes, values);
        } else {
            // 实例化对象
            Class<?> clazz = ClassTools.loadClass(beanDefinition.getClassName(), beanClassLoader);
            if (paramTypes.length == 0) {
                beanDefinition.setObject(MvcUtils.ReflectionUtil.newInstance(clazz));
            } else {
                Constructor<?> constructor = ClassTools.findConstructor(clazz, paramTypes);
                if (constructor == null) {
                    throw new NestedRuntimeException("can not find the special constructor '{}#({})'",
                            beanDefinition.getClassName(), Arrays.toString(paramTypes));
                }
                try {
                    constructor.setAccessible(true);
                    beanDefinition.setObject(constructor.newInstance(values));
                } catch (Exception e) {
                    throw new NestedRuntimeException(e, "newInstance error!");
                } catch (LinkageError ex) {
                    throw new NestedRuntimeException(ex, "newInstance error!");
                }
            }

            // 取得接口名称
            String[] names = MvcUtils.ClassUtil.getInterfaceNames(clazz, beanClassLoader);
            beanDefinition.setInterfaceNames(names);
            log.info("class [" + beanDefinition.getClassName() + "] names size [" + names.length + "]");
            
            final Object object = beanDefinition.getObject(); // 取得需要注入的对象
            assignSetterProperty(object, beanDefinition.getProperties());
        }

        beanDefinition.setFinished(true);
        // 添加到beanMap
        addObjectToContext(beanDefinition);
        
        return beanDefinition.getObject();
    }
    
    // 预先对Ref节点对应的bean进行解析。这一步非必须。
    private void lookupRefBean(final Map<String, Object> properties) {
        if (!properties.isEmpty()) {
            Iterator<String> it = properties.keySet().iterator();
            while (it.hasNext()) {
                Object value = properties.get(it.next());
                if (value instanceof ManagedRef) {
                    ManagedRef ref = (ManagedRef) value;
                    recursiveXmlInject(ref.getBeanName());
                }
            }
        }
    }
    
    // 调用Setter方法，将所有属性设置进去。
    private void assignSetterProperty(final Object object, final Map<String, Object> properties) {
        if (properties.isEmpty()) {
            return;
        }
        Class<?> clazz = object.getClass();
        // 遍历所有注册的set方法注入，比如 setAge() 方法，获取到该method，找到该属性的properties value，
        // 然后调用 getInjectArg(value, setterParamType) 获取参数的实例，根据method获取method参数的类型，
        // 比如age，是 int类型，那么 getInjectArg(value, method) 取得的就是 int类型。
        MvcUtils.ReflectUtil.getSetterMethods(clazz, new MvcUtils.ReflectUtil.BeanMethodFilter() {
            @Override
            public boolean accept(String propertyName, Method method) {
                Object value = properties.get(propertyName);
                if (value != null) {
                    value = getInjectArg(value, method == null ? null 
                            : method.getParameterTypes()[0]);
                    try {
                        MvcUtils.ReflectionUtil.invokeMethod(method, object, value);
                    }
                    catch (Exception t) {
                        throw new NestedRuntimeException(t, "xml inject error [method={}, value={}]", 
                                method.toString(), value.toString());
                    }
                }
                return false;
            }
        });
    }
    
    private void refreshMethodBean(XmlBeanDefinition beanDefinition,
            Class<?>[] paramTypes, Object[] values) {
        
        // 获取宿主对象实例
        Object object = null;
        // 如果以className为id的bean已经存在，则说明是Ref对象，直接从beanDefinition获取
        BeanDefinition bean = findXmlBeanDefinitionById(beanDefinition.getClassName());
        if (bean != null) {
            object = doXmlInject(bean);
        }
        
        Class<?> clazz = (object == null) ? 
                ClassTools.loadClass(beanDefinition.getClassName(), beanClassLoader) : object.getClass();
        // 查找Method
        Method method = ClassTools.findMethod(clazz, beanDefinition.getMethodName(), paramTypes);
        if (method == null) {
            throw new NestedRuntimeException("can not find the special method '{}#{}'",
                    beanDefinition.getClassName(), beanDefinition.getMethodName());
        }
        // 如果object未实例化，且非static方法调用，则直接根据class.newInstance
        if (object == null && !Modifier.isStatic(method.getModifiers())) {
            object = MvcUtils.ReflectionUtil.newInstance(clazz);
            assignSetterProperty(object, beanDefinition.getProperties());
        }
        
        // 调用method.invoke获取目标对象
        Object result;
        try {
            method.setAccessible(true);
            result = MvcUtils.ReflectionUtil.invokeMethod(method, object, values);
        } catch (Exception t) {
            throw new NestedRuntimeException(t, "xml method bean inject error [{}#{}]",
                    beanDefinition.getClassName(), beanDefinition.getMethodName());
        }
        beanDefinition.setObject(result);
        beanDefinition.setClassName(result.getClass().getName());
        // 取得接口名称
        String[] names = MvcUtils.ClassUtil.getInterfaceNames(result.getClass(), getBeanClassLoader());
        beanDefinition.setInterfaceNames(names);
    }

    
    /**
     * 注意：调用该方法其Properties属性的ManagedRef节点，都已经解析完成了。 
     * 但是对于其List属性里面又包含ManagedRef节点的，则未做预先解析。
     * 
     * @param value
     *            属性值的元信息
     * @param setterParamType
     *            该属性的set方法的参数类型
     */
    private Object getInjectArg(Object value, Class<?> setterParamType) {
        if (value instanceof ManagedValue) { // value
            return getValueArg(value, setterParamType);
        }
        if (value instanceof ManagedRef) { // ref
            return getRefArg(value);
        }
        if (value instanceof ManagedList) { // list
            return getListArg(value, setterParamType);
        }
        if (value instanceof ManagedArray) { // array
            return getArrayArg(value, setterParamType);
        }
        if (value instanceof ManagedMap) { // map
            return getMapArg(value, setterParamType);
        }
        return null; // <null />结点，返回null值
    }

    private Object getValueArg(Object value, Class<?> setterParamType) {
        ManagedValue managedValue = (ManagedValue) value;
        if (managedValue.getValue() == null) {
            return null;
        }
        String typeName = null;
        if (MvcUtils.StringUtil.isNotBlank(managedValue.getTypeName())) {
            typeName = managedValue.getTypeName();
        } else if (setterParamType != null) {
            typeName = setterParamType.getName();
        }
        return MvcUtils.ConvertUtil.convert(managedValue.getValue(), typeName);
    }

    private Object getRefArg(Object value) {
        ManagedRef ref = (ManagedRef) value;
        Object instance = beanMap.get(ref.getBeanName());
        if (instance == null) {
            return recursiveXmlInject(ref.getBeanName());
        }
        return instance;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getListArg(Object value, Class<?> setterParamType) {
        ManagedList<Object> values = (ManagedList<Object>) value;
        Collection collection = null;

        if (MvcUtils.StringUtil.isNotBlank(values.getTypeName())) { // 指定了list的类型
            try {
                collection = (Collection) getBeanClassLoader().loadClass(values.getTypeName())
                        .newInstance();
            }
            catch (Throwable t) {
                throw new NestedRuntimeException(t, "list inject error Type=[{}]",
                        values.getTypeName());
            }
        }
        else { // 根据set方法参数类型获取list类型
            collection = (setterParamType == null ? new ArrayList() : MvcUtils.CollectionUtil
                    .getCollectionObj(setterParamType));
        }

        for (Object item : values) {
            Object listValue = getInjectArg(item, null);
            collection.add(listValue);
        }

        return collection;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getArrayArg(Object value, Class<?> setterParamType) {
        ManagedArray<Object> values = (ManagedArray<Object>) value;

        Collection collection = new ArrayList();// 指定为ArrayList

        for (Object item : values) {
            Object listValue = getInjectArg(item, null);
            collection.add(listValue);
        }
        
        if(setterParamType==null) {
            if (MvcUtils.StringUtil.isNotBlank(values.getTypeName())) { // 指定了list的类型
                setterParamType = MvcUtils.ConvertUtil.resolveArrayClass(values.getTypeName());
            }
        }
        
        return MvcUtils.CollectionUtil.toArrayObj(collection, setterParamType);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getMapArg(Object value, Class<?> setterParamType) {
        ManagedMap<Object, Object> values = (ManagedMap<Object, Object>) value;
        Map m = null;
        if (MvcUtils.StringUtil.isNotBlank(values.getTypeName())) {
            try {
                m = (Map) getBeanClassLoader().loadClass(values.getTypeName()).newInstance();
            }
            catch (Throwable t) {
                throw new NestedRuntimeException(t, "map inject error Type=[{}]",
                        values.getTypeName());
            }
        }
        else { // 根据set方法参数类型获取map类型
            m = (setterParamType == null ? new HashMap() : MvcUtils.CollectionUtil
                    .getMapObj(setterParamType));
        }
        for (Map.Entry<Object, Object> entry : values.entrySet()) {
            Object k = getInjectArg(entry.getKey(), null);
            Object v = getInjectArg(entry.getValue(), null);
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

    // 属性注入
    private void fieldInject(AnnotationBeanDefinition beanDefinition) {
        Object object = beanDefinition.getObject();
        for (Field field : beanDefinition.getInjectFields()) {
            field.setAccessible(true);
            Class<?> clazz = field.getType();
            String id = field.getAnnotation(Inject.class).value();

            String key = null;
            // id为空时按类型注入，调用checkConflict检查是否冲突
            if (MvcUtils.StringUtil.isBlank(id)) {
                key = clazz.getName();
                checkConflict4Inject(key, object.getClass().getName());
            }
            else {
                key = id;
            }
            Object instance = beanMap.get(key);
            if (instance == null) {
                instance = recursiveAnnotationInject(key, object.getClass().getName());
            }
            if (instance != null) {
                try {
                    field.set(object, instance);
                }
                catch (Throwable t) {
                    throw new NestedRuntimeException(t, "field inject error [{}]",
                            object.getClass().getName());
                }
            }
        }
    }

    // 从方法注入
    private void methodInject(AnnotationBeanDefinition beanDefinition) {
        Object object = beanDefinition.getObject();
        for (Method method : beanDefinition.getInjectMethods()) {
            method.setAccessible(true);
            Class<?>[] params = method.getParameterTypes();
            Object[] p = new Object[params.length];
            Annotation[][] annotations = method.getParameterAnnotations();
            String pid = method.getAnnotation(Inject.class).value();
            for (int i = 0; i < p.length; i++) {
                Annotation anno = getMethodBeanIdAnno(annotations[i]);
                String id = null;
                if (anno != null) {
                    MethodBeanId mbId = (MethodBeanId) anno;
                    id = mbId.value();
                }
                String key = null;
                if (MvcUtils.StringUtil.isEmpty(id)) {
                    if (p.length == 1 && MvcUtils.StringUtil.isNotEmpty(pid)) {
                        key = pid;
                    } else {
                        // id为空时按类型注入，调用checkConflict检查是否冲突
                        key = params[i].getName();
                        checkConflict4Inject(key, object.getClass().getName());
                    }
                }
                else {
                    key = id;
                }

                Object instance = beanMap.get(key);
                if (instance != null) {
                    p[i] = instance;
                }
                else {
                    p[i] = recursiveAnnotationInject(key, object.getClass().getName());
                }
            }

            try {
                MvcUtils.ReflectionUtil.invokeMethod(method, object, p);
            }
            catch (Exception t) {
                throw new NestedRuntimeException(t, "method inject error [{}]",
                        object.getClass().getName());
            }
        }
    }

    private Annotation getMethodBeanIdAnno(Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (a.annotationType().equals(MethodBeanId.class)) {
                return a;
            }
        }
        return null;
    }

    private Object recursiveXmlInject(String key) {
        BeanDefinition bean = findXmlBeanDefinition(key);
        if (bean == null) {
            throw new BeansException("can not findXmlBeanDefinition, bean [{}] is null", key);
        }
        return doXmlInject(bean);
    }
    
    private Object recursiveAnnotationInject(String key, String info) {
        BeanDefinition bean = findAnnoBeanDefinition(key);
        if (bean == null) {
            throw new BeansException("[{}]: can not findAnnoBeanDefinition, bean [{}] is null", info, key);
        }
        return doAnnotationInject(bean);
    }
    
    private BeanDefinition findXmlBeanDefinition(String key) {
        BeanDefinition ret = null;
        for (XmlBeanDefinition beanDefinition : xmlBeanDefinitions) {
            if (key.equals(beanDefinition.getId())) {
                ret = beanDefinition;
                break;
            }
            if (key.equals(beanDefinition.getClassName())) {
                ret = beanDefinition;
                break;
            }

            if (beanDefinition.getInterfaceNames() != null) {
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
    
    private BeanDefinition findXmlBeanDefinitionById(String id) {
        BeanDefinition ret = null;
        for (XmlBeanDefinition beanDefinition : xmlBeanDefinitions) {
            if (id.equals(beanDefinition.getId())) {
                ret = beanDefinition;
                break;
            }
        }
        return ret;
    }

    private BeanDefinition findAnnoBeanDefinition(String key) {
        BeanDefinition ret = null;
        for (AnnotationBeanDefinition beanDefinition : annoBeanDefinitions) {
            if (key.equals(beanDefinition.getId())) {
                ret = beanDefinition;
                break;
            }
            if (key.equals(beanDefinition.getClassName())) {
                ret = beanDefinition;
                break;
            }

            if (beanDefinition.getInterfaceNames() != null) {
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

    /**
     * 按类型注入，检查是否冲突
     */
    private void checkConflict4Inject(String key, String info) {
        if (conflictMem.contains(key)) {
            throw new BeansException(
                    "[{}]: Anonymous Inject failure! Bean [{}] has multi matching.", info, key);
        }
    }
    

    /**
     * 1.id相同的抛异常 
     * 2.className或者interfaceName相同，但其中任意一个没有定义id，抛异常
     * 3.className或者interfaceName相同，且都定义的id，需要保存备忘，按类型或者接口自动注入的时候抛异常
     */
    protected void checkConflict() {
        List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();
        if (xmlBeanDefinitions != null) {
            beanDefinitions.addAll(xmlBeanDefinitions);
        }
        if (annoBeanDefinitions != null) {
            beanDefinitions.addAll(annoBeanDefinitions);
        }
        for (int i = 0; i < beanDefinitions.size(); i++) {
            for (int j = i + 1; j < beanDefinitions.size(); j++) {
                BeanDefinition b1 = beanDefinitions.get(i);
                BeanDefinition b2 = beanDefinitions.get(j);
                if (b1.getId() != null && b1.getId().equals(b2.getId())) {
                    throw new BeansException(
                            "find duplicate id [] of bean [{}] and [{}]", b1.getId(),
                            b1.getClassName(), b2.getClassName());
                }
                if (b1.getClassName().equals(b2.getClassName())) {
                    if (b1.getId()==null || b2.getId()==null) {
                        throw new BeansException("beans [{}] duplicate definition, please set id for them.",
                                b1.getClassName());
                    }
                    // 否则添加到conflictMem
                    conflictMem.add(b1.getClassName());
                } else {
                    checkInterface(b1, b2);
                }
            }
        }
    }
    
    // 接口相同的情况，例如 Bean1=Person, Bean2=Teacher，它们都没有ID，那么如果需要注入Person时则难以选择。
    private void checkInterface(BeanDefinition b1, BeanDefinition b2) {
        for (String iname1 : b1.getInterfaceNames()) {
            for (String iname2 : b2.getInterfaceNames()) {
                if (iname1.equals(iname2)) {
                    if (b1.getId() == null || b2.getId() == null) {
                        throw new BeansException(
                                "beans [{}] duplicate definition, please set id for them.", iname1);
                    }
                    // 否则添加到conflictMem
                    conflictMem.add(iname1);
                    continue;
                }
            }
        }
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }
    
    public Map<String, Object> getBeanMap() {
        return beanMap;
    }

}