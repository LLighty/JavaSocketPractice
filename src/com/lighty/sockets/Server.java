package com.lighty.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liam on 1/04/2021.
 */
public class Server {
    static boolean isRunning = true;
    final static int PORTNUMBER = 5555;

    List<ServerThread> serverThreads = new ArrayList<>();
    Object mutex = new Object();

    public void start(int portNumber) {
        try{
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server listening on port " + portNumber);

            while(isRunning){
                Socket newSocket = serverSocket.accept();
                System.out.println("New client has joined");

                ServerThread serverThread = new ServerThreadUnsafe(newSocket, this);

                synchronized (mutex) {
                    serverThreads.add(serverThread);
                }
                new Thread (serverThread).start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void exitServer(ServerThread serverThread){
        synchronized (mutex) {
            serverThreads.remove(serverThread);
        }
    }

    public void broadcastMessage(String message, ServerThread sThread){
        List<ServerThread> serverThreadCopy;
        synchronized (mutex) {
            serverThreadCopy = new ArrayList<>(serverThreads);
        }
        for (ServerThread serverThread : serverThreadCopy) {
            if(serverThread.equals(sThread)){
                continue;
            }
            serverThread.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(PORTNUMBER);
    }

    public static void closeServer(){
        isRunning = false;
    }
}
