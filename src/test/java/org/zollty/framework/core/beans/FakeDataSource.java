package org.zollty.framework.core.beans;

public class FakeDataSource {
    
    private String name;
    
    public FakeDataSource(){
        
    }

    public FakeDataSource(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
