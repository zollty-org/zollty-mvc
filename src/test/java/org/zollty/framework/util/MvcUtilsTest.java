package org.zollty.framework.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.junit.Assert;
import org.junit.Test;

public class MvcUtilsTest {
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void toArrayObjTest(){
        
        Collection coll3 = new LinkedHashSet();
        coll3.add(new Integer(5));
        coll3.add(new Integer(8));
        
        // 注意： Integer 转换成了 int
        int[] ary4 = (int[]) MvcUtils.CollectionUtil.toArrayObj(coll3, int[].class);
        Assert.assertEquals("[5, 8]", Arrays.toString(ary4));
        
        
        Integer[] ary3 = (Integer[]) MvcUtils.CollectionUtil.toArrayObj(coll3, Integer[].class);
        Assert.assertEquals("[5, 8]", Arrays.toString(ary3));
    }

}
