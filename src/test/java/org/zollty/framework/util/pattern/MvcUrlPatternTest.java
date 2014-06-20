package org.zollty.framework.util.pattern;

import java.util.ArrayList;
import java.util.List;

import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * MvcUrlPattern 单元测试类
 * @author zollty
 * @since 2014-6-2
 */
public class MvcUrlPatternTest {
    
    private static Logger LOG = LogFactory.getLogger();
    
    private static List<TestCase> forTest = new ArrayList<TestCase>();
    static {
//      forTest.add(new TestCase("sss", "ssss", "[ssss]"));
        forTest.add(new TestCase("/mlf4j/*/*/*", "/mlf4j/vf/logConfig/uu", "[vf, logConfig, uu]"));
        forTest.add(new TestCase("*/ab/**/*", "P/ab/CC-TD/S", "[P, CC-TD, S]"));
        forTest.add(new TestCase("**/ab/**/ffd/*/dff*ddf/*S", "s/s/ab/g/g/ffd/m/dffrddf/sS", "[s/s, g/g, m, r, s]"));
        forTest.add(new TestCase("**/ab/**/ffd/*/dff*ddf/*S", "/ab/g/g/ffd/m/dffrddf/sS", "[, g/g, m, r, s]"));
        forTest.add(new TestCase("*/ab/**/ffd/*/dff*ddf/*S", "h/ab/g/g/ffd/m/dffrddf/sS", "[h, g/g, m, r, s]"));
        // 5
        
        forTest.add(new TestCase("*/ab/*-*/*", "P/ab/CC-TD/S", "[P, CC, TD, S]"));
        forTest.add(new TestCase("**/ab/f/*/a", "/ab/f/CC/DD/a/ab/f/CC/a", "[/ab/f/CC/DD/a, CC]")); // 问题在于，多次匹配，忽略了第二个匹配项
        // 解决方案-向后匹配-适用于“**开头且非**结尾的字符串”
        forTest.add(new TestCase("**/ab/f/*/a/**", "/ab/f/CC/DD/a/ab/f/BB/EE/a/ab/f/AA/a/ab/f/KK/EE/a/ab/f/HH/RR/a", "[/ab/f/CC/DD/a/ab/f/BB/EE/a, AA, ab/f/KK/EE/a/ab/f/HH/RR/a]"));
        forTest.add(new TestCase("/ab/**/f/*/c/", "/ab/a/f/CC/f/M/c/", "[a/f/CC, M]")); // 中间通配符匹配问题
        // 终极解决方案： 向后"循环匹配"法
        forTest.add(new TestCase("**/ab/**/f/*/c/**", "/ab/a/f/CC/f/M/c/", "[, a/f/CC, M, ]"));
        // 10
        
        forTest.add(new TestCase("**/ab/**/f/*/c/**/g/*/h/**", "/ab/a/f/CC/f/M/c/g/h/f/M/c/k/g/m/h/", "[, a/f/CC, M, g/h/f/M/c/k, m, ]")); // 只回溯了/f/
        forTest.add(new TestCase("**/a*c*d/", "/a/c/ac/d/acd/", "[/a/c/ac/d, , ]"));
        forTest.add(new TestCase("**/a**c*d/", "/a/c/ac/d/cd/", "[, /c/ac/d/, ]")); //回溯了c
        forTest.add(new TestCase("**m*n/**/e**c*d/", "/m/n/l/mn/l/e/c/ac/d/cd/", "[/m/n/l/, , l, /c/ac/d/, ]")); //回溯了m和c
        forTest.add(new TestCase("*/ab/**/*/**", "P/ab/S//", "[P, S, , ]"));
        // 15
        
        forTest.add(new TestCase("**/ab/**/ccc", "love/cx/ab/f/h/ccc", "[love/cx, f/h]"));
    }
    
    public static void main(String[] args) {
        TestCase testcase;
        String result;
        for(int i=0; i<forTest.size(); i++) {
            testcase = forTest.get(i);
            result = match(testcase.pattern, testcase.src);
            if(testcase.assume.equals(result)){
                LOG.info((i+1)+". Success. pattern="+testcase.pattern+", src="+testcase.src+", assume="+testcase.assume);
            } else {
                LOG.error((i+1)+". Failure. pattern="+testcase.pattern+", src="+testcase.src+", assume="+testcase.assume+", real result="+result);
            }
        }
    }
    
    public static String match(final String pattern, final String src){
        List<String> valueList = new MvcUrlPattern(pattern).match(src);
        if (valueList != null) {
            return valueList.toString();
        }
        return null;
    }
    
    static class TestCase{
        String pattern; // 字符模板
        String src; // 实际字符
        String assume; // 预计正确匹配时的结果（list.toString()）
        public TestCase(String pattern, String src, String assume) {
            super();
            this.pattern = pattern;
            this.src = src;
            this.assume = assume;
        }
    }

}
