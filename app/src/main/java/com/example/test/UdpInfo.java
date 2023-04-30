package com.example.test;

import static com.example.test.MyForegroundService.SERVER_IP;
import static com.example.test.MyForegroundService.SERVER_PORT;
import static com.example.test.MyForegroundService.counter;

import android.content.Context;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;

public class UdpInfo {
    private static Context context;
    private static Vibrator vibrator;

    public UdpInfo(Context context) {
        UdpInfo.context = context;
    }

    public static void sendPacket(String ID) throws IOException {
        // Отправка сообщения {identificator} + «ps» + «{counter}» + «0» -
        String formattedMessage = ID + "ps" + String.format("%04d", counter) + "0";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1200);
                    byte[] buffer = formattedMessage.getBytes();
                    int port = SERVER_PORT; // ПОРТ
                    InetAddress address = InetAddress.getByName(SERVER_IP); // IP СЕРВЕРА

                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

                    //время начала отправки сообщения сохраняем в памяти(timePingSended).
                    Instant timePingSended = Instant.now();
                    socket.send(packet);  // отправляем пакет

                    //
                    byte[] bufferResponce = new byte[1024];
                    DatagramPacket responsePacket = new DatagramPacket(bufferResponce, bufferResponce.length);

                    //Ожидаем ответ от сервера той же мессаги 5 секунд.
                    socket.setSoTimeout(5000); //Время в милисекундах ожидание ответа от сервера
                    try {
                        socket.receive(responsePacket);
                        Instant receivePacketTime = Instant.now();
                        //Если ответ получен вычисляем время ответа между  {текущее время} и timePingSended в миллисекундах – pingValue
                        Duration pingValue = Duration.between(timePingSended, receivePacketTime);

                        // Convert response to string
                        String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                        Log.d("UDP Response", response + " ping: " + pingValue.toMillis());

                        //Отправляем сообщение {identificator} + «pm» + «{counter }» + pingValue.
                        String formattedMessageRespone = ID + "pm" + String.format("%04d", counter) + pingValue.toMillis();
                        byte[] buffer2 = formattedMessageRespone.getBytes();
                        DatagramSocket socket2 = new DatagramSocket();
                        DatagramPacket packet2 = new DatagramPacket(buffer2, buffer.length, address, port);
                        Log.d("UDP2-buffer", formattedMessageRespone);
                        socket2.send(packet2);

                        byte[] bufferResponce2 = new byte[1024];
                        DatagramPacket responsePacket2 = new DatagramPacket(bufferResponce2, bufferResponce.length);
                        String response2 = new String(responsePacket2.getData(), 0, responsePacket.getLength());
                        Log.d("UDP Response", response2);
                        socket2.close();


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare(); // add this line
                                // thread code here

                                Toast.makeText(context, "PING = " + pingValue.toMillis(), Toast.LENGTH_SHORT).show();
                                Looper.loop(); // add this line
                            }
                        }).start();


                    } catch (SocketTimeoutException E) {
                        // если небыло ответа
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare(); // add this line
                                // thread code here

                                Toast.makeText(context, "NO INFO" , Toast.LENGTH_SHORT).show();
                                Looper.loop(); // add this line
                            }
                        }).start();
                    }

                    counter++;
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
