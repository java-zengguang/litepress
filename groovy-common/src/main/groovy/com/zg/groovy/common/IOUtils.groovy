package com.zg.groovy.common

import groovy.time.TimeCategory
import groovyjarjarpicocli.CommandLine

import java.util.zip.DataFormatException

class IOUtils {
    //全量读取
    static readFullList(File file){
        file.readLines(line->printf("你好，${line}"));
    }
    //流式读取
    static readSteamList(File file){

    }

    //流式读取
    static writeSteamList(File file){
        file.withWriter {writer->writer.write("hello")}
    }

    static printString(){
        printf("""你好
            天气不错""");
        String s="123456789";
        print(s[1..5]);
        Range range='a'..'x';
        String x="12344xxxbusowfa";
        print(x[range]);
    }

    static list(){
        List list=["123",['a','b','c'],"good","""去吧  
甜腻"""];
        List list1=["123","good"];
        print(list.minus(list1));
    }

    static map(){
        Map map=[:];
        map.put("1","2");
        map.forEach((a,b)->{print("nihao${a}+${b}")})
    }



     static void main(String[] args) {
        // map();



         use( TimeCategory ) {
             return STARTDATE  + ((PAYNO as int)-1).month
         }

    }
}
