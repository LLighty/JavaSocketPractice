package com.lighty.sockets;

import java.io.*;
import java.net.Socket;

/**
 * Created by Liam on 1/04/2021.
 */
public class ServerThread extends Thread {
    private Socket socket;
    private Server server;
    private InputStream input;
    private OutputStream output;
    private BufferedReader reader;
    private PrintWriter writer;

    public ServerThread(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        try {
            String inputLine;

            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));

            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
                server.broadcastMessage(inputLine, this);
            }

            server.exitServer(this);
            socket.close();
        } catch (IOException ex) {
            server.exitServer(this);
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendMessage(String message){
        writer.println(message);
        writer.flush();
    }
}
