package com.example.test;

import static com.example.test.MyForegroundService.LONG_VIBRATION_DURATION;
import static com.example.test.MyForegroundService.SERVER_IP;
import static com.example.test.MyForegroundService.SERVER_PORT;
import static com.example.test.MyForegroundService.SHORT_VIBRATION_DURATION;
import static com.example.test.MyForegroundService.counter;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;

public class UdpSender {
    private static Context context;
    private static Vibrator vibrator;

    public UdpSender(Context context) {
        UdpSender.context = context;
    }

    public static void sendPacket(String ID, int typeDuration, int typeButton, String message) throws IOException {
        String formattedMessage = ID + typeDuration + typeButton + String.format("%04d", counter) + message;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    byte[] buffer = formattedMessage.getBytes();
                    int port = SERVER_PORT; // ПОРТ
                    InetAddress address = InetAddress.getByName(SERVER_IP); // IP СЕРВЕРА

                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

                    socket.send(packet);
                    // ДОБАВИТЬ КОРОТКОЕ ВИБРО
                    vibrator.vibrate(SHORT_VIBRATION_DURATION);
                    counter++;

                    Instant sendPacketTime = Instant.now();
                    byte[] bufferResponce = new byte[1024];
                    DatagramPacket responsePacket = new DatagramPacket(bufferResponce, bufferResponce.length);

                    socket.setSoTimeout(3000); //Время в милисекундах ожидание ответа от сервера
                    try {
                        socket.receive(responsePacket);
                        Instant receivePacketTime = Instant.now();
                        Duration responceDuration = Duration.between(sendPacketTime, receivePacketTime);

                        // Convert response to string
                        String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                        Log.d("UDP Response", response);
                    } catch (SocketTimeoutException E) {
                        vibrator.vibrate(LONG_VIBRATION_DURATION);
                        // latency 300ms
                        Thread.sleep(1200);
                        vibrator.vibrate(SHORT_VIBRATION_DURATION);
                        Thread.sleep(600);
                        vibrator.vibrate(SHORT_VIBRATION_DURATION);
                    }

                    socket.close();

                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}



