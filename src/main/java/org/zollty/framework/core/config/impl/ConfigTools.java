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
 * Create by ZollTy on 2014-5-22 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.config.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.zollty.framework.util.MvcUtils;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;

/**
 * 
 * @author zollty
 * @since 2014-5-22
 */
class ConfigTools {

    public static Set<String> parseExcludePrefix(String prefix) {
        Set<String> excludePrefixes = new HashSet<String>();
        // 前缀，为空则不拦截
        boolean bt = false;
        String[] tarry = null;
        if (prefix != null) {
            prefix = prefix.replaceAll(" ", "");
            if (prefix.length() != 0) {
                bt = true;
            }
        }
        if (bt) {
            tarry = MvcUtils.StringSplitUtil.splitIgnoreEmpty(prefix, ',');
            // 检测前缀
            String add = "/";
            String tmp = null;
            for (int i = 0; i < tarry.length; i++) {
                tmp = tarry[i];
                if (tmp.charAt(0) == '/') {
                    excludePrefixes.add(tmp);
                }
                else {
                    excludePrefixes.add(add + tmp);
                }
            }
        }

        Logger log = LogFactory.getLogger(ConfigTools.class);
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder(40);
            sb.append("prefix = [{");
            for (String pref : excludePrefixes) {
                sb.append(pref).append("},{");
            }
            sb.append("}]");
            int index = sb.lastIndexOf(",{}]");
            if (index != -1) {
                sb = new StringBuilder(sb.substring(0, index));
                sb.append("]");
            }
            log.debug("no-intercept - " + sb.toString());
        }
        return excludePrefixes;
    }

    public static Set<String> parseExcludeSuffix(String suffix) {
        Set<String> excludeSuffixes = new HashSet<String>();
        boolean bt = false;
        String[] tarry = null;
        // 后缀，取配置值或者默认值
        bt = false;
        if (suffix != null) {
            suffix = suffix.replaceAll(" ", "");
            if (suffix.length() != 0) {
                bt = true;
            }
        }
        if (bt) {
            tarry = MvcUtils.StringSplitUtil.splitIgnoreEmpty(suffix, ',');
            excludeSuffixes.addAll(new HashSet<String>(Arrays.asList(tarry)));
        }

        Logger log = LogFactory.getLogger(ConfigTools.class);
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder(40);
            sb.append("suffix = [{");
            for (String suff : excludeSuffixes) {
                sb.append(suff).append("},{");
            }
            sb.append("}]");
            int index = sb.lastIndexOf(",{}]");
            if (index != -1) {
                sb = new StringBuilder(sb.substring(0, index));
                sb.append("]");
            }
            log.debug("no-intercept - " + sb.toString());
        }
        return excludeSuffixes;
    }

}