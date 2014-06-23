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
package org.zollty.framework.util.pattern;

import java.util.ArrayList;
import java.util.List;

import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.ZolltyUtils;

/**
 * 字符串匹配算法，专用于PathParam
 * @author zollty
 * @since 2014-6-2
 * @see MvcUrlPatternTest 单元测试类
 */
public class MvcUrlPattern {
    
    private static Logger LOG = LogFactory.getLogger(MvcUrlPattern.class);
    
    public MvcUrlPattern(final String pattern) {
        this.pattern = pattern;
        this.check();
    }

    private final String pattern;
    private final List<MatchInfo> miList = new ArrayList<MatchInfo>();
    
    public List<String> match(String src) {
        List<TempMatchValue> tempValueList = new ArrayList<TempMatchValue>();
        List<String> valueList = new ArrayList<String>();

        List<MatchFlag> tempMfList = new ArrayList<MatchFlag>(miList.size());
        for(MatchInfo ma: miList){
            tempMfList.add(new MatchFlag(ma));
        }
        
        if(LogFactory.isTraceEnabled()){
            LOG.trace("Pattern={}, Src={}", pattern, src);
        }
        if (!this.isMatch(src, tempMfList, tempValueList)) {
            if(LogFactory.isTraceEnabled()){
                LOG.trace("Matched Failure. Pattern={}, Src={}", pattern, src);
            }
            return null;
        }
        if(LogFactory.isTraceEnabled()){
            LOG.trace("Matched Success. Pattern={}, Src={}", pattern, src);
        }
        this.handleValue(valueList, tempValueList);
        return valueList;
    }
    
