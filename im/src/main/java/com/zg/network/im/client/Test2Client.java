package com.zg.network.im.client;

import com.zg.network.bean.ZGMPBean;

import java.util.Scanner;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public class Test2Client {
    public static void main(String args[]) {

        ResolveCommand resolveCommand = new ResolveCommand();
        ZGMPBean request = new ZGMPBean("REQUEST");
        IMClient imClient = new IMClient(new IMClientHandler(),"127.0.0.1",10000);
        imClient.addRequest(request);
        Thread thread=new Thread(imClient);
        thread.start();
        System.out.print("网络通信客户端");
        boolean go = true;

        Scanner input = new Scanner(System.in);
        while (go) {
            System.out.print("-》");
            String command = input.nextLine();
            if (command != null && !"".equals(command)) {
                if ("end".equals(command)) {
                    go = false;
                    System.exit(0);
                }
                request = resolveCommand.resolveCommand(command);
                if (request != null) {
                    imClient.addRequest(request);
                }
            }

        }

    }


}
