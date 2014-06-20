/* @(#)TextView.java 
 * Copyright (C) 2013-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by zollty on 2013-8-05 [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.mvc.View;

/**
 * @author zollty
 * @since 2013-8-05
 */
public class TextView implements View {

	private static String encoding;
	private final String text;
	private final String mimeType;

	public static void setEncoding(String encoding) {
		if (TextView.encoding == null && encoding != null)
			TextView.encoding = encoding;
	}

	public TextView(String text) {
		this.text = text;
		this.mimeType = null;
	}
	
	public TextView(String text, String mimeType) {
		this.text = text;
		this.mimeType = mimeType;
	}

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding(TextView.encoding);
		if( null != mimeType){
			response.setHeader("Content-Type", mimeType + "; charset=" + TextView.encoding);
		}else{
			response.setHeader("Content-Type", "text/plain; charset=" + TextView.encoding);
		}
		PrintWriter writer = response.getWriter();
		try {
			writer.print(text);
		} finally {
			writer.close();
		}
	}

}
