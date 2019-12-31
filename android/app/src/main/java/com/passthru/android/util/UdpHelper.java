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
    private static InetAddress _ipAddress;
    private static int _port;
    private static DatagramSocket _socket;

    public static void connect(String ipAddress, int port){
        try{
            _ipAddress = InetAddress.getByName(ipAddress);
            _port = port;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            _socket = new DatagramSocket();
        }
        catch (Exception ex){
            Log.e("UdpHelper", ex.getMessage());
        }
    }

    public static void disconnect(){
        try{
            _socket.disconnect();
        }
        catch (Exception ex){
            Log.e("UdpHelper", ex.getMessage());
        }
        finally {
            _socket = null;
        }
    }

    public static boolean isConnected(){
        return _socket != null;
    }

    public static void sendUdp(String payload){
        if(_socket == null){
            Log.i("UdpHelper", "No socket connected.");
            return;
        }
        try{
            byte[] sentData = payload.getBytes();
            DatagramPacket packet = new DatagramPacket(sentData, sentData.length, _ipAddress, _port);
            _socket.send(packet);
        }
        catch (Exception ex){
            Log.e("UdpHelper", ex.getMessage());
        }
    }

    public static void receiveUdp(){
        byte[] buffer = new byte[2048];
        DatagramSocket socket = null;
        try{
            socket = new DatagramSocket(_port, _ipAddress);
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
