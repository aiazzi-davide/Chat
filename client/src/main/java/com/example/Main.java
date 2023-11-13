package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 4000);
            BufferedReader tastieraNome = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String readline;
            
            //messaggi benvenuto
            System.out.println("Connesso al server");
            System.out.println("Benvenuto, se vuoi uscire digita /exit: ");
            System.out.println("Inserisci il nome: ");

            //invio nome
            readline = tastieraNome.readLine();
            out.writeBytes(readline + '\n');

            //da fare: gestire con thread il ricevimento dei messaggi

            //invio destinatario
            System.out.println("Per inviare a tutti digita: TUTTI. Altrimenti digita il nome del destinatario: ");
            readline = tastieraNome.readLine();
            out.writeBytes(readline + '\n');

            //invio messaggio
            System.out.println("Digita il tuo messaggio: ");
            readline = tastieraNome.readLine();
            out.writeBytes(readline + '\n');

            
        } catch (Exception e) {
            System.out.println("ERRORE");
           e.printStackTrace();
        }
}
}