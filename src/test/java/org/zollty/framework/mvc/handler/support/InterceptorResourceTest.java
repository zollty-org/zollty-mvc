package org.zollty.framework.mvc.handler.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.support.InterceptorMetaInfo;
import org.zollty.framework.util.MvcReflectUtils;

/**
 * @author zollty
 * @since 2014-6-3
 */
public class InterceptorResourceTest {
    
    
    public static void main(String[] args) {
      unitTest();
  }
    
    public static void unitTest(){
        String uriPattern = "/mlf4j/**";
        String uriPattern2 = "/mlf4j/vf/cc/**";
        String uriPattern3 = "/mlf4j/vf/cb/**";
        
        String servletURI = "/mlf4j/vf/cb/uu";
        Method me = MvcReflectUtils.findMethod(Pattern.class, "compile");
        InterceptorMetaInfo im1 = new InterceptorMetaInfo(null, me, new String[]{uriPattern}, 8);
        InterceptorMetaInfo im2 = new InterceptorMetaInfo(null, me, new String[]{uriPattern2, uriPattern3}, 1);
        List<InterceptorMetaInfo> interceptorList = new LinkedList<InterceptorMetaInfo>();
        interceptorList.add(im1);
        interceptorList.add(im2);
        
        InterceptorResource cr = new InterceptorResource();
        cr.addInterceptor(interceptorList);
        List<WebHandler> handlers = cr.getHandlers(servletURI);
        System.out.println("OK...."+Arrays.toString(handlers.toArray()));
    }

}
