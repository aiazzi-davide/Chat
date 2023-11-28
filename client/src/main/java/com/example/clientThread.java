package com.example;
import java.io.*;
import java.net.Socket;


public class clientThread extends Thread{
    private Socket client;
    BufferedReader in;
    String errore = "0000";
    boolean exit = false;
    public clientThread(Socket client){
        this.client = client;
    }

    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (errore != "0001") {
                //ricezione messaggio
                String messaggio = in.readLine();

                //controllo errori
                if (messaggio.contains("0002")) {
                    errore = "0002";
                } else if (messaggio.contains("0001")) {
                    errore = "0001";
                    in.close();
                    client.close();
                } else  {
                    //gestione lista
                    if (messaggio.contains("Lista client connessi:")) {
                        messaggio = messaggio.replaceAll(", ", "\n");
                    }
                    System.out.println(messaggio);
                    errore = "0000";
                }
            } 
            return;
        } catch (Exception e) {
            System.out.println("Errore di connessione");
        }
    }
    public String getError(){
        return errore;
    }
}
