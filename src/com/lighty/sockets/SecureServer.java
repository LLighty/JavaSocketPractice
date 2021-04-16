package com.lighty.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by Liam on 1/04/2021.
 */
public class SecureServer {
    static boolean isRunning = true;
    final static int PORTNUMBER = 5554;

    List<ServerThread> serverThreads = new ArrayList<>();
    Object mutex = new Object();

    public void start(int portNumber) {
        try{
            System.setProperty("javax.net.debug", "all");

            //Key store
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            String password = "12345";
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("../../resources/server/certificate-server.p12");
            keyStore.load(inputStream, password.toCharArray());

            //Trust store
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            String password2 = "54321";
            InputStream inputStream1 = ClassLoader.getSystemClassLoader().getResourceAsStream("../../resources/client/certificate-client.p12");
            trustStore.load(inputStream1, password.toCharArray());


            // set up the SSL Context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(createKeyManager(keyStore, password), createTrustManager(trustStore, password2), null);

            SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(portNumber);
            serverSocket.setNeedClientAuth(true);
            serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
            System.out.println("Server listening on port " + portNumber);

            while(isRunning){
                SSLSocket newSocket = (SSLSocket) serverSocket.accept();
                System.out.println("New client has joined");

                ServerThread serverThread = new ServerThreadSafe(newSocket, this);

                synchronized (mutex) {
                    serverThreads.add(serverThread);
                }
                new Thread (serverThread).start();
            }
        } catch(IOException e){
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private KeyManager[] createKeyManager(KeyStore keyStore, String password) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        keyManagerFactory.init(keyStore, password.toCharArray());
        X509KeyManager x509KeyManager = null;
        for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
            if (keyManager instanceof X509KeyManager) {
                x509KeyManager = (X509KeyManager) keyManager;
                break;
            }
        }
        if (x509KeyManager == null) throw new NullPointerException();

        return new KeyManager[]{x509KeyManager};
    }

    private TrustManager[] createTrustManager(KeyStore trustStore, String password) throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX", "SunJSSE");
        trustManagerFactory.init(trustStore);
        X509TrustManager x509TrustManager = null;
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                x509TrustManager = (X509TrustManager) trustManager;
                break;
            }
        }

        if (x509TrustManager == null) throw new NullPointerException();

        return new TrustManager[]{x509TrustManager};
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
        SecureServer server = new SecureServer();
        server.start(PORTNUMBER);
    }

    public static void closeServer(){
        isRunning = false;
    }
}
