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
