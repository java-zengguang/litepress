package com.zg.network.im.utils;

import org.springframework.core.io.ClassPathResource;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.File;
import java.io.InputStream;


public class AudioUtils {


    public static void playAudio() {
        try {
            //  InputStream is=AudioUtils.class.getResourceAsStream("/resources/audio/11210.wav");
            ClassPathResource cpr = new ClassPathResource("audio" + File.separator + "11210.wav");
            InputStream in = cpr.getInputStream();
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void playAudioThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                playAudio();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void main(String args[]) {

        playAudio();
    }
}
