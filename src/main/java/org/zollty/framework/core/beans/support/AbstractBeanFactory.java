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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zollty.framework.core.annotation.Inject;
import org.zollty.framework.core.annotation.MethodBeanId;
import org.zollty.framework.core.beans.BeanDefinition;
import org.zollty.framework.core.beans.BeansException;
import org.zollty.framework.core.beans.ConfigurableBeanFactory;
import org.zollty.framework.core.beans.annotation.AnnotationBeanDefinition;
import org.zollty.framework.core.beans.xml.ManagedArray;
import org.zollty.framework.core.beans.xml.ManagedList;
import org.zollty.framework.core.beans.xml.ManagedMap;
import org.zollty.framework.core.beans.xml.ManagedRef;
import org.zollty.framework.core.beans.xml.ManagedValue;
import org.zollty.framework.core.beans.xml.XmlBeanDefinition;
import org.zollty.framework.util.MvcUtils;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.NestedRuntimeException;

/**
 * 
 * @author zollty
 * @since 2013-10-11
 */
abstract public class AbstractBeanFactory implements ConfigurableBeanFactory {

    private Logger log = LogFactory.getLogger(AbstractBeanFactory.class);

    /** ClassLoader to resolve bean class names with, if necessary */
    private ClassLoader beanClassLoader;

    protected List<BeanDefinition> beanDefinitions;

    private Map<String, Object> beanMap;

    private Set<String> errorConflict;

    /**
     * Using MvcUtils.ClassUtil.getDefaultClassLoader() for beanClassLoader
     */
    public AbstractBeanFactory() {
        this.beanClassLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
    }

    /**
     * Using the given beanClassLoader
     */
    public AbstractBeanFactory(ClassLoader beanClassLoader) {
        if (beanClassLoader != null) {
            this.beanClassLoader = beanClassLoader;
        }
        else {
            this.beanClassLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
        }
    }

    @Override
    public void refresh() {
        // 刷新之前执行个性化操作
        doBeforeRefresh();

        // ~初始化工作
        close();
        doAfterClose();
        errorConflict = new HashSet<String>();
        beanMap = new HashMap<String, Object>();
        // ~End

        beanDefinitions = loadBeanDefinitions();
        check(); // 冲突检测
        addObjectToContext();

        // 把当前的BeanFactory或者ApplicationContext转存一份到BeanFactoryHelper静态方法中，以便外部调用
        BeanFactoryHelper.refreshBeanFactory(this);

        log.debug("BeanFactory refresh success! bean size = {}", beanMap.size());
        // 刷新之后执行个性化操作
        doAfterRefresh();
    }

    @Override
    public void close() {
        beanDefinitions = null;
        errorConflict = null;
        beanMap = null;
    }

    // 交给子类去实现
    /** 加载beans的定义 */
    abstract protected List<BeanDefinition> loadBeanDefinitions();

    /** 在刷新前做的一些个性化操作 */
    abstract protected void doBeforeRefresh();

    /** 在刷新后做的一些个性化操作 */
    abstract protected void doAfterRefresh();

