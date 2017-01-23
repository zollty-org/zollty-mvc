package org.zollty.framework.core.beans;

import java.io.Serializable;

public class Foo extends BasicBean implements Serializable {
    private static final long serialVersionUID = -3723028242467068948L;

    private int num;
    
    private String name;
    
    public Foo() {
    }
    
    public Foo(int num, String name) {
        super();
        this.num = num;
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
