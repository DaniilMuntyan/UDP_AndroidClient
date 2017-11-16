package com.example.androidclientudp;

import android.widget.TextView;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Fromfile implements Runnable{
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Fromfile(){}

    public Fromfile(String path, BufferedInputStream bufIS, BufferedOutputStream bufOS) {
        this.path = path;
        this.bufIS = bufIS;
        this.bufOS = bufOS;
    }

    public Fromfile(BufferedInputStream bufIS, String path, BufferedOutputStream bufOS, String name) {
        this.bufIS = bufIS;
        this.path = path;
        this.bufOS = bufOS;
        this.name = name;
    }

    public Fromfile(MainActivity mainActivity, String port, String address, String path, TextView textView, VideoView videoView, String ID, Socket socket) {
        this.mainActivity = mainActivity;
        this.path = path;
        this.address = address;
        this.port = port;
        this.textView = textView;
        this.videoView = videoView;
        this.ID = ID;
        this.socket = socket;
    }

    public Fromfile(MainActivity mainActivity, String port, String address, String path, TextView textView, VideoView videoView, String ID) {
        this.mainActivity = mainActivity;
        this.path = path;
        this.address = address;
        this.port = port;
        this.textView = textView;
        this.videoView = videoView;
        this.ID = ID;
    }

    private boolean isInit;
    private String ID;
    private VideoView videoView;
    private MainActivity mainActivity;
    private BufferedInputStream bufIS;
    private String path;
    private BufferedOutputStream bufOS;
    private String port,address;
    private int size;
    private String name;
    private TextView textView, textInfo;
    private FileOutputStream fos;
    private File file;
    private DatagramSocket datagramSocket;
    private byte[] byteArray = new byte[1048576];
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Socket getSocket() {
        return socket;
    }

    private Socket socket;
    public int getSize() {
        return size;
    }


    public byte[] getByteArray() {
        return byteArray;
    }

    private ArrayList<byte[]> byteList = new ArrayList<byte[]>();

    private void changeText(final String s){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(s);
            }
        });
    }
    private byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length + b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }
    @Override
    public void run() {
        try {
           //socket = Initialization.getSocket();
            if(socket == null){
                changeText("117");
                socket = new Socket(InetAddress.getByName(address), Integer.valueOf(port));
            }
            changeText("110");
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            changeText("113");
            new Thread(new Resender(input, output, textView, videoView, socket, mainActivity)).start();
            File file = new File(path);
            if(!file.exists()) file.createNewFile();
            FileInputStream fis = new FileInputStream(file.getPath());
            long s = file.length();
            size = new Integer((int) s);
            String stringSize = String.valueOf(size);
            output.writeObject(concat(new byte[]{Byte.valueOf(ID)}, String.valueOf(size).getBytes()));
            Integer i = size;
            while (fis.available() != 0) {
                size = (int)(fis.available());
                if(size < byteArray.length)
                    byteArray = new byte[size];
                else
                    byteArray = new byte[524288];
                fis.read(byteArray);
                byteArray = concat(new byte[]{Byte.valueOf(ID)}, byteArray);
                output.writeObject(byteArray);
            }
            fis.close();

        }catch(IOException e) {
            changeText(e.getMessage());
        }
    }
}