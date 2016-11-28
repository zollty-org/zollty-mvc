package org.zollty.framework.core.beans;

import org.jretty.util.BasicRuntimeException;

public class BeansException extends BasicRuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5356594713002414317L;

    public BeansException() {
        super();
    }

    public BeansException(String message, String... args) {
        super(message, args);
    }

    public BeansException(Throwable e, String message, String... args) {
        super(e, message, args);
    }

    public BeansException(Throwable e) {
        super(e);
    }

}
