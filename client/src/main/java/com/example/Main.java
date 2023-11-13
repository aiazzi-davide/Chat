package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("10.22.9.9", 4000);
            BufferedReader tastieraNome = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader inDalServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream outVersoServer = new DataOutputStream(s.getOutputStream());
            String readline;
            do {
                  readline = tastieraNome.readLine();
                  System.out.println("Connesso al server");
            System.out.println("Inserisci il nome: ");
            outVersoServer.writeBytes(readline + '\n');
            System.out.println("Per inviare a tutti digita: TUTTI. Altrimenti digita il nome del destinatario: ");
            outVersoServer.writeBytes(readline + '\n');
            } while (!readline.equals("ARRIVEDERCI"));
        } catch (Exception e) {
           System.out.println("ERRORE");
        }
}
}