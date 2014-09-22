package org.zollty.framework.util;

import org.zollty.util.NestedRuntimeException;

public class MvcRuntimeException extends NestedRuntimeException {

    private static final long serialVersionUID = -2845664236708352011L;

    
    public MvcRuntimeException(Throwable e) {
        super(e);
    }

    /**
     * @param e
     * @param message 自定义错误信息
     * @param args 占位符参数--[ 变长参数，用于替换message字符串里面的占位符"{}" ]
     */
    public MvcRuntimeException(Throwable e, String message, String... args) {
        super(e, message, args);
    }

    /**
     * @param message 自定义错误信息
     * @param args 占位符参数--[ 变长参数，用于替换message字符串里面的占位符"{}" ]
     */
    public MvcRuntimeException(String message, String... args) {
        super(message, args);
    }

}
