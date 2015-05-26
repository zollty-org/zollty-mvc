package org.zollty.framework.mvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface InvokeParamsAdapter {
    
    Object[] getInvokeParams(HttpServletRequest request, HttpServletResponse response);

}
