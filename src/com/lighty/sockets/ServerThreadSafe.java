package com.lighty.sockets;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Liam on 16/04/2021.
 */
public class ServerThreadSafe extends ServerThread{
    private SSLSocket socket;
    private SecureServer server;

    public ServerThreadSafe(SSLSocket socket, SecureServer server) {
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
}
