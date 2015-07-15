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
 * Create by ZollTy on 2013-9-16 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.handler;

/**
 * 供注入到实例的方法参数中
 * 
 * @author zollty
 * @since 2013-9-16
 */
public class InterceptorInfo {

    private String servletURI;

    public InterceptorInfo() {
    }

    /**
     * @param servletURI
     */
    public InterceptorInfo(String servletURI) {
        super();
        this.servletURI = servletURI;
    }

    public String getServletURI() {
        return servletURI;
    }

    public void setServletURI(String servletURI) {
        this.servletURI = servletURI;
    }
}