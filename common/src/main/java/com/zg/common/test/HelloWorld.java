package com.zg.common.test;



import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {

        Flowable.just("hello").map(x->x+" world").subscribe(System.out::println);


        Flowable flowable=  Flowable.fromCallable(()->{
            return "done";
        }).subscribeOn(Schedulers.io());

        System.out.println("1");

        Flowable flowable1= Flowable.fromCallable(()->{
            return "done";
        }).subscribeOn(Schedulers.io());

        System.out.println("1");

        Flowable.zip(flowable,flowable1,(result1,result2)->{
            return result1+"333"+result2;
        }).subscribeOn(Schedulers.io()).subscribe(System.out::println);

        System.out.println("1");
        Thread.sleep(3000);
    }



}