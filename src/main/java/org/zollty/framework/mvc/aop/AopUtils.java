package org.zollty.framework.mvc.aop;

public class AopUtils {

    public static boolean judgeIsInstance(Class<?> c, Class<?> o){
        return c.isAssignableFrom(o);
    }
    
    
    public static void main(String[] args) {
        
       // System.out.println( judgeIsInstance(MvcBefore.class, AxxxxBefore.class) );
        
    }
    
    
}
