package com.zg.network.im.utils;


import org.tinylog.Logger;

public class AudioUtils {


    public static void playAudio() {
        try {
            //  InputStream is=AudioUtils.class.getResourceAsStream("/resources/audio/11210.wav");
        } catch (Exception e) {
            Logger.error(e);
        }

    }

    public static void playAudioThread() {
        Runnable runnable = () -> playAudio();
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void main(String[] args) {

        playAudio();
    }
}
