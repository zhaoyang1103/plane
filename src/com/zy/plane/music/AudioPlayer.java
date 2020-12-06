package com.zy.plane.music;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.omg.SendingContext.RunTime;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.*;
import java.lang.management.ThreadMXBean;
import java.net.MalformedURLException;
import java.net.URL;

public class AudioPlayer  {
    Player player;
    File music;
    public static String bang = "src/com/zy/plane/music/blast_1.wav";

    //构造方法  参数是一个..
    public AudioPlayer(File file) {
        this.music = file;
    }

    //播放方法
    public void play() throws FileNotFoundException, JavaLayerException {

        BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(music));
        player = new Player(buffer);
        player.play();
    }



}
