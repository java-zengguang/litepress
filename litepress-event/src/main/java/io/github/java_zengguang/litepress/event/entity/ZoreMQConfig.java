package io.github.java_zengguang.litepress.event.entity;

public class ZoreMQConfig {
    public String port;
    public String registerURL;
    public int ioThreads = 1;
    public int maxSessionThreads = 50;
    public String busAddress = "inproc://zeromq-bus";
}
