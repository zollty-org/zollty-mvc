/* 
 * Copyright (C) 2012-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by Zollty Tsou [http://blog.csdn.net/zollty (or GitHub)]
 */
package org.zollty.framework.mvc.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zollty.framework.core.config.IApplicationConfig;
import org.zollty.framework.ext.Constants;
import org.zollty.framework.mvc.View;
import org.zollty.framework.mvc.handler.support.ErrorHandler;
import org.zollty.framework.util.MvcUtils;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

public class StaticFileView implements View {
	
	private Logger log = LogFactory.getLogger(StaticFileView.class);
	public static final String CRLF = "\r\n";
	private static Set<String> ALLOW_METHODS = new HashSet<String>(Arrays.asList("GET", "POST", "HEAD"));
	private static IApplicationConfig CONFIG;
	private static String RANGE_ERROR_HTML;
	private static String TEMPLATE_PATH;
	private final String inputPath;
	private final int maxRangeNum = 1024;
	static {
	    StringBuilder ret = new StringBuilder();
	    ret.append("<!DOCTYPE html><html><head><title>Unexpect Error Occured</title></head><body>")
        .append("<h2>HTTP ERROR 416</h2><div>")
        .append("None of the range-specifier values in the Range request-header field overlap the current extent of the selected resource.")
        .append("</div><hr/><div style=\"font-style: italic; font-family: Baskerville, 'Group Old Style', Palatino, 'Book Antiqua', serif;\"><small>Created-By: ZolltyMVC Framework ").append(
                MvcUtils.DATEFORMAT.format(new Date())
                ).append("</small></div></body></html>");
	    RANGE_ERROR_HTML = ret.toString();
	}
	
	
	public StaticFileView(String path) {
		this.inputPath = path;
	}
	
	public static void init(IApplicationConfig serverConfig, String tempPath) {
		if(CONFIG == null && serverConfig != null)
			CONFIG = serverConfig;
		
		if(TEMPLATE_PATH == null && tempPath != null)
			TEMPLATE_PATH = tempPath;
	}
	
	/**
	 * 去除../或者./等非法路径，防止任意文件访问漏洞
	 * @param path 请求文件路径
	 */
	public static boolean checkPath(String path) {
		if(path.length() < 3)
			return true;

		if(path.charAt(0) == '/' && path.charAt(1) == '.') {
			if(path.charAt(2) == '/') 
				return false;
			if(path.length() > 3) {
				if(path.charAt(2) == '.' || path.charAt(3) == '/')
					return false;
			}
		}
		return true;
	}
	
	/**
     * max read file buffer size. default 500k
     */
    private static final int MAX_BUFFER_SIZE = 512000;
    
    /**
     * min read file buffer size. default 1k
     */
    private static final int MIN_BUFFER_SIZE = 1024;
    
    /**
     * @param len in-source-length e.g. long len = fileIn.length()
     */
    public static void clone(final InputStream in, int off, long len, final OutputStream out) throws IOException {
        byte[] buf;
        // 动态缓存大小
        // case1 LEN>200000kb(195M) -- BUF=500kb e.g. 200M--500k
        // case2 400kb< LEN <200000kb -- BUF=LEN/400 e.g. 100M--250k,
        // 10M--25k, 400kb--1kb
        // case3 LEN<400kb -- BUF=1kb e.g. 300kb--1kb, 0kb-1kb
        if (len > MAX_BUFFER_SIZE * 400) {
            buf = new byte[MAX_BUFFER_SIZE];
        } else if (len > MIN_BUFFER_SIZE * 400) {
            buf = new byte[(int) len / 400];
        } else {
            buf = new byte[MIN_BUFFER_SIZE];
        }
        int numRead = 0;
        numRead = in.read(buf, off, buf.length);
        if (-1 != numRead) {
            do {
                out.write(buf, 0, numRead);
            } while (-1 != (numRead = in.read(buf)));
        }
    }
	

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if(!checkPath(inputPath) || inputPath.startsWith(TEMPLATE_PATH)) {
		    new ErrorHandler(null, request.getRequestURI() + " not found",
                    HttpServletResponse.SC_NOT_FOUND).render(request, response);
			return;
		}
		
		if(!ALLOW_METHODS.contains(request.getMethod())) {
			response.setHeader("Allow", "GET,POST,HEAD");
			new ErrorHandler(null, "Only support GET, POST or HEAD method",
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED).render(request, response);
			return;
		}
		