    private boolean isMatch(String src, List<MatchFlag> tempMfList, List<TempMatchValue> tempValueList) {
        MatchFlag mf;
        int inx;
        String tempStr;
        int[] b = new int[tempMfList.size() + 1];
        int i = 0;
        for (; i < tempMfList.size(); i++) {
            mf = tempMfList.get(i);
            inx = src.indexOf(mf.ma.matchStr, b[i]);
            while (inx != -1) {
                tempStr = src.substring(b[i], inx);
                if(LogFactory.isTraceEnabled()){
                    LOG.trace(mf.ma.matchStr+"["+tempStr+"]");
                }
                tempValueList.add(new TempMatchValue(mf, tempStr));
                if(mf.ma.matchType==0 && tempStr.indexOf("/")!=-1){ //没匹配到，继续变换上一个index尝试，如果没有匹配则return
                    b[i + 1] = inx + mf.ma.matchStr.length();
                    b[i] = b[i + 1];
                    inx = -1;
                    break;
                }
                // OK，匹配到一个，重新定义src的寻址位置，继续下一个for循环matchStr
                b[i + 1] = inx + mf.ma.matchStr.length();
                b[i] = b[i + 1];
                if (i + 2 > tempMfList.size()) {
                    tempValueList.add(new TempMatchValue(new MatchFlag(null), src.substring(b[i + 1])));
                }
                break;
            }
            if (inx == -1) {
                --i; // 回溯
                if (--i < -1) {
                    return false;
                }
                continue;
            }
        }
        if (pattern.endsWith("**")) {
        } else if (pattern.endsWith("*")) {
            if (src.indexOf("/", b[i]) != -1) {
                return false;
            }
        } else if (tempValueList.get(tempValueList.size()-1).value.length() != 0) {
            return false;
        }
        return true;
    }
    
    
    private void handleValue(List<String> valueList, List<TempMatchValue> tempValueList){
        int size = tempValueList.size()-1;
        TempMatchValue t1, t2;
        for (int i = 0; i < size; i++) {
            t1 = tempValueList.get(i);
            for (int j = i-1; j >= 0; j--) {
                t2 = tempValueList.get(j);
                if(t2.mf.append) {
                    break;
                }
                if(t2.mf.equals(t1.mf)){
                    if(canAppend(t1.mf, tempValueList)){
                        t2.mf.append=true;
                    }
                    break;
                }
            }
        }
        List<TempMatchValue> tv = new ArrayList<TempMatchValue>();
        for (int i = 0; i < size; i++) {
            t1 = tempValueList.get(i);
            if(!t1.mf.used) {
                tv = new ArrayList<TempMatchValue>();
                tv.add(t1);
                for (int j = 0; j < size; j++) {
                    if (j != i) {
                        t2 = tempValueList.get(j);
                        if (t2.mf.equals(t1.mf)) {
                            tv.add(t2);
                        }
                    }
                }
                if(!t1.mf.append){
                    valueList.add(tv.get(tv.size()-1).value);
                }else{
                    StringBuilder val = new StringBuilder();
                    String mstr = t1.mf.ma.matchStr;
                    for (int k = 0; k < tv.size(); k++) {
                        if (k != tv.size() - 1) {
                            val.append(tv.get(k).value).append(mstr);
                        } else {
                            val.append(tv.get(k).value);
                        }
                    }
                    valueList.add(val.toString());
                }
                t1.mf.used = true;
            }
        }
        
        if(!pattern.startsWith("*")&&!pattern.startsWith("**")){
            valueList.remove(0);
        }
        if(pattern.endsWith("*")||pattern.endsWith("**")){
            valueList.add(tempValueList.get(tempValueList.size()-1).value);
        }
        if(LogFactory.isTraceEnabled()){
            LOG.trace("ValueList={}", valueList.toString());
        }
    }
    
    
    private boolean canAppend(MatchFlag mf, List<TempMatchValue> tempValueList) {
        int size = tempValueList.size() - 1;
        TempMatchValue t1, t2;
        for (int i = size - 1; i > 0; i--) {
            t1 = tempValueList.get(i);
            if (t1.mf.equals(mf)) {
                t2 = tempValueList.get(i - 1);
                if (t2.mf.append) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private final void check(){
        if(LogFactory.isTraceEnabled()){
            LOG.trace("begin init pattern[{}]", pattern);
        }
        char[] pchar = pattern.toCharArray();
        pchar = ZolltyUtils.ArrayUtil.addChar(pchar, '$');
        int p1 = -1, p2 = -1;
        String val1;
        int mf = -1; // mf标记，1代表“**匹配”, 0代表“*匹配”
        for(int i=0;i<pchar.length-1;i++){
            if(pchar[i]=='*' && pchar[i+1]=='*'){
                if(p1==-1) {
                    p1 = i+2; //System.out.println(p1+"---p1");
                    i++;
                } else {
                    p2 = i; 
                    val1 = pattern.substring(p1, p2);
                    if(p1>1 && pchar[p1-2]=='*' && pchar[p1-1]=='*'){
                        mf = 1;
                    } else {
                        mf = 0;
                    }
                    //System.out.println(new MatchFlag(mf, val1));
                    miList.add(new MatchInfo(mf, val1));
                    p1 = -1;
                    i++;
                }
            }
            else if(pchar[i]=='*' && pchar[i+1]!='*'){
                if(p1==-1) {
                    p1 = i+1; //System.out.println(p1+"---p1");
                } else {
                    p2 = i;
                    val1 = pattern.substring(p1, p2);
                    if(p1>1 && pchar[p1-2]=='*' && pchar[p1-1]=='*'){
                        mf = 1;
                    } else {
                        mf = 0;
                    }
                    //System.out.println(new MatchFlag(mf, val1));
                    miList.add(new MatchInfo(mf, val1));
                    p1 = -1;
                }
            }
            else if( pchar[i+1]=='$' && p1!=-1){ // !a
                p2 = i;
                val1 = pattern.substring(p1, p2+1);
                if(p1>1 && pchar[p1-2]=='*' && pchar[p1-1]=='*'){
                    mf = 1;
                } else {
                    mf = 0;
                }
                //System.out.println(new MatchFlag(mf, val1));
                miList.add(new MatchInfo(mf, val1));
                p1 = -1;
            }
            else if(p1==-1){
                p1 = i;
            }
        }
        if(p1!=-1){
            p2 = pchar.length-1;
            val1 = pattern.substring(p1, p2);
            if(p1>1 && pchar[p1-2]=='*' && pchar[p1-1]=='*'){
                mf = 1;
            } else {
                mf = 0;
            }
            //System.out.println(new MatchFlag(mf, val1));
            miList.add(new MatchInfo(mf, val1));
            p1 = -1;
        }
        if(LogFactory.isTraceEnabled()){
            LOG.trace("MatchInfo List = {}", miList.toString());
        }
    }
    
    private static class MatchInfo {
        /** 匹配类型, 1代表"**匹配", 0代表"*匹配" */
        int matchType;
        String matchStr;
        public MatchInfo(int matchType, String matchStr){
            this.matchType = matchType;
            this.matchStr = matchStr;
        }
        public String toString(){
            return "MatchInfo(matchStr="+matchStr+", type="+matchType+")";
        }
    }

    private static class MatchFlag {
        MatchInfo ma;
        boolean append;
        boolean used;
        public MatchFlag(MatchInfo ma){
            if(ma!=null){
                this.ma  = new MatchInfo(ma.matchType, ma.matchStr);
            } else {
                this.ma  = null;
            }
        }
    }
    
    private static class TempMatchValue {
        MatchFlag mf;
        String value;
        public TempMatchValue(MatchFlag mf, String value) {
            super();
            this.mf = mf;
            this.value = value;
        }
    }
}
