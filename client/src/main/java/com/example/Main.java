package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_BLUE = "\u001B[34m";
        String ANSI_RESET = "\u001B[0m";
        try {
            Socket s = new Socket("localhost", 4000);
            BufferedReader tastieraNome = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String readline;

            // Messaggi di benvenuto
            System.out.println(ANSI_GREEN + "Connesso al server" + ANSI_RESET);
            System.out.println("Benvenuto, se vuoi uscire digita " + ANSI_RED + "/exit" + ANSI_RESET + ": ");
            System.out.println("Inserisci " + ANSI_BLUE + "/tell @[nomedestinatario] [messaggio]" + ANSI_RESET + " per inviare un messaggio privato:");
            System.out.println("Inserisci " + ANSI_BLUE + "/tell @all [messaggio]" + ANSI_RESET + " per inviare un messaggio a tutti i client connessi:");
            System.out.println("Inserisci " + ANSI_BLUE + "/lista" + ANSI_RESET + " per visualizzare la lista dei client connessi:");
            System.out.println("Per iniziare inserisci il tuo nome:");
            

            //gestione con thread il ricevimento dei messaggi
            clientThread thread = new clientThread(s);
            thread.start();

            //invio nome
            do {
                readline = tastieraNome.readLine();
                out.writeBytes(readline + '\n');
            } while (thread.getError().equals("0002"));

            

            do {
                //invio messaggio
                //System.out.println("Inserisci il messaggio:");
                readline = tastieraNome.readLine();
                out.writeBytes(readline + '\n');
            } while (!readline.equals("/exit"));
            System.out.println(ANSI_RED + "Disconnessione in corso..." + ANSI_RESET);
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Errore generico ");
            e.printStackTrace();
        }
    }
}