package com.zg.direction;

import com.mysql.cj.xdevapi.JsonArray;
import com.zg.direction.annotation.Provider;
import org.apache.poi.hssf.record.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

@Provider(providerName = "/Test")
public class Test implements TestInte{

    public TestEntity hello(){
        System.out.println("hello");
        List list=new ArrayList();
        list.add("1212");
        list.add("1213");

        TestEntity testEntity=new TestEntity();
        testEntity.list=list;
        return testEntity;
    }

}
