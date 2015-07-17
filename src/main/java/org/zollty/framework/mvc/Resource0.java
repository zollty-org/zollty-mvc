/* 
 * Copyright (C) 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by ZollTy on 2013-9-16 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.mvc;

import java.util.HashMap;
import java.util.Map;

import org.zollty.framework.mvc.handler.ControllerViewHandler;
import org.zollty.framework.mvc.support.ControllerMetaInfo;

/**
 * 
 * @author zollty
 * @since 2013-9-16
 * @deprecated
 */
public class Resource0 {
//	public static final String WILDCARD = "?";
//	private static final String[] EMPTY = new String[0];
	private String encoding;
	
	private final Map<String, ControllerViewHandler> CONSTANT_URI;
	
	private String uri;
//	private Pattern pattern;
//	private String uriPattern;
	private ControllerMetaInfo controller;
//	private ResourceSet children = new ResourceSet();
	
	public Resource0(String encoding) {
		CONSTANT_URI = new HashMap<String, ControllerViewHandler>();
		this.encoding = encoding;
	}
	
//	private Resource(boolean root) {
//		CONSTANT_URI = root ? new HashMap<String, ControllerHandler>() : null;
//	}
	private Resource0() {
        CONSTANT_URI = null;
    }
	
	public ControllerMetaInfo getController() {
		return controller;
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void add(String uri, ControllerMetaInfo c) {
	    char last = uri.charAt(uri.length() - 1);
        if(last != '/') {
            uri += "/";
        }
        Resource0 resource = new Resource0();
        resource.uri = uri;
        resource.controller = c;
        ControllerViewHandler result = null;//new ControllerHandler(resource, null);
        CONSTANT_URI.put(uri, result);
//		if(uri.contains(WILDCARD)) {
//			Resource current = this;
//			List<String> list = MvcUtils.StringUtil.splitURL(uri);
//			int max = list.size() - 1;
//			
//			for (int i = 0; ;i++) {
//				String name = list.get(i);
//				if (i == max) {
//					current = current.children.add(name, c);
//					return;
//				}
//				
//				current = current.children.add(name, null);;
//			}
//		} else {
//			char last = uri.charAt(uri.length() - 1);
//			if(last != '/') {
//				uri += "/";
//			}
//			Resource resource = new Resource(false);
//			resource.uri = uri;
//			resource.controller = c;
//			ControllerHandler result = new ControllerHandler(resource, null);
//			CONSTANT_URI.put(uri, result);
//		}
	}
	
	public ControllerViewHandler match(String uri) {
		char last = uri.charAt(uri.length() - 1);
		if(last != '/') {
			uri += "/";
		}
		
		ControllerViewHandler ret = CONSTANT_URI.get(uri);
		if(ret != null)
			return ret;
		
//		Resource current = this;
//		List<String> list = MvcUtils.StringUtil.splitURL(uri);
//		List<String> params = new ArrayList<String>();
//		
//		for(String i : list) {
//			ret = current.children.match(i);
//			if(ret == null)
//				return ret;
//			
//			if(ret.getParams() != null) { // ret
//				for(String p : ret.getParams())
//					params.add(p);
//			}
//				
//			current = ret.getResource();
//		}
//		
//		if(ret == null || ret.getResource().controller == null)
//			return null;
//		
//		if(params.size() > 0)
//			ret.setParams(params.toArray(EMPTY));
		return ret;
	}
//	private static PathMatcher pathMatcher = new AntPathMatcher();
//	private class ResourceSet implements Iterable<Resource>{
//		private Map<String, Resource> map = new HashMap<String, Resource>();
//		private List<Resource> list = new LinkedList<Resource>();
//		
//		private boolean isVariable() {
//			return list.size() > 0;
//		}
//		
//		private ControllerHandler match(String str) {
//			Resource ret = map.get(str);
//			if(ret != null)
//				return new ControllerHandler(ret, null);
//			
//			for(Resource res : list) {
////			    if(pathMatcher.match(uriPattern, str)){
////			        return new ControllerHandler(res, p);
////			    }
//				String[] p = res.pattern.match(str);
//				if(p != null)
//					return new ControllerHandler(res, p);
//			}
//			
//			return null;
//		}

//		private Resource add(String uri, ControllerMetaInfo c) {
//			Resource resource = findByURI(uri);
//			if(resource == null) {
//				resource = new Resource();
//				resource.uri = uri;
//				
//				map.put(uri, resource);
////				if(uri.contains(WILDCARD)) {
//////				    resource.uriPattern = resource.uri;
////					resource.pattern = Pattern.compile(resource.uri, WILDCARD);
////					list.add(resource);
////				} else {
////					map.put(uri, resource);
////				}
//			}
//			if(c != null)
//				resource.controller = c;
//			return resource;
//		}
		
//        private Resource findByURI(String uri) {
//            Resource r = map.get(uri);
//            if (r != null) {
//                return r;
//            }
//            else {
//                for (Resource res : list) {
//                    if (uri.equals(res.uri))
//                        return res;
//                }
//            }
//            return null;
//        }
		

//		@Override
//		public Iterator<Resource> iterator() {
//			return new ResourceSetItr();
//		}
//		
//		private class ResourceSetItr implements Iterator<Resource> {
//			
//			private Iterator<Resource> listItr = list.iterator();
//			private Iterator<Entry<String, Resource>> mapItr = map.entrySet().iterator();
//
//			@Override
//			public boolean hasNext() {
//				return mapItr.hasNext() || listItr.hasNext();
//			}
//
//			@Override
//			public Resource next() {
//				if(mapItr.hasNext())
//					return mapItr.next().getValue();
//				else
//					return listItr.next();
//			}
//
//			@Override
//			public void remove() {
//				throw new RuntimeException("not implements this method!");
//			}
//			
//		}
//		
//	}

	@Override
	public String toString() {
		return toString(" ", "");
	}
	
	private String toString(String l, String append) {
		StringBuilder s = new StringBuilder();
//		s.append(append + uri + "(" + children.isVariable() + ")" + "\r\n");
//		for(Resource r : children) {
//			s.append(l + r.toString(l + " ", "├"));
//		}
		return s.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource0 other = (Resource0) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
