package org.zollty.framework.mvc.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zollty.framework.mvc.aop.MvcBefore;

@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CBefore {
    
    Class<? extends MvcBefore>[] value();

    //int[] order() default {};

}
