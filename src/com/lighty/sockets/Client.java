package com.lighty.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Liam on 16/04/2021.
 */
public abstract class Client implements Runnable{

    public PrintWriter out;
    public BufferedReader in;

    public void startConnection(String ip, int port){}

    public void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

    public void stopConnection() {}

    public void run() {}
}
