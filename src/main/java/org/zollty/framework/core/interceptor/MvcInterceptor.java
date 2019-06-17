/* 
 * Copyright (C) 2013-2018 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by ZollTy on 2018-9-26 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.interceptor;

/**
 * 拦截器基础接口
 * 
 * @author zollty
 * @since 2018年9月26日
 */
public interface MvcInterceptor {

    /**
     * 事件触发时执行的操作
     */
    public void onEnvent();
}
