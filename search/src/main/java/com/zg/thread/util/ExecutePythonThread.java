package com.zg.thread.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecutePythonThread implements Runnable {


    private String[] arguments;


    public ExecutePythonThread(String... arguments) {
        this.arguments = arguments;
    }

    //传入 py环境地址    py脚本地址    py脚本参数 ...
    public void java2python(String... arguments) throws Exception {

        Process process = Runtime.getRuntime().exec(arguments);
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String result = "";
//        python里的运行结果，想传给java，就需要用这种readline的形式了。
        while ((line = in.readLine()) != null) {
            result += line;
            System.out.println(line);
        }
        in.close();
//        System.out.println(result);
    }


    @Override
    public void run() {
        try {
            java2python(this.arguments);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception {
        ExecutePythonThread executePython = new ExecutePythonThread("D:\\software\\Python\\Python37-32\\Python.exe", "D:\\test\\python\\temp.py", "1", "2");
        Thread thread = new Thread(executePython);
        thread.start();
        //   "D:\\software\\Python\\Python37-32\\Python.exe","D:\\test\\python\\temp.py"
    }

}
