/* 
 * Copyright (C) 2012-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Zollty Framework MVC Source Code - Since v1.0
 * Author(s): 
 * Zollty Tsou (zolltytsou@gmail.com, http://blog.zollty.com)
 */
package org.zollty.framework.core.beans.annotation;

import org.zollty.framework.core.annotation.Inject;

/**
 * @author zollty
 * @since 2014-1-4
 */
public class SubClass extends RootClass {

    @Inject
    private Object obj;

    public Object getObj() {
        return obj;
    }

    @Inject
    public void setObj(Object obj) {
        this.obj = obj;
    }

}
