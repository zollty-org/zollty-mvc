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
 * Create by ZollTy on 2015-7-23 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.aop;

import java.util.ArrayList;
import java.util.List;

import org.zollty.framework.mvc.aop.bean.MvcAfterBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAfterThrowBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcAroundBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeBeanDefinition;
import org.zollty.framework.mvc.aop.bean.MvcBeforeRenderBeanDefinition;

/**
 * 
 * @author zollty
 * @since 2015-7-23
 */
public class ControllerMethodAopMeta {

    private List<MvcBeforeBeanDefinition> beforeIntercList = new ArrayList<MvcBeforeBeanDefinition>();

    private List<MvcAroundBeanDefinition> aroundIntercList = new ArrayList<MvcAroundBeanDefinition>();

    private List<MvcBeforeRenderBeanDefinition> beforeRenderIntercList = new ArrayList<MvcBeforeRenderBeanDefinition>();

    private List<MvcAfterBeanDefinition> afterIntercList = new ArrayList<MvcAfterBeanDefinition>();

    private List<MvcAfterThrowBeanDefinition> afterThrowIntercList = new ArrayList<MvcAfterThrowBeanDefinition>();

    public List<MvcBeforeBeanDefinition> getBeforeIntercList() {
        return beforeIntercList;
    }

    public void setBeforeIntercList(List<MvcBeforeBeanDefinition> beforeIntercList) {
        this.beforeIntercList = beforeIntercList;
    }

    public List<MvcAroundBeanDefinition> getAroundIntercList() {
        return aroundIntercList;
    }

    public void setAroundIntercList(List<MvcAroundBeanDefinition> aroundIntercList) {
        this.aroundIntercList = aroundIntercList;
    }

    public List<MvcBeforeRenderBeanDefinition> getBeforeRenderIntercList() {
        return beforeRenderIntercList;
    }

    public void setBeforeRenderIntercList(List<MvcBeforeRenderBeanDefinition> beforeRenderIntercList) {
        this.beforeRenderIntercList = beforeRenderIntercList;
    }

    public List<MvcAfterBeanDefinition> getAfterIntercList() {
        return afterIntercList;
    }

    public void setAfterIntercList(List<MvcAfterBeanDefinition> afterIntercList) {
        this.afterIntercList = afterIntercList;
    }

    public List<MvcAfterThrowBeanDefinition> getAfterThrowIntercList() {
        return afterThrowIntercList;
    }

    public void setAfterThrowIntercList(
            List<MvcAfterThrowBeanDefinition> afterThrowIntercList) {
        this.afterThrowIntercList = afterThrowIntercList;
    }

}