/*
 * @(#)StaticResourceView.java
 * Travelsky Report Engine (TRE) Source Code, Version 2.0
 * Author(s): 
 * Zollty Tsou (http://blog.csdn.net/zollty, zouty@travelsky.com)
 * Copyright (C) 2014-2015 Travelsky Technology. All rights reserved.
 */
package org.zollty.framework.mvc.view;


/**
 * @author zollty
 * @since 2014-5-27
 */
public class StaticResourceView extends AbstractStaticResourceView {
    
    private static String viewPathPrefix = "classpath:/META-INF/resources/";
    
    public StaticResourceView(String shortPath) {
        super(shortPath);
    }
    
    @Override
    public String getViewPathPrefix(){
        return viewPathPrefix;
    }

}
