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
 * Create by ZollTy on 2013-10-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.ext.jdbc;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * Helper class that simplifies JNDI operations. It provides methods to lookup and bind objects, and
 * allows implementations of the {@link JndiCallback} interface to perform any operation they like
 * with a JNDI naming context provided.
 *
 */
public class JndiTemplate {

    private static Logger logger = LogFactory.getLogger(JndiTemplate.class);

    private Properties environment;

    /**
     * Create a new JndiTemplate instance.
     */
    public JndiTemplate() {
    }

    /**
     * Create a new JndiTemplate instance, using the given environment.
     */
    public JndiTemplate(Properties environment) {
        this.environment = environment;
    }

    /**
     * Set the environment for the JNDI InitialContext.
     */
    public void setEnvironment(Properties environment) {
        this.environment = environment;
    }

    /**
     * Return the environment for the JNDI InitialContext, if any.
     */
    public Properties getEnvironment() {
        return this.environment;
    }

    /**
     * Release a JNDI context as obtained from {@link #getContext()}.
     * 
     * @param ctx
     *            the JNDI context to release (may be <code>null</code>)
     * @see #getContext
     */
    public void releaseContext(Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            }
            catch (NamingException ex) {
                logger.debug("Could not close JNDI InitialContext", ex);
            }
        }
    }

    /**
     * Create a new JNDI initial context. Invoked by {@link #getContext}.
     * <p>
     * The default implementation use this template's environment settings. Can be subclassed for
     * custom contexts, e.g. for testing.
     * 
     * @return the initial Context instance
     * @throws NamingException
     *             in case of initialization errors
     */
    protected Context createInitialContext() throws NamingException {
        Hashtable<String, Object> icEnv = null;
        Properties env = getEnvironment();
        if (env != null) {
            icEnv = new Hashtable<String, Object>(env.size());
            mergePropertiesIntoMap(env, icEnv);
        }
        return new InitialContext(icEnv);
    }

    /**
     * Look up the object with the given name in the current JNDI context.
     * 
     * @param name
     *            the JNDI name of the object
     * @return object found (cannot be <code>null</code>; if a not so well-behaved JNDI
     *         implementations returns null, a NamingException gets thrown)
     * @throws NamingException
     *             if there is no object with the given name bound to JNDI
     */
    public Object lookup(final String name) throws NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Looking up JNDI object with name [" + name + "]");
        }
        Context ctx = createInitialContext();
        try {
            Object located = ctx.lookup(name);
            if (located == null) {
                throw new NameNotFoundException("JNDI object with [" + name
                        + "] not found: JNDI implementation returned null");
            }
            return located;
        } finally {
            releaseContext(ctx);
        }
    }

    /**
     * Look up the object with the given name in the current JNDI context.
     * 
     * @param name
     *            the JNDI name of the object
     * @param requiredType
     *            type the JNDI object must match. Can be an interface or superclass of the actual
     *            class, or <code>null</code> for any match. For example, if the value is
     *            <code>Object.class</code>, this method will succeed whatever the class of the
     *            returned instance.
     * @return object found (cannot be <code>null</code>; if a not so well-behaved JNDI
     *         implementations returns null, a NamingException gets thrown)
     * @throws NamingException
     *             if there is no object with the given name bound to JNDI
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String name, Class<T> requiredType) throws NamingException {
        Object jndiObject = lookup(name);
        if (requiredType != null && !requiredType.isInstance(jndiObject)) {
            throw new NamingException("Object of type ["
                    + (jndiObject != null ? jndiObject.getClass() : null)
                    + "] available at JNDI location [" + name + "] is not assignable to ["
                    + requiredType.getName() + "]");
        }
        return (T) jndiObject;
    }

    /**
     * Merge the given Properties instance into the given Map, copying all properties (key-value
     * pairs) over.
     * <p>
     * Uses <code>Properties.propertyNames()</code> to even catch default properties linked into the
     * original Properties instance.
     * 
     * @param props
     *            the Properties instance to merge (may be <code>null</code>)
     * @param map
     *            the target Map to merge the properties into
     */
    public static void mergePropertiesIntoMap(Properties props, Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (props != null) {
            for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements();) {
                String key = (String) en.nextElement();
                Object value = props.getProperty(key);
                if (value == null) {
                    // Potentially a non-String value...
                    value = props.get(key);
                }
                map.put(key, value);
            }
        }
    }

}