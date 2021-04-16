package com.lighty.sockets;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;

/**
 * Created by Liam on 1/04/2021.
 */
public abstract class ServerThread extends Thread {
    public InputStream input;
    public OutputStream output;
    public BufferedReader reader;
    public PrintWriter writer;

    public void run() {}

    public void sendMessage(String message){
        writer.println(message);
        writer.flush();
    }
}
