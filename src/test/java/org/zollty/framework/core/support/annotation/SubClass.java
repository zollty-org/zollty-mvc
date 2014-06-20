/*
 * @(#)SubClass.java
 * Create by Zollty_Tsou on 2014-1-4 
 * you may find ZollTy at csdn, github, oschina, stackoverflow...
 * e.g. https://github.com/zollty  http://blog.csdn.net/zollty 
 */
package org.zollty.framework.core.support.annotation;

import org.zollty.framework.core.annotation.Inject;

/**
 * @author zollty 
 * @since 2014-1-4
 */
public class SubClass extends RootClass {
	
	@Inject
	private Object obj;
	
	public Object getObj() {
		return obj;
	}

	@Inject
	public void setObj(Object obj) {
		this.obj = obj;
	}

}
