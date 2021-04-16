package com.lighty.gui;

import com.lighty.sockets.Client;
import com.lighty.sockets.ClientUnsafe;
import com.lighty.sockets.SecureClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Liam on 6/04/2021.
 */
public class GUI {
    final String APPLICATIONNAME = "Simple Sockets";

    //5555 - Insecure
    //5554 - Secure
    int portNum = 5554;

    //0 - insecure
    //1 - secure
    int currentClient = 1;

    Client client;
    JFrame jFrame;

    TextArea textArea;
    JTextField textField;

    public GUI(String[] args){
        if(args.length > 1){
            currentClient = Integer.parseInt(args[0]);

            if(currentClient > 1 || currentClient < 0){
                currentClient = 0;
            }
        }

        if(currentClient == 0){
            client = new ClientUnsafe(this);
            client.startConnection("127.0.0.1", portNum);
        } else{
            client = new SecureClient(this);
            client.startConnection("127.0.0.1", portNum);
        }
        jFrame = new JFrame();

        jFrame.setSize(750, 800);
        jFrame.setName(APPLICATIONNAME);
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(null);
        jFrame.setResizable(false);

        textArea = new TextArea();
        textField = new JTextField();

        //Text Area
        textArea.setEditable(false);
        textArea.setBounds(0,0,740,675);

        //Text Field
        textField.setBounds(0,680,740,25);
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();

                System.out.println("Enter was pressed");
                System.out.println("Text is: " + message);

                textArea.append(message + "\n");

                try{
                    client.sendMessage(message);
                } catch (Exception error){
                    error.printStackTrace();
                }
                textField.setText("");
            }
        });

        jFrame.add(textArea);
        jFrame.add(textField);

        new Thread (client).run();
    }


    public static void main(String[] args) {
        GUI gui = new GUI(args);
    }

    public void appendToTextArea(String message){
        textArea.append(message + "\n");
    }

}
