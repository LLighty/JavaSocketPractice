package com.lighty.sockets;

import com.lighty.gui.GUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Liam on 1/04/2021.
 */
public class Client implements Runnable{

    private Object mutex = new Object();

    private GUI gui;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(GUI gui){
        this.gui = gui;
    }

    public void startConnection(String ip, int port) {
        try{
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e){
            System.out.println("Error connecting to server");
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

    public void stopConnection() {
        try{
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String inputLine;

        try{
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                gui.appendToTextArea(inputLine);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
