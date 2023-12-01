package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class client {
    String server;
    int port;
    public client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void start() {
        String ANSI_GREEN = "\033[1;32m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_BLUE = "\u001B[34m";
        String ANSI_RESET = "\u001B[0m";
        String commandPattern = "^(/|@all|all).*$";
        String encryptedString = "";
        try {
            Socket s = new Socket(server, port);
            BufferedReader tastieraNome = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String readline;
            String Secretkey = "1234567890123456";

            // Messaggi di benvenuto
            System.out.println(ANSI_GREEN + "Connesso al server" + ANSI_RESET);
            System.out.println("Benvenuto, se vuoi uscire digita " + ANSI_RED + "/exit" + ANSI_RESET + ": ");
            System.out.println("Inserisci " + ANSI_BLUE + "/tell @[nomedestinatario] [messaggio]" + ANSI_RESET + " per inviare un messaggio privato:");
            System.out.println("Inserisci " + ANSI_BLUE + "/tell @all [messaggio]" + ANSI_RESET + " per inviare un messaggio a tutti i client connessi:");
            System.out.println("Inserisci " + ANSI_BLUE + "/list" + ANSI_RESET + " per visualizzare la lista dei client connessi:");
            System.out.println("Per iniziare inserisci il tuo nome:");
            

            //gestione con thread il ricevimento dei messaggi
            clientThread thread = new clientThread(s);
            thread.start();

            //invio nome
            boolean nomeValido = false;
            do {
                readline = tastieraNome.readLine();
                // Controllo se il nome contiene uno o più spazi
                if (readline.matches(".*\\s+.*")) {
                    System.out.println(ANSI_RED + "Errore: il nome non può contenere spazi" + ANSI_RESET);
                } else if (readline.matches(commandPattern) || readline.equals("")) {
                    System.out.println(ANSI_RED + "Errore: nome non valido" + ANSI_RESET);
                }
                else {
                    nomeValido = true;
                    readline = encryptMessage(readline, Secretkey);
                    out.writeBytes(readline + '\n');
                }
                Thread.sleep(20);
                if (thread.getError().equals("0002")) {
                    nomeValido = false;
                }
            } while (!nomeValido);

            

            do {
                //invio messaggio
                //System.out.println("Inserisci il messaggio:");
                readline = tastieraNome.readLine();
                encryptedString = encryptMessage(readline, Secretkey);
                out.writeBytes(encryptedString + '\n');

            } while (!readline.equals("/exit"));
            System.out.println(ANSI_RED + "Disconnessione in corso..." + ANSI_RESET);
            thread.interrupt();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Errore generico ");
        }
    }
    //metodo per criptare il messaggio
    public String encryptMessage(String message, String secretKey) {
        try{
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
        } catch (Exception e) {
            System.out.println("Errore di criptazione");
            return null;
        }
    }
}
