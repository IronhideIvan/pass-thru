package com.passthru.android.util;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpHelper {
    public static String _ipAddress = "192.168.0.39";
    public static int _port = 7084;

    public static void sendUdp(String payload){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            DatagramSocket socket = new DatagramSocket();
            byte[] sentData = payload.getBytes();
            DatagramPacket packet = new DatagramPacket(sentData, sentData.length, InetAddress.getByName(_ipAddress), _port);
            socket.send(packet);
        }
        catch (Exception ex){
            Log.e("Error", ex.getMessage());
        }
    }

    public static void receiveUdp(){
        byte[] buffer = new byte[2048];
        DatagramSocket socket = null;
        try{
            socket = new DatagramSocket(_port, InetAddress.getByName(_ipAddress));
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
        }
        catch (Exception ex){
            Log.e("Error", ex.getMessage());
        }
        finally {
            if(socket != null){
                socket.close();
            }
        }
    }
}
