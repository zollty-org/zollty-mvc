package org.zollty.framework.core.support.xml;

import java.util.HashMap;

@SuppressWarnings("serial")
public class ManagedMap<K, V> extends HashMap<K, V> {
	
	/**
	 * 类型名称
	 */
	private String typeName;
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
