package com.example.test;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpSender {
    private static Vibrator vibrator;
    public static void sendPacket(String message) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buffer = message.getBytes();
                    int port = 29996; // ПОРТ
                    InetAddress address = InetAddress.getByName("51.77.116.226"); // IP СЕРВЕРА

                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

                    socket.send(packet);

                    byte[] bufferResponce = new byte[1024];
                    DatagramPacket responsePacket = new DatagramPacket(bufferResponce, bufferResponce.length);
                    socket.receive(responsePacket);
                    // ДОБАВИТЬ КОРОТКОЕ ВИБРО

                    // Convert response to string
                    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    Log.d("UDP Response", response);

                    socket.close();

                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}



