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
package org.zollty.framework.util.pattern;

import java.util.Arrays;

import org.zollty.framework.util.match.AntPathMatcher;
import org.zollty.framework.util.match.PathMatcher;

/**
 * @author zollty
 * @since 2014-5-28
 */
public class PatternTest {
    
    private static Case[] 测试用例 = new Case[]{
        new Case("/*/a*.html", "/ssa/a.html"),
        new Case("/s/*/*/", "/s/a/b/c"),
        new Case("/lesson*/**", "/lesson1/ss"), 
//        new Case("/*/*/*.html", "/aa/dsds/a.html"),
//        new Case("/s*-*/*/*", "/s{aa}-{cc}/{ccd}/{dd}"),
//        new Case("/a.html", "/a.html"),
//        new Case("**/csv/**", "sd/dsds/csv/saas/sads"),
        new Case("/webjars/**", "/webjars/bootstrap/3.1.0/css/bootstrap.min.css")
        };
    static class Case {
        String pattern;
        String uri;
        public Case(String pattern, String uri) {
            super();
            this.pattern = pattern;
            this.uri = uri;
        }
        @Override
        public String toString(){
            return "pattern: "+pattern+", uri: "+uri;
        }
    }
    
    public static void main(String[] args) {
        for(Case ca: 测试用例){
//            isMatch(ca);
            antMatch(ca);
        }
    }
    
    private static void antMatch(Case ca){
        PathMatcher pa = new AntPathMatcher();
        
       System.out.println( pa.match(ca.pattern, ca.uri));
        
    }
    protected static boolean isMatch(Case ca){
        Pattern pa = Pattern.compile(ca.pattern, "*");
        String[] ary = pa.match(ca.uri);
        System.out.println(pa.getClass());
        if(ary==null){
            System.out.println("Not Match==="+ca);
            return false;
        }
        System.out.println("Matched==="+Arrays.deepToString(ary));
        return true;
    }

}
