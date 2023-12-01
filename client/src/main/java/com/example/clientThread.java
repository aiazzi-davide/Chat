package com.example;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class clientThread extends Thread{
    private Socket client;
    BufferedReader in;
    String errore = "";
    String messaggio = "";
    String SecretKey = "1234567890123456";
    boolean exit = false;
    public clientThread(Socket client){
        this.client = client;
    }

    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                
                //ricezione messaggio
                messaggio = in.readLine();
                if (messaggio == null) {
                    continue;
                }
                messaggio = decryptMessage(messaggio, SecretKey);

                //controllo errori
                if (messaggio.contains("0002")) {
                    errore = "0002";
                } else  {
                    //gestione lista
                    if (messaggio.contains("Lista client connessi:")) {
                        messaggio = messaggio.replaceAll(", ", "\n");
                    }
                    System.out.println(messaggio);
                    errore = "0000";
                }
            }
            
        } catch (Exception e) {
            System.out.println("Errore di connessione");
        }
    }

    public String decryptMessage(String encryptedMessage, String secretKey) {
        try{
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
        decryptedMessageBytes = cipher.doFinal(decryptedMessageBytes);
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
         } catch (Exception e) {
            System.out.println("Errore di decriptazione");
            e.printStackTrace();
            return null;
        }
    }
    public String getError(){
        return errore;
    }
}