    /** 关闭之后，执行一些个性化操作 */
    abstract protected void doAfterClose();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz) {
        return (T) beanMap.get(clazz.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String id) {
        return (T) beanMap.get(id);
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        return this.beanDefinitions;
    }

    @Override
    public Map<String, Object> getBeanMap() {
        return beanMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new LinkedHashMap<String, T>(4);
        for (BeanDefinition def : beanDefinitions) {
            if (type.getName().equals(def.getClassName())) {
                if (def.getId() != null) {
                    result.put(def.getId(), (T) beanMap.get(def.getId()));
                }
                else {
                    Object obj = beanMap.get(def.getClassName());
                    if (obj != null) {
                        result.put(def.getClassName(), (T) obj);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 这是一个递归处理过程，递归inject(BeanDefinition beanDef)方法，其逻辑大概如下：
     * 调用inject方法，判断bean是否已经完成处理，如果完成，调用addObjectToContext(BeanDefinition beanDefinition) 方法，
     *  把它放到map中。如果未完成，判断它是XmlBean还是AnnotationBean，分别进行解析。 以AnnotationBean解析为例，检查
     * fieldInject(beanDefinition) 和 methodInject(beanDefinition)。 检查 它是否有注入的其他bean，如果有，则调用
     * findBeanDefinition(String key)检查是否有此bean实例存在且是否唯一。
     * 如果有这个bean，看它是否已经处理完成，如果未完成，则继续调用inject方法解析，重复开始的步骤。
     */
    protected void addObjectToContext() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            inject(beanDefinition);
        }
    }

    private Object inject(BeanDefinition beanDef) {
        if (!beanDef.isFinished()) {
            if (beanDef instanceof XmlBeanDefinition) {
                return xmlInject(beanDef);
            }
            if (beanDef instanceof AnnotationBeanDefinition) {
                return annotationInject(beanDef);
            }

            return null;
        }

        return beanDef.getObject();
    }

    protected void addObjectToContext(BeanDefinition beanDefinition) {
        Object object = beanDefinition.getObject();
        // 把id作为key
        String id = beanDefinition.getId();
        if (MvcUtils.StringUtil.isNotBlank(id))
            beanMap.put(id, object);

        // ----------Begin 以下副本用于根据类型自动注入
        // 把类名作为key
        beanMap.put(beanDefinition.getClassName(), object);
        // 把接口名作为key
        String[] keys = beanDefinition.getInterfaceNames();
        for (String k : keys) {
            beanMap.put(k, object);
        }
        // ----------End-----------
    }

    /**
     * 1.id相同的抛异常 
     * 2.className或者interfaceName相同，但其中任意一个没有定义id，抛异常
     * 3.className或者interfaceName相同，且都定义的id，需要保存备忘，按类型或者接口自动注入的时候抛异常
     */
    protected void check() {
        for (int i = 0; i < beanDefinitions.size(); i++) {
            for (int j = i + 1; j < beanDefinitions.size(); j++) {
                BeanDefinition b1 = beanDefinitions.get(i);
                BeanDefinition b2 = beanDefinitions.get(j);
                if (MvcUtils.StringUtil.isNotBlank(b1.getId()) && b1.getId().equals(b2.getId())) {

                    throw new BeansException(
                            "bean [{}] and [{}] have duplicate id", b1.getClassName(),
                            b2.getClassName());
                }
                if (b1.getClassName().equals(b2.getClassName())) {
                    if (MvcUtils.StringUtil.isNullOrEmpty(b1.getId())
                            || MvcUtils.StringUtil.isNullOrEmpty(b2.getId())) {

                        throw new BeansException("bean [{}] duplicate definition",
                                b1.getClassName());
                    }
                    errorConflict.add(b1.getClassName());
                }
                for (String iname1 : b1.getInterfaceNames()) {
                    for (String iname2 : b2.getInterfaceNames()) {
                        if (iname1.equals(iname2)) {
                            if (MvcUtils.StringUtil.isNullOrEmpty(b1.getId())
                                    || MvcUtils.StringUtil.isNullOrEmpty(b2.getId())) {

                                throw new BeansException(
                                        "bean [{}] duplicate definition", iname1);
                            }
                            errorConflict.add(iname1);
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

        // ----------Step 1. 对Ref节点对应的bean进行预先解析。这一步非必须，可以省略。
        Iterator<String> it = properties.keySet().iterator();
        while (it.hasNext()) {
            Object value = properties.get(it.next());
            if (value instanceof ManagedRef) {
                ManagedRef ref = (ManagedRef) value;
                recursiveInject(ref.getBeanName());
            }
        }

        // -----------Step 2. 调用Setter方法，将所有属性设置进去。
        final Object object = beanDefinition.getObject(); // 取得需要注入的对象
        Class<?> clazz = object.getClass();
        // 遍历所有注册的set方法注入，比如 setAge() 方法，获取到该method，找到该属性的properties value，
        // 然后调用 getInjectArg(value, setterParamType) 获取参数的实例，根据method获取method参数的类型，
        // 比如age，是 int类型，那么 getInjectArg(value, method) 取得的就是 int类型。
        MvcUtils.ReflectUtil.getSetterMethods(clazz, new MvcUtils.ReflectUtil.BeanMethodFilter() {
            @Override
            public boolean accept(String propertyName, Method method) {
                Object value = properties.get(propertyName);
                if (value != null) {
                    try {
                        method.invoke(
                                object,
                                getInjectArg(value,
                                        method == null ? null : method.getParameterTypes()[0]));
                    }
                    catch (Throwable t) {
                        throw new NestedRuntimeException(t, "xml inject error [{}]", object
                                .getClass().getName());
                    }
                }
                return false;
            }
        });

        // -----------Step 3. 如果是method_bean类型，则调用method获取result object，替换掉beanDefinition中原来的
        // Object、ClassName、InterfaceNames。其余的Id属性值保持不变。（偷梁换柱，把原来的对象换成通过method方法获取的对象）
        // @see org.zollty.framework.core.support.BeanDefinition
        if (BeanDefinition.METHOD_BEAN_TYPE.equals(beanDefinition.getBeanType())) {
            try {
                Method method = clazz.getDeclaredMethod(beanDefinition.getMethodName(),
                        new Class[] {});
                method.setAccessible(true);
                Object result = method.invoke(object, new Object[] {});

                beanDefinition.setObject(result);
                beanDefinition.setClassName(result.getClass().getName());
                // 取得接口名称
                String[] names = MvcUtils.ReflectUtil.getInterfaceNames(result.getClass());
                beanDefinition.setInterfaceNames(names);
            }
            catch (Throwable t) {
                throw new NestedRuntimeException(t, "xml inject error [{}]", clazz.getName());
            }
        }

        beanDefinition.setFinished(true);
        addObjectToContext(beanDefinition);
        return beanDefinition.getObject();
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
        else if (value instanceof ManagedRef) { // ref
            return getRefArg(value);
        }
        else if (value instanceof ManagedList) { // list
            return getListArg(value, setterParamType);
        }
        else if (value instanceof ManagedArray) { // array
            return getArrayArg(value, setterParamType);
        }
        else if (value instanceof ManagedMap) { // map
            return getMapArg(value, setterParamType);
        }
        else {
            return null;
        }
    }

    private Object getValueArg(Object value, Class<?> setterParamType) {
        ManagedValue managedValue = (ManagedValue) value;
        String typeName = null;
        if (MvcUtils.StringUtil.isNotBlank(managedValue.getTypeName())) {
            typeName = managedValue.getTypeName();
        }
        else if (setterParamType != null) {
            typeName = setterParamType.getName();
        }
        // log.debug("value type - " + typeName);
        return MvcUtils.ConvertUtil.convert(managedValue.getValue(), typeName);
    }

    private Object getRefArg(Object value) {
        ManagedRef ref = (ManagedRef) value;
        Object instance = beanMap.get(ref.getBeanName());
        if (instance == null) {
            return recursiveInject(ref.getBeanName());
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
            // log.debug("map ret - " + m.getClass().getName());
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

    private void fieldInject(AnnotationBeanDefinition beanDefinition) {
        Object object = beanDefinition.getObject();

        // 属性注入
        for (Field field : beanDefinition.getInjectFields()) {
            field.setAccessible(true);
            Class<?> clazz = field.getType();
            String id = field.getAnnotation(Inject.class).value();

            String key = null;
            // id为空时按类型注入，调用checkConflict检查是否冲突
            if (MvcUtils.StringUtil.isBlank(id)) {
                key = clazz.getName();
                checkConflict(key, object.getClass().getName());
            }
            else {
                key = id;
            }
            Object instance = beanMap.get(key);
            if (instance == null) {
                instance = recursiveInject(key);
            }
            if (instance != null) {
                try {
                    field.set(object, instance);
                }
                catch (Throwable t) {
                    throw new NestedRuntimeException(t, "field inject error [{}]", object
                            .getClass().getName());
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
            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < p.length; i++) {
                Annotation anno = getMethodBeanIdAnno(annotations[i]);
                String id = null;
                if (anno != null) {
                    MethodBeanId mbId = (MethodBeanId) anno;
                    id = mbId.value();
                }
                String key = null;
                // id为空时按类型注入，调用checkConflict检查是否冲突
                if (MvcUtils.StringUtil.isBlank(id)) {
                    key = params[i].getName();
                    checkConflict(key, object.getClass().getName());
                }
                else {
                    key = id;
                }

                Object instance = beanMap.get(key);
                if (instance != null) {
                    p[i] = instance;
                }
                else {
                    p[i] = recursiveInject(key);
                }
            }

            try {
                method.invoke(object, p);
            }
            catch (Throwable t) {
                throw new NestedRuntimeException(t, "method inject error [{}]", object.getClass()
                        .getName());
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

    private Object recursiveInject(String key) {
        BeanDefinition bean = findBeanDefinition(key);
        if (bean == null) {
            throw new BeansException("bean [{}] is null", key);
        }

        return inject(bean);
    }

    private BeanDefinition findBeanDefinition(String key) {
        // checkConflict(key);
        BeanDefinition ret = null;
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (key.equals(beanDefinition.getId())) {
                ret = beanDefinition;
                break;
            }
            else if (key.equals(beanDefinition.getClassName())) {
                ret = beanDefinition;
                break;
            }

            for (String interfaceName : beanDefinition.getInterfaceNames()) {
                if (key.equals(interfaceName)) {
                    ret = beanDefinition;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 按类型注入，检查是否冲突
     */
    private void checkConflict(String key, String info) {
        if (errorConflict.contains(key)) {
            throw new BeansException(
                    "[{}]: Anonymous Inject failure! Bean [{}] has multi matching.", info, key);
        }
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

}