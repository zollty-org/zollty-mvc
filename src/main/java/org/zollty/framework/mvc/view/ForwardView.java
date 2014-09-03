package org.zollty.framework.mvc.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;

public class ForwardView implements View {
    
    private final String uri;

    public ForwardView(String uri) {
        this.uri = uri;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.getRequestDispatcher(uri).forward(request, response);
    }

}
