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
 * Create by ZollTy on 2013-10-14 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.ext;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.framework.ext.jdbc.JndiTemplate;

/**
 * @author zollty
 * @since 2013-10-14
 */
public class JndiDataSourceCreator {

    public DataSource getDataSource() {
        
        Logger log = LogFactory.getLogger(JndiDataSourceCreator.class);

        JndiTemplate jndiTemplate = new JndiTemplate();
        try {
            return jndiTemplate.lookup(jndiName, javax.sql.DataSource.class);
        }
        catch (NamingException e) {
            log.error(e, "get jndi datasource error, jndiName=" + jndiName);
        }
        return null;
    }

    private String jndiName;

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }
}