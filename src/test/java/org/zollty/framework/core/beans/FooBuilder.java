package org.zollty.framework.core.beans;

import java.util.List;

import org.zollty.framework.core.annotation.Inject;

public class FooBuilder {

    private int num;

    private String name="sssss";
    
    @Inject
    public BasicBean bean;

    public FooBuilder() {
    }

    public FooBuilder(int num, String name) {
        super();
        this.num = num;
        this.name = name;
    }

    public Foo build(int num, String name) {
        return new Foo(num, name);
    }
    
    public static Foo staticBuild(int num, String name) {
        return new Foo(num, name);
    }
    
    public Foo build2(int num, String[] names) {
        return new Foo(num, names[0]);
    }
    
    public Foo build3(int num, List<String> names) {
        return new Foo(num, names.get(0));
    }
    
    public Foo build() {
        return new Foo(num, name);
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