		String path = inputPath;//CONFIG.getFileAccessFilter().doFilter(request, response, inputPath); 静态文件访问过滤器
		if (MvcUtils.StringUtil.isNullOrEmpty(path)){
		    return;
		}
		
		
		File file = new File(request.getSession().getServletContext().getRealPath(inputPath), path);
		if (!file.exists() || file.isDirectory()) {
		    new ErrorHandler(null, request.getRequestURI() + " not found",
                    HttpServletResponse.SC_NOT_FOUND).render(request, response);
            return;
		}

		String fileName = file.getName();
		String fileSuffix = getFileSuffix(fileName).toLowerCase();
        String contentType = Constants.MIME.get(fileSuffix);
        if (contentType == null) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        }
        else {
            String[] type = MvcUtils.StringSplitUtil.split(contentType, '/');
            if ("application".equals(type[0])) {
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            }
            else if ("text".equals(type[0])) {
                contentType += "; charset=" + CONFIG.getEncoding();
            }
            response.setContentType(contentType);
        }

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            long fileLen = file.length();
            String range = request.getHeader("Range");
            if (range == null) {
                MvcUtils.IOUtil.clone(new FileInputStream(file), fileLen, out);
            }
            else {
                String[] rangesSpecifier = MvcUtils.StringSplitUtil.split(range, '=');
                if (rangesSpecifier.length != 2) {
                    response.setStatus(416);
                    out.write(RANGE_ERROR_HTML.getBytes(CONFIG.getEncoding()));
                    return;
                }

                String byteRangeSet = rangesSpecifier[1].trim();
                String[] byteRangeSets = MvcUtils.StringSplitUtil.split(byteRangeSet, ',');
                if (byteRangeSets.length > 1) { // multipart/byteranges
                    String boundary = "ff10" + MvcUtils.RandomUtil.getRadomStr09AZaz(13);
                    if (byteRangeSets.length > maxRangeNum) {
                        log.error("multipart range more than {}", maxRangeNum);
                        response.setStatus(416);
                        out.write(RANGE_ERROR_HTML.getBytes(CONFIG.getEncoding()));
                        return;
                    }
                    // multipart output
                    List<MultipartByteranges> tmpByteRangeSets = new ArrayList<MultipartByteranges>(maxRangeNum);
                    // long otherLen = 0;
                    for (String t : byteRangeSets) {
                        String tmp = t.trim();
                        String[] byteRange = MvcUtils.StringSplitUtil.split(tmp, '-');
                        if (byteRange.length == 1) {
                            long pos = Long.parseLong(byteRange[0].trim());
                            if (pos == 0)
                                continue;
                            if (tmp.charAt(0) == '-') {
                                long lastBytePos = fileLen - 1;
                                long firstBytePos = lastBytePos - pos + 1;
                                if (firstBytePos > lastBytePos) {
                                    continue;
                                }

                                MultipartByteranges multipartByteranges = getMultipartByteranges(contentType,
                                        firstBytePos, lastBytePos, fileLen, boundary);
                                tmpByteRangeSets.add(multipartByteranges);
                            }
                            else if (tmp.charAt(tmp.length() - 1) == '-') {
                                long firstBytePos = pos;
                                long lastBytePos = fileLen - 1;
                                if (firstBytePos > lastBytePos) {
                                    continue;
                                }

                                MultipartByteranges multipartByteranges = getMultipartByteranges(contentType,
                                        firstBytePos, lastBytePos, fileLen, boundary);
                                tmpByteRangeSets.add(multipartByteranges);
                            }
                        }
                        else {
                            long firstBytePos = Long.parseLong(byteRange[0].trim());
                            long lastBytePos = Long.parseLong(byteRange[1].trim());
                            if (firstBytePos > fileLen || firstBytePos >= lastBytePos)
                                continue;

                            MultipartByteranges multipartByteranges = getMultipartByteranges(contentType, firstBytePos,
                                    lastBytePos, fileLen, boundary);
                            tmpByteRangeSets.add(multipartByteranges);
                        }
                    }

                    if (tmpByteRangeSets.size() > 0) {
                        response.setStatus(206);
                        response.setHeader("Accept-Ranges", "bytes");
                        response.setHeader("Content-Type", "multipart/byteranges; boundary=" + boundary);
                        for (MultipartByteranges m : tmpByteRangeSets) {
                            long length = m.lastBytePos - m.firstBytePos + 1;
                            out.write(m.head.getBytes(CONFIG.getEncoding()));
                            clone(new FileInputStream(file), (int) m.firstBytePos, length, out);
                        }
                        out.write((CRLF + "--" + boundary + "--" + CRLF).getBytes(CONFIG.getEncoding()));
                        log.debug("multipart download|{}", range);
                    }
                    else {
                        response.setStatus(416);
                        out.write(RANGE_ERROR_HTML.getBytes(CONFIG.getEncoding()));
                        return;
                    }
                }
                else {
                    String tmp = byteRangeSets[0].trim();
                    String[] byteRange = MvcUtils.StringSplitUtil.split(tmp, '-');
                    if (byteRange.length == 1) {
                        long pos = Long.parseLong(byteRange[0].trim());
                        if (pos == 0) {
                            response.setStatus(416);
                            out.write(RANGE_ERROR_HTML.getBytes(CONFIG.getEncoding()));
                            return;
                        }
                        if (tmp.charAt(0) == '-') {
                            long lastBytePos = fileLen - 1;
                            long firstBytePos = lastBytePos - pos + 1;
                            writePartialFile(request, response, out, file, firstBytePos, lastBytePos, fileLen);
                        }
                        else if (tmp.charAt(tmp.length() - 1) == '-') {
                            writePartialFile(request, response, out, file, pos, fileLen - 1, fileLen);
                        }
                        else {
                            response.setStatus(416);
                            out.write(RANGE_ERROR_HTML.getBytes(CONFIG.getEncoding()));
                            return;
                        }
                    }
                    else {
                        long firstBytePos = Long.parseLong(byteRange[0].trim());
                        long lastBytePos = Long.parseLong(byteRange[1].trim());
                        if (firstBytePos > fileLen || firstBytePos >= lastBytePos) {
                            response.setStatus(416);
                            out.write(RANGE_ERROR_HTML.getBytes(CONFIG.getEncoding()));
                            return;
                        }
                        if (lastBytePos >= fileLen) {
                            lastBytePos = fileLen - 1;
                        }
                        writePartialFile(request, response, out, file, firstBytePos, lastBytePos, fileLen);
                    }
                    log.debug("single range download|{}", range);
                }
            }
		} catch (Throwable e) {
			throw new RuntimeException("get static file output stream error");
		} finally {
			if (out != null)
				try {
					// System.out.println("close~~");
					out.close();
				} catch (IOException e) {
					throw new RuntimeException(
							"static file output stream close error");
				}
		}

	}
	
	private void writePartialFile(HttpServletRequest request,
			HttpServletResponse response, ServletOutputStream out,
			File file, long firstBytePos, long lastBytePos, long fileLen)
			throws Throwable {

		long length = lastBytePos - firstBytePos + 1;
		if (length <= 0) {
			response.setStatus(416);
			out.write(RANGE_ERROR_HTML.getBytes(CONFIG.getEncoding()));
			return;
		}
		response.setStatus(206);
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Content-Range", "bytes " + firstBytePos + "-"
				+ lastBytePos + "/" + fileLen);
		clone(new FileInputStream(file), (int)firstBytePos, length, out);
	}

	public static String getFileSuffix(String name) {
		if (name.charAt(name.length() - 1) == '.')
			return "*";

		for (int i = name.length() - 2; i >= 0; i--) {
			if (name.charAt(i) == '.') {
				return name.substring(i + 1, name.length());
			}
		}
		return "*";
	}

	private static class MultipartByteranges {
		public String head;
		public long firstBytePos, lastBytePos;
	}

	private MultipartByteranges getMultipartByteranges(String contentType,
			long firstBytePos, long lastBytePos, long fileLen, String boundary) {
		MultipartByteranges ret = new MultipartByteranges();
		ret.firstBytePos = firstBytePos;
		ret.lastBytePos = lastBytePos;
		ret.head = CRLF + "--" + boundary + CRLF + "Content-Type: "
				+ contentType + CRLF + "Content-range: bytes " + firstBytePos
				+ "-" + lastBytePos + "/" + fileLen + CRLF + CRLF;
		return ret;
	}

}
