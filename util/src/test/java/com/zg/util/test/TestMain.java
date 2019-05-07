package com.zg.util.test;

/**
 * Created by Administrator on 2018/11/29 0029.
 */
public class TestMain {

    private int count=0;
    private int head=0;
    private int body=0;
    private int money=10;

    public void buy(){
        count=count+money/2;
        head=head+count;
        body=body+count;
        exchange();

    }

    public void exchange(){
        System.out.println("---------");
        System.out.println("酒："+count+" 瓶盖"+head+" 瓶子"+body);
        if(head>=4){

            int x=head/4;
            head=head-4*x;
            count=count+x;
            head=head+x;
            body=body+x;
            exchange();
        }else if(body>=2){

            int x=body/2;
            body=body-2*x;
            count=count+x;
            head=head+x;
            body=body+x;
            exchange();
        }else{

            return;
        }


    }

    public static void main(String args[]) throws Exception {
        TestMain testMain=new TestMain();
        testMain.buy();
    }
}
