package org.zollty.framework.mvc.handler.support;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import org.zollty.framework.mvc.handler.WebHandler;
import org.zollty.framework.mvc.support.ControllerMetaInfo;
import org.zollty.framework.util.MvcReflectUtils;

/**
 * @author zollty
 * @since 2014-6-2
 */
public class ControllerResourceTest {
    
    public static void main(String[] args) {
//      parseUriPathVariableTest();
      unitTest();
  }
    
    public static void unitTest(){
        String uriPattern = "/mlf4j/{p1}/{p2}/{v1}";
        String servletURI = "/mlf4j/vf/logConfig/uu";
        Method me = MvcReflectUtils.findMethod(Pattern.class, "compile");
        ControllerMetaInfo cm = new ControllerMetaInfo(null, me, new String[]{"GET"}, uriPattern);
        ControllerResource cr = new ControllerResource();
        cr.addController(cm);
        WebHandler ha = cr.getHandler(servletURI);
        System.out.println("OK...."+ha);
    }
    
    public static void parseUriPathVariableTest(){
      String uri = "as{ak}sa/{v{1}/[c8]-[mk]/{v2}";
      List<String> list = ControllerResource.parseUriPathVariable(uri);
      System.out.println(list.size());
      System.out.println(list.toString());
    }
    
}
