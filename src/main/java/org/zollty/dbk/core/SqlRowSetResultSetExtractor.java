/*
 * @(#)SqlRowSetResultSetExtractor.java
 * Create by Zollty_Tsow on 2013-12-8 
 * you may find ZollTy at csdn, github, oschina, stackoverflow...
 * e.g. https://github.com/zollty  http://www.cnblogs.com/zollty 
 * 
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 */
package org.zollty.dbk.core;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import org.zollty.dbk.support.rowset.ResultSetWrappingSqlRowSet;
import org.zollty.dbk.support.rowset.SqlRowSet;
import org.zollty.dbk.util.ReflectionUtils;

import com.sun.rowset.CachedRowSetImpl;

/**
 * @author zollty 
 * @since 2013-12-8
 */
public class SqlRowSetResultSetExtractor implements ResultSetExtractor<SqlRowSet> {

    private static Object rowSetFactory = null;

    private static Method createCachedRowSet = null;

    static {
        ClassLoader cl = SqlRowSetResultSetExtractor.class.getClassLoader();
        try {
            Class rowSetProviderClass = cl.loadClass("javax.sql.rowset.RowSetProvider");
            Method newFactory = rowSetProviderClass.getMethod("newFactory");
            rowSetFactory = ReflectionUtils.invokeMethod(newFactory, null);
            createCachedRowSet = rowSetFactory.getClass().getMethod("createCachedRowSet");
        }
        catch (Exception ex) {
            // JDBC 4.1 API not available - fall back to Sun CachedRowSetImpl
        }
    }


    public SqlRowSet extractData(ResultSet rs) throws SQLException {
        return createSqlRowSet(rs);
    }

    /**
     * Create a SqlRowSet that wraps the given ResultSet,
     * representing its data in a disconnected fashion.
     * <p>This implementation creates a Spring ResultSetWrappingSqlRowSet
     * instance that wraps a standard JDBC CachedRowSet instance.
     * Can be overridden to use a different implementation.
     * @param rs the original ResultSet (connected)
     * @return the disconnected SqlRowSet
     * @throws SQLException if thrown by JDBC methods
     * @see #newCachedRowSet
     * @see org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet
     */
    protected SqlRowSet createSqlRowSet(ResultSet rs) throws SQLException {
        CachedRowSet rowSet = newCachedRowSet();
        rowSet.populate(rs);
        return new ResultSetWrappingSqlRowSet(rowSet);
    }

    /**
     * Create a new CachedRowSet instance, to be populated by
     * the <code>createSqlRowSet</code> implementation.
     * <p>The default implementation creates a new instance of
     * Sun's <code>com.sun.rowset.CachedRowSetImpl</code> class.
     * @return a new CachedRowSet instance
     * @throws SQLException if thrown by JDBC methods
     * @see #createSqlRowSet
     * @see com.sun.rowset.CachedRowSetImpl
     */
    protected CachedRowSet newCachedRowSet() throws SQLException {
        if (createCachedRowSet != null) {
            // RowSetProvider.newFactory().createCachedRowSet();
            return (CachedRowSet) ReflectionUtils.invokeJdbcMethod(createCachedRowSet, rowSetFactory);
        }
        else {
            return new CachedRowSetImpl();
        }
    }

}
