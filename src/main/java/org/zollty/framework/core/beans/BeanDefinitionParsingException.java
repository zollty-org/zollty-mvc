package org.zollty.framework.core.beans;

import org.jretty.util.BasicRuntimeException;

public class BeanDefinitionParsingException extends BasicRuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5356594713002414317L;

    public BeanDefinitionParsingException() {
        super();
    }

    public BeanDefinitionParsingException(String message, String... args) {
        super(message, args);
    }

    public BeanDefinitionParsingException(Throwable e, String message, String... args) {
        super(e, message, args);
    }

    public BeanDefinitionParsingException(Throwable e) {
        super(e);
    }

}
