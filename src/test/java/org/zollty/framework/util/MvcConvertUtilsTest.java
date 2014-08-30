package org.zollty.framework.util;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author zollty
 * @since 2014-5-29
 */
@RunWith(JUnit4.class)
public class MvcConvertUtilsTest {
    
    @Test
    public void testCollectionConvert() {
        String[] res = new String[]{"aa", "bbb", "ccc"};
        List<String> list = Arrays.asList(res);
        String[] ary = (String[]) MvcConvertUtils.convert(list, String[].class);
        assertArrayEquals(res, ary);
    }

}
