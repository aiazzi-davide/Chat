package com.example;
import java.io.*;
import java.net.Socket;


public class clientThread extends Thread{
    private Socket client;
    BufferedReader in;
    int errore;
    boolean exit = false;
    public clientThread(Socket client){
        this.client = client;
    }

    public void terminate()  
    {  
        exit = true;
    }

    public void run(){
        while (true) {
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String messaggio = in.readLine();
                if (messaggio.contains("0002")) {
                    errore = 0002;
                } else if (messaggio.contains("0001")) {
                    errore = 0001;
                    in.close();
                    client.close();
                } else  {
                    System.out.println(messaggio);
                    errore = 0000;
                }
            } catch (Exception e) {
                System.out.println("Errore generico");
                e.printStackTrace();
            }
        }
    }
    public int getError(){
        return errore;
    }
}
