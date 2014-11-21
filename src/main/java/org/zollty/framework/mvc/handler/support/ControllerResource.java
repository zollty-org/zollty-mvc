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
 * Zollty Framework MVC Source Code - Since v1.1
 * Author(s): 
 * Zollty Tsou (zolltytsou@gmail.com, http://blog.zollty.com)
 */
package org.zollty.framework.mvc.handler.support;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.support.ControllerMetaInfo;
import org.zollty.framework.util.MvcRuntimeException;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * @author zollty
 * @since 2014-5-29
 */
public class ControllerResource {
    
    private static final Logger LOG = LogFactory.getLogger(ControllerResource.class);

    protected final List<ControllerHandlerPattern> patternControllerList = new ArrayList<ControllerHandlerPattern>();
    protected final List<ControllerHandler> simpleControllerList = new ArrayList<ControllerHandler>();

    public void addController(ControllerMetaInfo controller) {
        String uri = controller.getServletURI();
        // char last = uri.charAt(uri.length() - 1);
        // if (last != '/') {
        // uri += "/";
        // }
        List<String> list = parseUriPathVariable(uri);
        if (list.size() == 0) {
            if(this.isDuplicate(uri, controller.getAllowHttpMethods())) {
                throw new MvcRuntimeException("the controller definition is a duplicate!");
            }
            simpleControllerList.add(new ControllerHandler(controller));
        }
        else {
            ControllerHandlerPattern chp = new ControllerHandlerPattern(controller, list);
            if(this.isDuplicate(chp)) {
                throw new MvcRuntimeException("the controller definition is a duplicate!");
            }
            patternControllerList.add(chp);
        }
    }

    /**
     * 针对普通URI（即非pattern模糊匹配的uri），判断 uri+allowHttpMethods 是否有重复(会冲突)的定义
     * <p>
     * 注意： uri里的斜杠也算作一个有效字符
     */
    protected boolean isDuplicate(String uri, String[] allowHttpMethods) {
        // 当已存在相同的普通URI时，则返回true，代表不允许有两个一模一样的普通URI的定义
        for (ControllerHandler ch : simpleControllerList) {
            ControllerMetaInfo ctrl = ch.getController();
            if (uri.equals(ctrl.getServletURI())) {
                for (String me : allowHttpMethods) {
                    for (String mi : ctrl.getAllowHttpMethods()) {
                        if (me.equals(mi)) {
                            LOG.error("'{}' is a duplicate of '{}'", uri, ctrl.getServletURI());
                            return true;
                        }
                    }
                }
            }
        }
        
        // 检测 如果有已定义的 ControllerHandlerPattern 包含了当前的普通ControllerHandler定义，则给出WARN级别的提示。
        for (ControllerHandlerPattern chp : patternControllerList) {
            for (String me : allowHttpMethods) {
                if(chp.getController().allowMethod(me)){
                    if(chp.getHandler(uri, me)!=null)
                        LOG.warn("'{}' is included by '{}'", uri, chp.getController().getServletURI());
                }
            }
        }
        return false;
    }
    
    /**
     * 针对pattern模糊匹配的URI，判断 uri+allowHttpMethods 是否有重复(会冲突)的定义
     * <p>
     * 注意： uri里的斜杠也算作一个有效字符
     */
    protected boolean isDuplicate(ControllerHandlerPattern chp) {
        
        // 以下两个for循环，第一个是检测当前的ControllerHandlerPattern（CHP）是否被其他已经定义了的CHP所包含
        // 比如 已存在了/app/*/*，那么当前如果是 /app/user/*，则是冲突的
        for (ControllerHandlerPattern chpa : patternControllerList) {
            for (String me : chp.getController().getAllowHttpMethods()) {
                if(chpa.getController().allowMethod(me)){
                    if(chpa.getHandler(chp.getPatternStr().replace('*', 'a'), me)!=null){
                        LOG.error("'{}' is a duplicate of '{}'", chp.getController(), chpa.getController());
                        return true;
                    }
                    break;
                }
            }
        }
        // 第二个for循环是为了检测当前的ControllerHandlerPattern是否可以包含其他已定义了的CHP
        // 比如 已存在了/app/user/*，那么当前如果是 /app/*/*，则是冲突的
        for (ControllerHandlerPattern chpa : patternControllerList) {
            for (String me : chpa.getController().getAllowHttpMethods()) {
                if(chp.getController().allowMethod(me)){
                    if(chp.getHandler(chpa.getPatternStr().replace('*', 'a'), me)!=null){
                        LOG.error("'{}' is a duplicate of '{}'", chp.getController(), chpa.getController());
                        return true;
                    }
                    break;
                }
            }
        }
        
        // 检测 如果有已定义的 普通ControllerHandler 被当前的ControllerHandlerPattern所匹配到，则给出WARN级别的提示。
        for (ControllerHandler ch : simpleControllerList) {
            ControllerMetaInfo ctrl = ch.getController();
            for (String me : ctrl.getAllowHttpMethods()) {
                if (chp.getController().allowMethod(me)) {
                    if (chp.getHandler(ctrl.getServletURI(), me) != null) {
                        LOG.warn("'{}' is included by '{}'", ctrl.getServletURI(), chp.getController().getServletURI());
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * 注意： servletURI里的斜杠也算作一个有效字符
     */
    public WebHandler getHandler(String servletURI, HttpServletRequest request) {
        if (servletURI == null) {
            return null;
        }
        // char last = servletURI.charAt(servletURI.length() - 1);
        // String newURI = servletURI;
        // if (last != '/') {
        // newURI += "/";
        // }
        // step 1 首先从普通URI Controller定义中去寻找
        for (final ControllerHandler ch : simpleControllerList) {
            ControllerMetaInfo controller = ch.getController();
            if (controller.getServletURI().equals(servletURI) && controller.allowMethod(request.getMethod())) {
                return ch;
            }
        }
        // step 2
        for (final ControllerHandlerPattern chp : patternControllerList) {
            WebHandler ret = chp.getHandler(servletURI, request);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    protected static List<String> parseUriPathVariable(String uri) {
        char[] chars = uri.toCharArray();
        int a = -1, b = -1;
        int c = -1, d = -1;
        String temp;
        List<String> params = new ArrayList<String>();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '{') {
                if (a == -1) {
                    a = i;
                }
            }
            else if (chars[i] == '}') {
                if (a != -1) {
                    b = i;
                    temp = uri.substring(a + 1, b);
                    if (temp.indexOf("[") != -1 || temp.indexOf("]") != -1 || temp.indexOf("{") != -1
                            || temp.indexOf("}") != -1) {
                        throw new IllegalArgumentException(
                                "uri path variable error, can't include those symbols: {, [, }, ]. URI=" + uri);
                    }
                    params.add(temp);
                    a = -1;
                }
            }
            else if (chars[i] == '[') {
                if (c == -1) {
                    c = i;
                }
            }
            else if (chars[i] == ']') {
                if (c != -1) {
                    d = i;
                    temp = uri.substring(c + 1, d);
                    if (temp.indexOf("[") != -1 || temp.indexOf("]") != -1 || temp.indexOf("{") != -1
                            || temp.indexOf("}") != -1) {
                        throw new IllegalArgumentException(
                                "uri path variable error, can't include those symbols: {, [, }, ]. URI=" + uri);
                    }
                    params.add(temp);
                    c = -1;
                }
            }
        }
        return params;
    }

}
