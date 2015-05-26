/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zollty.dbk.support;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import org.zollty.dbk.temp.beans.BeansException;
import org.zollty.dbk.util.PatternMatchUtils;
import org.zollty.framework.core.beans.ListableBeanFactory;
import org.zollty.framework.core.beans.support.SimpleBeanFactory;
import org.zollty.framework.core.support.xml.XmlBeanReader;
import org.zollty.framework.util.Assert;
import org.zollty.framework.util.ResourcContext;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
/**
 * Factory for creating {@link SQLErrorCodes} based on the
 * "databaseProductName" taken from the {@link java.sql.DatabaseMetaData}.
 *
 * <p>Returns {@code SQLErrorCodes} populated with vendor codes
 * defined in a configuration file named "sql-error-codes.xml".
 * Reads the default file in this package if not overridden by a file in
 * the root of the class path (for example in the "/WEB-INF/classes" directory).
 *
 * @author Thomas Risberg
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see java.sql.DatabaseMetaData#getDatabaseProductName()
 */
public class SQLErrorCodesFactory {

    /**
     * The name of custom SQL error codes file, loading from the root
     * of the class path (e.g. from the "/WEB-INF/classes" directory).
     */
//    public static final String SQL_ERROR_CODE_OVERRIDE_PATH = "sql-error-codes.xml";

    /**
     * The name of default SQL error code files, loading from the class path.
     */
    public static final String SQL_ERROR_CODE_DEFAULT_PATH = "classpath:org/zollty/dbk/support/sql-error-codes.xml";


    private static final Logger logger = LogFactory.getLogger(SQLErrorCodesFactory.class);

    /**
     * Keep track of a single instance so we can return it to classes that request it.
     */
    private static final SQLErrorCodesFactory instance = new SQLErrorCodesFactory();


    /**
     * Return the singleton instance.
     */
    public static SQLErrorCodesFactory getInstance() {
        return instance;
    }


    /**
     * Map to hold error codes for all databases defined in the config file.
     * Key is the database product name, value is the SQLErrorCodes instance.
     */
    private final Map<String, SQLErrorCodes> errorCodesMap;

    /**
     * Map to cache the SQLErrorCodes instance per DataSource.
     */
    private final Map<DataSource, SQLErrorCodes> dataSourceCache = new WeakHashMap<DataSource, SQLErrorCodes>(16);


    /**
     * Create a new instance of the {@link SQLErrorCodesFactory} class.
     * <p>Not public to enforce Singleton design pattern. Would be private
     * except to allow testing via overriding the
     * {@link #loadResource(String)} method.
     * <p><b>Do not subclass in application code.</b>
     * @see #loadResource(String)
     */
    protected SQLErrorCodesFactory() {
        Map<String, SQLErrorCodes> errorCodes;

        try {
            ResourcContext resourcContext = new ResourcContext(SQL_ERROR_CODE_DEFAULT_PATH, getClass().getClassLoader());
            SimpleBeanFactory sbf = new SimpleBeanFactory(new XmlBeanReader(resourcContext));
            // Check all beans of type SQLErrorCodes.
            errorCodes = ((ListableBeanFactory) sbf ).getBeansOfType(SQLErrorCodes.class);
            if (logger.isInfoEnabled()) {
                logger.info("SQLErrorCodes loaded: " + errorCodes.keySet());
            }
        }
        catch (BeansException ex) {
            logger.warn("Error loading SQL error codes from config file", ex);
            errorCodes = Collections.emptyMap();
        }

        this.errorCodesMap = errorCodes;
    }

