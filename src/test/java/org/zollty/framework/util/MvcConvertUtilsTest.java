package org.zollty.framework.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author zollty
 * @since 2014-5-29
 */
public class MvcConvertUtilsTest {
    
    public static void main(String[] args) {
        convertTest();
    }
    
    
    public static void convertTest() {
        List<String> list = MvcUtils.StringUtil.splitURL("/dsds/ope/user/");
        String[] ary = (String[])MvcConvertUtils.convert(list, String[].class);
        
        System.out.println(Arrays.deepToString(ary));
        
    }

}
