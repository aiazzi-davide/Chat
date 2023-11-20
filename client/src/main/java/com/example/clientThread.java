package com.example;
import java.io.*;
import java.net.Socket;


public class clientThread extends Thread{
    private Socket client;
    BufferedReader in;
    boolean errore = false;
    boolean x = false;
    public clientThread(Socket client){
        this.client = client;
    }
    public void run(){
        while (true) {
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String messaggio = in.readLine();
                if (messaggio.contains("CODICE_ERRORE: 0001")) {
                    errore = true;
                } else System.out.println(messaggio);
            } catch (IOException e) {
                System.out.println("Errore di I/O");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Errore generico");
                e.printStackTrace();
            }
        }
    }
    public boolean getErrore(){
        x = errore;
        errore = false;
        return x;
    }
}
