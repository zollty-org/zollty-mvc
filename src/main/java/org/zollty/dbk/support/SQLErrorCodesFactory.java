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

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import org.zollty.dbk.util.PatternMatchUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.Assert;
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
//    public static final String SQL_ERROR_CODE_DEFAULT_PATH = "classpath:org/zollty/dbk/support/sql-error-codes.xml";


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

//        try {
//            ResourceContext resourcContext = new ResourceContext(getClass().getClassLoader(), SQL_ERROR_CODE_DEFAULT_PATH);
//            SimpleBeanFactory sbf = new SimpleBeanFactory(new XmlBeanReader(resourcContext));
//            // Check all beans of type SQLErrorCodes.
//            errorCodes = ((ListableBeanFactory) sbf ).getBeansOfType(SQLErrorCodes.class);
//            if (logger.isInfoEnabled()) {
//                logger.info("SQLErrorCodes loaded: " + errorCodes.keySet());
//            }
//        }
//        catch (BeansException ex) {
//            logger.warn("Error loading SQL error codes from config file", ex);
//            errorCodes = Collections.emptyMap();
//        }
        
        errorCodes = new HashMap<String, SQLErrorCodes>(15);
        
        SQLErrorCodes db2 = new SQLErrorCodes();
        db2.setDatabaseProductName("DB2*");
        db2.setBadSqlGrammarCodes("-007,-029,-097,-104,-109,-115,-128,-199,-204,-206,-301,-408,-441,-491");
        db2.setDuplicateKeyCodes("-803");
        db2.setDataIntegrityViolationCodes("-407,-530,-531,-532,-543,-544,-545,-603,-667");
        db2.setDataAccessResourceFailureCodes("-904,-971");
        db2.setTransientDataAccessResourceCodes("-1035,-1218,-30080,-30081");
        db2.setDeadlockLoserCodes("-911,-913");
        
        errorCodes.put("DB2", db2);
        
        SQLErrorCodes derby = new SQLErrorCodes();
        derby.setDatabaseProductName("Apache Derby");
        derby.setUseSqlStateForTranslation(true);
        derby.setBadSqlGrammarCodes("42802,42821,42X01,42X02,42X03,42X04,42X05,42X06,42X07,42X08");
        derby.setDuplicateKeyCodes("23505");
        derby.setDataIntegrityViolationCodes("22001,22005,23502,23503,23513,X0Y32");
        derby.setDataAccessResourceFailureCodes("04501,08004,42Y07");
        derby.setCannotAcquireLockCodes("40XL1");
        derby.setDeadlockLoserCodes("40001");
        
        errorCodes.put("Derby", derby);
        
        SQLErrorCodes h2 = new SQLErrorCodes();
        h2.setBadSqlGrammarCodes("42000,42001,42101,42102,42111,42112,42121,42122,42132");
        h2.setDuplicateKeyCodes("23001,23505");
        h2.setDataIntegrityViolationCodes("22001,22003,22012,22018,22025,23000,23002,23003,23502,23503,23506,23507,23513");
        h2.setDataAccessResourceFailureCodes("90046,90100,90117,90121,90126");
        h2.setCannotAcquireLockCodes("50200");
        
        errorCodes.put("H2", h2);
        
        SQLErrorCodes hsql = new SQLErrorCodes();
        hsql.setDatabaseProductName("HSQL Database Engine");
        hsql.setBadSqlGrammarCodes("-22,-28");
        hsql.setDuplicateKeyCodes("-104");
        hsql.setDataIntegrityViolationCodes("-9");
        hsql.setDataAccessResourceFailureCodes("-80");
        
        errorCodes.put("HSQL", hsql);
        
        SQLErrorCodes informix = new SQLErrorCodes();
        informix.setDatabaseProductName("Informix Dynamic Server");
        informix.setBadSqlGrammarCodes("-201,-217,-696");
        informix.setDuplicateKeyCodes("-239,-268,-6017");
        informix.setDataIntegrityViolationCodes("-692,-11030");
        
        errorCodes.put("Informix", informix);
        
        SQLErrorCodes mssql = new SQLErrorCodes();
        mssql.setDatabaseProductName("Microsoft SQL Server");
        mssql.setBadSqlGrammarCodes("156,170,207,208,209");
        mssql.setPermissionDeniedCodes("229");
        mssql.setDuplicateKeyCodes("2601,2627");
        mssql.setDataIntegrityViolationCodes("544,8114,8115");
        mssql.setDataAccessResourceFailureCodes("4060");
        mssql.setCannotAcquireLockCodes("1222");
        mssql.setDeadlockLoserCodes("1205");
        
        errorCodes.put("MS-SQL", mssql);
        
        SQLErrorCodes mysql = new SQLErrorCodes();
        mysql.setBadSqlGrammarCodes("1054,1064,1146");
        mysql.setDuplicateKeyCodes("1062");
        mysql.setDataIntegrityViolationCodes("630,839,840,893,1169,1215,1216,1217,1364,1451,1452,1557");
        mysql.setDataAccessResourceFailureCodes("1");
        mysql.setCannotAcquireLockCodes("1205");
        mysql.setDeadlockLoserCodes("1213");
        
        errorCodes.put("MySQL", mysql);
        
        SQLErrorCodes oracle = new SQLErrorCodes();
        oracle.setBadSqlGrammarCodes("900,903,904,917,936,942,17006,6550");
        oracle.setInvalidResultSetAccessCodes("17003");
        oracle.setDuplicateKeyCodes("1");
        oracle.setDataIntegrityViolationCodes("1400,1722,2291,2292");
        oracle.setDataAccessResourceFailureCodes("17002,17447");
        oracle.setCannotAcquireLockCodes("54,30006");
        oracle.setCannotSerializeTransactionCodes("8177");
        oracle.setDeadlockLoserCodes("60");
        
        errorCodes.put("Oracle", oracle);
        
        SQLErrorCodes postgreSQL = new SQLErrorCodes();
        postgreSQL.setUseSqlStateForTranslation(true);
        postgreSQL.setBadSqlGrammarCodes("03000,42000,42601,42602,42622,42804,42P01");
        postgreSQL.setDuplicateKeyCodes("23505");
        postgreSQL.setDataIntegrityViolationCodes("23000,23502,23503,23514");
        postgreSQL.setDataAccessResourceFailureCodes("53000,53100,53200,53300");
        postgreSQL.setCannotAcquireLockCodes("55P03");
        postgreSQL.setCannotSerializeTransactionCodes("40001");
        postgreSQL.setDeadlockLoserCodes("40P01");
        
        errorCodes.put("PostgreSQL", postgreSQL);
        
        SQLErrorCodes sybase = new SQLErrorCodes();
        sybase.setDatabaseProductNames(new String[]{"Sybase SQL Server","SQL Server", "Adaptive Server Enterprise", 
                "ASE", //<!-- name as returned by jTDS driver -->
                "sql server" //<!-- name as returned by jTDS driver -->
                });
        sybase.setBadSqlGrammarCodes("101,102,103,104,105,106,107,108,109,110,111,112,113,116,120,121,123,207,208,213,257,512");
        sybase.setDuplicateKeyCodes("2601,2615,2626");
        sybase.setDataIntegrityViolationCodes("233,511,515,530,546,547,2615,2714");
        sybase.setTransientDataAccessResourceCodes("921,1105");
        sybase.setCannotAcquireLockCodes("12205");
        sybase.setDeadlockLoserCodes("1205");
        
        errorCodes.put("Sybase", sybase);

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