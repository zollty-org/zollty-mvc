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

import org.zollty.dbk.temp.core.SpringUtils;

/**
 * JavaBean for holding JDBC error codes for a particular database.
 * Instances of this class are normally loaded through a bean factory.
 *
 * <p>Used by Spring's {@link SQLErrorCodeSQLExceptionTranslator}.
 * The file "sql-error-codes.xml" in this package contains default
 * {@code SQLErrorCodes} instances for various databases.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @see SQLErrorCodesFactory
 * @see SQLErrorCodeSQLExceptionTranslator
 */
public class SQLErrorCodes {

	private String[] databaseProductNames;

	private boolean useSqlStateForTranslation = false;

	private String[] badSqlGrammarCodes = new String[0];

	private String[] invalidResultSetAccessCodes = new String[0];

	private String[] duplicateKeyCodes = new String[0];

	private String[] dataIntegrityViolationCodes = new String[0];

	private String[] permissionDeniedCodes = new String[0];

	private String[] dataAccessResourceFailureCodes = new String[0];

	private String[] transientDataAccessResourceCodes = new String[0];

	private String[] cannotAcquireLockCodes = new String[0];

	private String[] deadlockLoserCodes = new String[0];

	private String[] cannotSerializeTransactionCodes = new String[0];

	private CustomSQLErrorCodesTranslation[] customTranslations;

	private SQLExceptionTranslator customSqlExceptionTranslator;


	/**
	 * Set this property if the database name contains spaces,
	 * in which case we can not use the bean name for lookup.
	 */
	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductNames = new String[] {databaseProductName};
	}

	public String getDatabaseProductName() {
		return (this.databaseProductNames != null && this.databaseProductNames.length > 0 ?
				this.databaseProductNames[0] : null);
	}

	/**
	 * Set this property to specify multiple database names that contains spaces,
	 * in which case we can not use bean names for lookup.
	 */
	public void setDatabaseProductNames(String[] databaseProductNames) {
		this.databaseProductNames = databaseProductNames;
	}

	public String[] getDatabaseProductNames() {
		return this.databaseProductNames;
	}

	/**
	 * Set this property to true for databases that do not provide an error code
	 * but that do provide SQL State (this includes PostgreSQL).
	 */
	public void setUseSqlStateForTranslation(boolean useStateCodeForTranslation) {
		this.useSqlStateForTranslation = useStateCodeForTranslation;
	}

	public boolean isUseSqlStateForTranslation() {
		return this.useSqlStateForTranslation;
	}

	public void setBadSqlGrammarCodes(String badSqlGrammarCodes) {// String[] 
		this.badSqlGrammarCodes = SpringUtils.sortStringArray(SpringUtils.split(badSqlGrammarCodes, ','));
	}

	public String[] getBadSqlGrammarCodes() {
		return this.badSqlGrammarCodes;
	}

	public void setInvalidResultSetAccessCodes(String invalidResultSetAccessCodes) {
		this.invalidResultSetAccessCodes = SpringUtils.sortStringArray(SpringUtils.split(invalidResultSetAccessCodes, ','));
	}

	public String[] getInvalidResultSetAccessCodes() {
		return this.invalidResultSetAccessCodes;
	}

	public String[] getDuplicateKeyCodes() {
		return duplicateKeyCodes;
	}

	public void setDuplicateKeyCodes(String duplicateKeyCodes) {
		this.duplicateKeyCodes = SpringUtils.split(duplicateKeyCodes, ',');
	}

	public void setDataIntegrityViolationCodes(String dataIntegrityViolationCodes) {
		this.dataIntegrityViolationCodes = SpringUtils.sortStringArray(SpringUtils.split(dataIntegrityViolationCodes, ','));
	}

	public String[] getDataIntegrityViolationCodes() {
		return this.dataIntegrityViolationCodes;
	}

	public void setPermissionDeniedCodes(String permissionDeniedCodes) {
		this.permissionDeniedCodes = SpringUtils.sortStringArray(SpringUtils.split(permissionDeniedCodes, ','));
	}

	public String[] getPermissionDeniedCodes() {
		return this.permissionDeniedCodes;
	}

	public void setDataAccessResourceFailureCodes(String dataAccessResourceFailureCodes) {
		this.dataAccessResourceFailureCodes = SpringUtils.sortStringArray(SpringUtils.split(dataAccessResourceFailureCodes, ','));
	}

	public String[] getDataAccessResourceFailureCodes() {
		return this.dataAccessResourceFailureCodes;
	}

	public void setTransientDataAccessResourceCodes(String transientDataAccessResourceCodes) {
		this.transientDataAccessResourceCodes = SpringUtils.sortStringArray(SpringUtils.split(transientDataAccessResourceCodes, ','));
	}

	public String[] getTransientDataAccessResourceCodes() {
		return this.transientDataAccessResourceCodes;
	}

	public void setCannotAcquireLockCodes(String cannotAcquireLockCodes) {
		this.cannotAcquireLockCodes = SpringUtils.sortStringArray(SpringUtils.split(cannotAcquireLockCodes, ','));
	}

	public String[] getCannotAcquireLockCodes() {
		return this.cannotAcquireLockCodes;
	}

	public void setDeadlockLoserCodes(String deadlockLoserCodes) {
		this.deadlockLoserCodes = SpringUtils.sortStringArray(SpringUtils.split(deadlockLoserCodes, ','));
	}

	public String[] getDeadlockLoserCodes() {
		return this.deadlockLoserCodes;
	}

	public void setCannotSerializeTransactionCodes(String cannotSerializeTransactionCodes) {
		this.cannotSerializeTransactionCodes = SpringUtils.sortStringArray(SpringUtils.split(cannotSerializeTransactionCodes, ','));
	}

	public String[] getCannotSerializeTransactionCodes() {
		return this.cannotSerializeTransactionCodes;
	}

	public void setCustomTranslations(CustomSQLErrorCodesTranslation[] customTranslations) {
		this.customTranslations = customTranslations;
	}

	public CustomSQLErrorCodesTranslation[] getCustomTranslations() {
		return this.customTranslations;
	}

	public void setCustomSqlExceptionTranslatorClass(Class<? extends SQLExceptionTranslator> customTranslatorClass) {
		if (customTranslatorClass != null) {
			try {
				this.customSqlExceptionTranslator = customTranslatorClass.newInstance();
			}
			catch (Exception ex) {
				throw new IllegalStateException("Unable to instantiate custom translator", ex);
			}
		}
		else {
			this.customSqlExceptionTranslator = null;
		}
	}

	public void setCustomSqlExceptionTranslator(SQLExceptionTranslator customSqlExceptionTranslator) {
		this.customSqlExceptionTranslator = customSqlExceptionTranslator;
	}

	public SQLExceptionTranslator getCustomSqlExceptionTranslator() {
		return this.customSqlExceptionTranslator;
	}
	
}
