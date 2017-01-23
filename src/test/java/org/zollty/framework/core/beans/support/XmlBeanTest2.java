package org.zollty.framework.core.beans.support;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.zollty.framework.core.beans.Foo;
import org.zollty.framework.core.beans.xml.XmlBeanReader;
import org.zollty.framework.util.ResourceContext;

public class XmlBeanTest2 {

    private static final String XML_PATH = "classpath:org/zollty/framework/core/beans/support/bean-test2.xml";
    
    private SimpleBeanFactory sbf;

    @org.junit.Before
    public void initSimpleBeanFactory() {
        ResourceContext resourcContext = new ResourceContext(Thread.currentThread().getContextClassLoader(), XML_PATH);
        SimpleBeanFactory sbf = new SimpleBeanFactory(new XmlBeanReader(resourcContext));
        this.sbf = sbf;
    }

    @Test
    public void testSimpleBeanFactory() {
        Foo aBean = sbf.getBean("foo6");
        System.out.println(sbf.getBeanMap());

        assertNotNull(aBean);
//        assertEquals("jdbc/zollty", aBean.getName());
        System.out.println(aBean.getName());
        System.out.println(aBean.getNum());
    }

}
