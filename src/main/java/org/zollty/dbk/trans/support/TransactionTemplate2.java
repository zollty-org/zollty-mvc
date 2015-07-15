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

package org.zollty.dbk.trans.support;

import java.lang.reflect.UndeclaredThrowableException;

import org.zollty.log.Logger;
import org.zollty.log.LogFactory;
import org.zollty.framework.core.BeanFactoryHelper;
import org.zollty.dbk.temp.beans.factory.InitializingBean;
import org.zollty.dbk.trans.PlatformTransactionManager;
import org.zollty.dbk.trans.TransactionDefinition;
import org.zollty.dbk.trans.TransactionException;
import org.zollty.dbk.trans.TransactionStatus;
import org.zollty.dbk.trans.TransactionSystemException;

/**
 * Template class that simplifies programmatic transaction demarcation and
 * transaction exception handling.
 *
 * <p>The central method is {@link #execute}, supporting transactional code that
 * implements the {@link TransactionCallback} interface. This template handles
 * the transaction lifecycle and possible exceptions such that neither the
 * TransactionCallback implementation nor the calling code needs to explicitly
 * handle transactions.
 *
 * <p>Typical usage: Allows for writing low-level data access objects that use
 * resources such as JDBC DataSources but are not transaction-aware themselves.
 * Instead, they can implicitly participate in transactions handled by higher-level
 * application services utilizing this class, making calls to the low-level
 * services via an inner-class callback object.
 *
 * <p>Can be used within a service implementation via direct instantiation with
 * a transaction manager reference, or get prepared in an application context
 * and passed to services as bean reference. Note: The transaction manager should
 * always be configured as bean in the application context: in the first case given
 * to the service directly, in the second case given to the prepared template.
 *
 * <p>Supports setting the propagation behavior and the isolation level by name,
 * for convenient configuration in context definitions.
 *
 * @author Juergen Hoeller
 * @since 17.03.2003
 * @see #execute
 * @see #setTransactionManager
 * @see org.zollty.dbk.trans.PlatformTransactionManager
 */
@SuppressWarnings("serial")
public class TransactionTemplate2 extends DefaultTransactionDefinition
		implements TransactionOperations, InitializingBean {

	/** Logger available to subclasses */
	protected final Logger logger = LogFactory.getLogger(getClass());

	private PlatformTransactionManager transactionManager;
	
	private TransactionDefinition transactionDefinition;
	
	private TransactionCallback tcwr;


	/**
	 * Construct a new TransactionTemplate for bean usage.
	 * <p>Note: The PlatformTransactionManager needs to be set before
	 * any {@code execute} calls.
	 * @see #setTransactionManager
	 */
	public TransactionTemplate2() {
	}

	/**
	 * Construct a new TransactionTemplate using the given transaction manager.
	 * @param transactionManager the transaction management strategy to be used
	 */
	public TransactionTemplate2(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

//	/**
//	 * Construct a new TransactionTemplate using the given transaction manager,
//	 * taking its default settings from the given transaction definition.
//	 * @param transactionManager the transaction management strategy to be used
//	 * @param transactionDefinition the transaction definition to copy the
//	 * default settings from. Local properties can still be set to change values.
//	 */
//	public TransactionTemplate2(PlatformTransactionManager transactionManager) {
//		super(transactionDefinition);
//		this.transactionManager = transactionManager;
//	}


	/**
	 * Set the transaction management strategy to be used.
	 */
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * Return the transaction management strategy to be used.
	 */
	public PlatformTransactionManager getTransactionManager() {
		if (this.transactionManager == null) {
			this.transactionManager = BeanFactoryHelper.getBeanFactory().getBean("transactionManager");
			afterPropertiesSet();
		}
		return this.transactionManager;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.transactionManager == null) {
			throw new IllegalArgumentException("Property 'transactionManager' is required");
		}
	}


	//@Override
	public <T> T execute2() throws TransactionException {
		TransactionStatus status = getTransactionManager().getTransaction(this);
		T result;
		try {
			result = (T) tcwr.doInTransaction(status);
		}
		catch (RuntimeException ex) {
			// Transactional code threw application exception -> rollback
			rollbackOnException(status, ex);
			throw ex;
		}
		catch (Error err) {
			// Transactional code threw error -> rollback
			rollbackOnException(status, err);
			throw err;
		}
		catch (Exception ex) {
			// Transactional code threw unexpected exception -> rollback
			rollbackOnException(status, ex);
			throw new UndeclaredThrowableException(ex, "TransactionCallback threw undeclared checked exception");
		}
		getTransactionManager().commit(status);
		return result;
	}

	/**
	 * Perform a rollback, handling rollback exceptions properly.
	 * @param status object representing the transaction
	 * @param ex the thrown application exception or error
	 * @throws TransactionException in case of a rollback error
	 */
	private void rollbackOnException(TransactionStatus status, Throwable ex) throws TransactionException {
		logger.debug("Initiating transaction rollback on application exception", ex);
		try {
			this.transactionManager.rollback(status);
		}
		catch (TransactionSystemException ex2) {
			logger.error("Application exception overridden by rollback exception", ex);
			ex2.initApplicationException(ex);
			throw ex2;
		}
		catch (RuntimeException ex2) {
			logger.error("Application exception overridden by rollback exception", ex);
			throw ex2;
		}
		catch (Error err) {
			logger.error("Application exception overridden by rollback error", ex);
			throw err;
		}
	}

	public TransactionCallback getTcwr() {
		return tcwr;
	}

	public void setTcwr(TransactionCallback tcwr) {
		this.tcwr = tcwr;
	}

	@Override
	public <T> T execute(TransactionCallback<T> action)
			throws TransactionException {
		TransactionStatus status = getTransactionManager().getTransaction(this);
		T result;
		try {
			result = action.doInTransaction(status);
		}
		catch (RuntimeException ex) {
			// Transactional code threw application exception -> rollback
			rollbackOnException(status, ex);
			throw ex;
		}
		catch (Error err) {
			// Transactional code threw error -> rollback
			rollbackOnException(status, err);
			throw err;
		}
		catch (Exception ex) {
			// Transactional code threw unexpected exception -> rollback
			rollbackOnException(status, ex);
			throw new UndeclaredThrowableException(ex, "TransactionCallback threw undeclared checked exception");
		}
		getTransactionManager().commit(status);
		return result;
	}

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition;
	}

	public void setTransactionDefinition(TransactionDefinition transactionDefinition) {
		this.transactionDefinition = transactionDefinition;
		this.setPropagationBehavior( transactionDefinition.getPropagationBehavior() );
		this.setIsolationLevel ( transactionDefinition.getIsolationLevel() );
		this.setTimeout ( transactionDefinition.getTimeout() );
		this.setReadOnly ( transactionDefinition.isReadOnly() );
		this.setName ( transactionDefinition.getName() );
	}

}