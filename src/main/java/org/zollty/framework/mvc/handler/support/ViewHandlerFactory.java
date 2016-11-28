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
 * Create by ZollTy on 2014-6-29 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc.handler.support;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.zollty.framework.mvc.aop.bean.AopBeanDefinition;
import org.zollty.framework.mvc.handler.ControllerMeta;
import org.zollty.framework.mvc.handler.RequestViewHandler;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.BasicRuntimeException;
import org.jretty.util.match.ZolltyPathMatcher;

/**
 * @author zollty
 * @since 2014-5-29
 */
class ViewHandlerFactory {

    private static final Logger LOG = LogFactory.getLogger(ViewHandlerFactory.class);

    protected final List<ControllerHandlerPattern> patternControllerList = new ArrayList<ControllerHandlerPattern>();
    protected final List<RequestViewHandler> simpleControllerList = new ArrayList<RequestViewHandler>();

    protected final List<AopHandlerPattern> aopHandlerPatternList = new ArrayList<AopHandlerPattern>();

    /**
     * 注意： servletURI里的斜杠也算作一个有效字符
     */
    public RequestViewHandler getHandler(String servletURI, HttpServletRequest request) {
        if (servletURI == null) {
            return null;
        }

        List<AopBeanDefinition> tempAopIntercList = new ArrayList<AopBeanDefinition>();
        for (final AopHandlerPattern ahp : aopHandlerPatternList) {
            AopBeanDefinition ret = ahp.getHandler(servletURI);
            if (ret == null) {
                continue;
            }
            tempAopIntercList.add(ret);
        }

        RequestViewHandler vhandler = getHandlerInner(servletURI, request);

        if (vhandler != null) {
            vhandler.setAopIntercs(tempAopIntercList);
        }

        return vhandler;
    }

    private RequestViewHandler getHandlerInner(String servletURI, HttpServletRequest request) {
        // step 1 首先从普通URI Controller定义中去寻找
        for (final RequestViewHandler ch : simpleControllerList) {
            ControllerMeta meta = ch.getMeta();
            if (meta.getServletURI().equals(servletURI) && meta.allowMethod(request.getMethod())) {
                return ch;
            }
        }
        // step 2
        for (final ControllerHandlerPattern chp : patternControllerList) {
            RequestViewHandler ret = chp.getHandler(servletURI, request.getMethod());
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    public void setAopIntercList(List<? extends AopBeanDefinition> aopIntercList) {
        if (aopIntercList == null) {
            return;
        }
        for (AopBeanDefinition beanDef : aopIntercList) {
            aopHandlerPatternList.add(new AopHandlerPattern(beanDef));
        }
    }

    public void addControllerMeta(ControllerMeta meta) {
        String uri = meta.getServletURI();
        List<String> list = parseUriParams(uri);
        if (list.size() == 0) {
            if (this.isDuplicate(uri, meta.getAllowHttpMethods())) {
                throw new BasicRuntimeException("the controller definition is a duplicate!");
            }

            simpleControllerList.add(new RequestViewHandler(meta));
        }
        else {
            ControllerHandlerPattern chp = new ControllerHandlerPattern(meta, list);
            if (this.isDuplicate(chp)) {
                throw new BasicRuntimeException("the controller definition is a duplicate!");
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
        for (RequestViewHandler ch : simpleControllerList) {
            ControllerMeta meta = ch.getMeta();
            if (uri.equals(meta.getServletURI())) {
                for (String me : allowHttpMethods) {
                    for (String mi : meta.getAllowHttpMethods()) {
                        if (me.equals(mi)) {
                            LOG.error("'{}' is a duplicate of '{}'", uri, meta.getServletURI());
                            return true;
                        }
                    }
                }
            }
        }

        // 检测 如果有已定义的 ControllerHandlerPattern 包含了当前的普通ControllerHandler定义，则给出WARN级别的提示。
        for (ControllerHandlerPattern chp : patternControllerList) {
            for (String me : allowHttpMethods) {
                if (chp.getMeta().allowMethod(me)) {
                    if (chp.getHandler(uri, me) != null)
                        LOG.warn("'{}' is included by '{}'", uri, chp.getMeta().getServletURI());
                }
            }
        }
        return false;
    }

    /**
     * 针对pattern模糊匹配的URI，判断 uri+allowHttpMethods 是否有重复(会冲突)的定义
     * <p>
     * 注意： uri里的斜杠也算作一个有效字符。
     * <p>
     * 注意： 当两个Pattern存在交集时，该方法并不能全面检测出URI的重复匹配。
     * 
     * @see ZolltyPathMatcher.isTwoPatternSimilar()
     */
    protected boolean isDuplicate(ControllerHandlerPattern chp) {

        for (ControllerHandlerPattern chpa : patternControllerList) {
            if (isAllowMethod(chp, chpa) // 如果两者有HTTP METHOD 重叠
                    && ZolltyPathMatcher.isTwoPatternSimilar(chp.getPatternStr(),
                            chpa.getPatternStr())) { // 且URL Pattern有重叠
                LOG.error("'{}' is a duplicate of '{}'", chp.getMeta(), chpa.getMeta());
                return true;
            }
        }

        // 检测 如果有已定义的 普通ControllerHandler 被当前的ControllerHandlerPattern所匹配到，则给出WARN级别的提示。
        for (RequestViewHandler ch : simpleControllerList) {
            ControllerMeta ctrl = ch.getMeta();
            for (String me : ctrl.getAllowHttpMethods()) {
                if (chp.getMeta().allowMethod(me)) {
                    if (chp.getHandler(ctrl.getServletURI(), me) != null) {
                        LOG.warn("'{}' is included by '{}'", ctrl.getServletURI(), chp.getMeta()
                                .getServletURI());
                    }
                }
            }
        }

        return false;
    }

    protected boolean isAllowMethod(ControllerHandlerPattern chp1, ControllerHandlerPattern chp2) {
        for (String me : chp1.getMeta().getAllowHttpMethods()) {
            if (chp2.getMeta().allowMethod(me))
                return true;
        }
        for (String me : chp2.getMeta().getAllowHttpMethods()) {
            if (chp1.getMeta().allowMethod(me))
                return true;
        }
        return false;
    }

    protected static List<String> parseUriParams(String uri) {
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
                    if (temp.indexOf("[") != -1 || temp.indexOf("]") != -1
                            || temp.indexOf("{") != -1 || temp.indexOf("}") != -1) {
                        throw new IllegalArgumentException(
                                "uri path variable error, can't include those symbols: {, [, }, ]. URI="
                                        + uri);
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
                    if (temp.indexOf("[") != -1 || temp.indexOf("]") != -1
                            || temp.indexOf("{") != -1 || temp.indexOf("}") != -1) {
                        throw new IllegalArgumentException(
                                "uri path variable error, can't include those symbols: {, [, }, ]. URI="
                                        + uri);
                    }
                    params.add(temp);
                    c = -1;
                }
            }
        }
        return params;
    }

}