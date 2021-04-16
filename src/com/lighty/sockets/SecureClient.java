package com.lighty.sockets;

import com.lighty.gui.GUI;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by Liam on 16/04/2021.
 */
public class SecureClient extends Client implements Runnable{
    private Object mutex = new Object();

    private GUI gui;
    private SSLSocket clientSocket;

    public SecureClient(GUI gui){
        this.gui = gui;
    }

    public void startConnection(String ip, int port) {
        try{
            System.setProperty("javax.net.debug", "all");

            //Key store
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            String password = "12345";
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("../../resources/client/certificate-client.p12");
            keyStore.load(inputStream, password.toCharArray());

            //Trust store
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            String password2 = "54321";
            InputStream inputStream1 = ClassLoader.getSystemClassLoader().getResourceAsStream("../../resources/server/certificate-server.p12");
            trustStore.load(inputStream1, password2.toCharArray());


            // set up the SSL Context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(createKeyManager(keyStore, password), createTrustManager(trustStore, password2), null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            clientSocket = (SSLSocket) sslSocketFactory.createSocket(ip, port);
            clientSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e){
            System.out.println("Error connecting to server");
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
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