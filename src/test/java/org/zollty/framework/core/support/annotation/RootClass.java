/*
 * @(#)RootClass.java
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
public class RootClass {
	
	@Inject
	private Object superObj;
	
	@Inject
	public void setSuperObj(Object obj){
		this.superObj = obj;
	}
	
	public Object getSuperObj(){
		return this.superObj;
	}

}
