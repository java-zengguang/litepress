package com.zg.direction;

import com.zg.direction.annotation.Provider;

@Provider(providerName = "/Test")
public class Test implements TestInte{

    public TestEntity  hello(){
        System.out.println("hello");
        return new TestEntity(1);
    }
}
