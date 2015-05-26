package org.zollty.framework.mvc.aop;

import javax.servlet.http.HttpServletRequest;

public class MvcContext {
    
    private HttpServletRequest request;
    
    public MvcContext(HttpServletRequest request){
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    public String hello(){
        return "Hello World";
    }

}
