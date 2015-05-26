package org.zollty.framework.mvc.aop.annotation;

import org.zollty.framework.mvc.aop.MvcAfter;

public @interface CAfter {

    Class<? extends MvcAfter>[] cls() default {};

    int[] order() default {0};

}
