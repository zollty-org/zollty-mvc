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
 * Create by ZollTy on 2013-9-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.beans;

import org.jretty.util.BasicRuntimeException;

public class BeansException extends BasicRuntimeException {
    
    private static final long serialVersionUID = -5356594713002414317L;

    public BeansException() {
        super();
    }

    public BeansException(String message, String... args) {
        super(message, args);
    }

    public BeansException(Throwable e, String message, String... args) {
        super(e, message, args);
    }

    public BeansException(Throwable e) {
        super(e);
    }

}
