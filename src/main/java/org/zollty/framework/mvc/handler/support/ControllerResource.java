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
 * Create by Zollty Tsou [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.handler.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.support.ControllerMetaInfo;
import org.zollty.framework.mvc.support.PattenControllerHandler;

/**
 * @author zollty
 * @since 2014-5-29
 */
public class ControllerResource {

    protected final List<PattenControllerHandler> patternControllerList = new ArrayList<PattenControllerHandler>();
    private final Map<String, ControllerHandler> simpleUriControllerMap = new HashMap<String, ControllerHandler>();

    public void addController(ControllerMetaInfo controller) {
        String uri = controller.getServletURI();
        char last = uri.charAt(uri.length() - 1);
        if (last != '/') {
            uri += "/";
        }
        List<String> list = parseUriPathVariable(uri);
        if (list.size() == 0) {
            ControllerHandler result = new ControllerHandler(controller, null);
            simpleUriControllerMap.put(uri, result);
        }
        else {
            patternControllerList.add(new PattenControllerHandler(controller, list));
        }
    }

    public WebHandler getHandler(String servletURI) {
        if (servletURI == null) {
            return null;
        }
        char last = servletURI.charAt(servletURI.length() - 1);
        String newURI = servletURI;
        if (last != '/') {
            newURI += "/";
        }
        // step 1
        WebHandler ret = simpleUriControllerMap.get(newURI);
        if (ret != null) {
            return ret;
        }
        // step 2
        for (final PattenControllerHandler controller : patternControllerList) {
            ret = controller.getHandler(servletURI);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    static List<String> parseUriPathVariable(String uri) {
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

//  private static List<String> checkUriPathVariable(String uri){
//  List<String> list = MvcUtils.StringUtil.splitURL(uri);
//  List<String> params = new ArrayList<String>();
//  for(String svar: list){
//      List<String> placeholders = parsePlaceholder(svar);
//      params.addAll(placeholders);
//  }
//  String str1;
//  String str2;
//  int size = params.size();
//  for(int i=0;i<size;i++){
//      str1 = params.get(i);
//      for(int j=0;j<size;j++){
//          if(i==j){
//              continue;
//          }
//          str2 = params.get(j);
//          if(str2.equals(str1)){
//              throw new NestedRuntimeException("some uri params repeated. uri=[{}]", uri);
//          }
//      }
//  }
//  
//  return params;
//}
//
//private static List<String> parsePlaceholder(String str) {
//    char[] chars = str.toCharArray();
//    int a = -1, b = -1;
//    List<String> key = new ArrayList<String>();
//    for (int i = 0; i < chars.length; i++) {
//        if (chars[i] == '{') {
//            if (a == -1) {
//                a = i;
//            }
//        } else if (chars[i] == '}') {
//            if (a != -1) {
//                b = i;
//                key.add( str.substring(a+1, b) );
//                a = -1;
//            }
//        }
//    }
//    return key;
//}    
    
}
