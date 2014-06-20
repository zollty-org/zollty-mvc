/* @(#)HandlerMapping.java 
 * Copyright (C) 2013-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by zollty on 2013-9-15 [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.handler;

import java.util.Set;

import org.zollty.framework.mvc.handler.support.HandlerChainImpl;

/**
 * @author zollty 
 * @since 2013-9-15
 */
public interface HandlerMapping {

	HandlerChainImpl match(String servletURI);
	
    /**
     * @return the excludeSuffix
     */
    public Set<String> getExcludeSuffixes();

    /**
     * @return the excludeprefix
     */
    public Set<String> getExcludePrefixes();
}