    /**
     * Return the {@link SQLErrorCodes} instance for the given database.
     * <p>No need for a database metadata lookup.
     * @param dbName the database name (must not be {@code null})
     * @return the {@code SQLErrorCodes} instance for the given database
     * @throws IllegalArgumentException if the supplied database name is {@code null}
     */
    public SQLErrorCodes getErrorCodes(String dbName) {
        Assert.notNull(dbName, "Database product name must not be null");

        SQLErrorCodes sec = this.errorCodesMap.get(dbName);
        if (sec == null) {
            for (SQLErrorCodes candidate : this.errorCodesMap.values()) {
                if (PatternMatchUtils.simpleMatch(candidate.getDatabaseProductNames(), dbName)) {
                    sec = candidate;
                    break;
                }
            }
        }
        if (sec != null) {
            checkCustomTranslatorRegistry(dbName, sec);
            if (logger.isDebugEnabled()) {
                logger.debug("SQL error codes for '" + dbName + "' found");
            }
            return sec;
        }

        // Could not find the database among the defined ones.
        if (logger.isDebugEnabled()) {
            logger.debug("SQL error codes for '" + dbName + "' not found");
        }
        return new SQLErrorCodes();
    }

    /**
     * Return {@link SQLErrorCodes} for the given {@link DataSource},
     * evaluating "databaseProductName" from the
     * {@link java.sql.DatabaseMetaData}, or an empty error codes
     * instance if no {@code SQLErrorCodes} were found.
     * @param dataSource the {@code DataSource} identifying the database
     * @return the corresponding {@code SQLErrorCodes} object
     * @see java.sql.DatabaseMetaData#getDatabaseProductName()
     */
    public SQLErrorCodes getErrorCodes(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Looking up default SQLErrorCodes for DataSource [" + dataSource + "]");
        }

        synchronized (this.dataSourceCache) {
            // Let's avoid looking up database product info if we can.
            SQLErrorCodes sec = this.dataSourceCache.get(dataSource);
            if (sec != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SQLErrorCodes found in cache for DataSource [" +
                            dataSource.getClass().getName() + '@' + Integer.toHexString(dataSource.hashCode()) + "]");
                }
                return sec;
            }
            // We could not find it - got to look it up.
            try {
                String dbName = (String) JdbcUtils.extractDatabaseMetaData(dataSource, "getDatabaseProductName");
                if (dbName != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Database product name cached for DataSource [" +
                                dataSource.getClass().getName() + '@' + Integer.toHexString(dataSource.hashCode()) +
                                "]: name is '" + dbName + "'");
                    }
                    sec = getErrorCodes(dbName);
                    this.dataSourceCache.put(dataSource, sec);
                    return sec;
                }
            }
            catch (MetaDataAccessException ex) {
                logger.warn("Error while extracting database product name - falling back to empty error codes", ex);
            }
        }

        // Fallback is to return an empty SQLErrorCodes instance.
        return new SQLErrorCodes();
    }

    /**
     * Associate the specified database name with the given {@link DataSource}.
     * @param dataSource the {@code DataSource} identifying the database
     * @param dbName the corresponding database name as stated in the error codes
     * definition file (must not be {@code null})
     * @return the corresponding {@code SQLErrorCodes} object
     */
    public SQLErrorCodes registerDatabase(DataSource dataSource, String dbName) {
        synchronized (this.dataSourceCache) {
            SQLErrorCodes sec = getErrorCodes(dbName);
            this.dataSourceCache.put(dataSource, sec);
            return sec;
        }
    }

    /**
     * Check the {@link CustomSQLExceptionTranslatorRegistry} for any entries.
     */
    private void checkCustomTranslatorRegistry(String dbName, SQLErrorCodes dbCodes) {
        SQLExceptionTranslator customTranslator =
                CustomSQLExceptionTranslatorRegistry.getInstance().findTranslatorForDatabase(dbName);
        if (customTranslator != null) {
            if (dbCodes.getCustomSqlExceptionTranslator() != null) {
                logger.warn("Overriding already defined custom translator '" +
                        dbCodes.getCustomSqlExceptionTranslator().getClass().getSimpleName() +
                        " with '" + customTranslator.getClass().getSimpleName() +
                        "' found in the CustomSQLExceptionTranslatorRegistry for database " + dbName);
            }
            else {
                logger.info("Using custom translator '" + customTranslator.getClass().getSimpleName() +
                        "' found in the CustomSQLExceptionTranslatorRegistry for database " + dbName);
            }
            dbCodes.setCustomSqlExceptionTranslator(customTranslator);
        }
    }

}