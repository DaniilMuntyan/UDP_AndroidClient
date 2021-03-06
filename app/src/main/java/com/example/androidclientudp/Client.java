package com.example.androidclientudp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client implements Runnable{
    ImageView im1, im2;
    TextView text;
    MainActivity main;
    Bitmap screen;
    @Override
    public void run() {
        // "0.tcp.ngrok.io"
        //"192.168.1.101"
        try {
            changeText("in Client");
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setBroadcast(true);
            InetAddress IPAddress = InetAddress.getByName("192.168.1.102");
            byte[] sendData = new byte[102400];
            byte[] receiveData = new byte[102400];
            Bitmap bitmap = ((BitmapDrawable) im1.getDrawable()).getBitmap();
            sendData = bitmapToByteArray(bitmap);
            Integer sizeSend = sendData.length;
            String s = String.valueOf(sizeSend);

            //отправляю сам массив, конвертированный из битмапа - С СЖАТИЕМ
            DatagramPacket sendPacket = new DatagramPacket(sendData, sizeSend, IPAddress, 60000);
            clientSocket.send(sendPacket);
            //принимаю массив
            DatagramPacket receivePacket = new DatagramPacket(receiveData, sizeSend);
            clientSocket.receive(receivePacket);
            //Конвертирую в битмап и вывожу картинку
            changeText("receiveData.length = " + receiveData.length + " sizeSend = " + sizeSend + "\nsendData.length = " + sendData.length);
            Bitmap receiveBitmap = BitmapFactory.decodeByteArray(receiveData, 0, sizeSend);
            changeIm(receiveBitmap);
        }catch(Exception e){
            changeText("" + e.getMessage());
        }
    }
    private int toSizeFromByte(byte[] b, int length){
        int ans = 0;
        for(int i = 0; i < length; i++)
            ans += b[0]*(length-i);
        return ans;
    }
    private byte[] toByteFromSize(int size){
        StringBuffer sb = new StringBuffer(size);
        byte[] answer = new byte[102400];
        for(int i = 0; i < sb.length(); i++)
            answer[i] = (Byte.valueOf(sb.charAt(i) + ""));
        return answer;
    }
    private void changeIm(final Bitmap bitmap){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                im2.setImageBitmap(bitmap);
            }
        });
    }
    private void changeText(final String s){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(s);
            }
        });
    }
    public byte[] bitmapToByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    public Client(){}
    public Client(ImageView im1, ImageView im2, TextView text, MainActivity main){
        this.im1 = im1;
        this.im2 = im2;
        this.text = text;
        this.main = main;
    }
}
