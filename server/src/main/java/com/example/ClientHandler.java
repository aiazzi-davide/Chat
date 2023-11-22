package com.example;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    String ANSI_GREEN = "\u001B[32m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_YELLOW = "\u001B[33m";   
    String ANSI_PURPLE = "\u001B[35m";  
    String ANSI_ORANGE = "\u001B[36m";
    String ANSI_RESET = "\u001B[0m";

    private Socket client;
    private Socket client2;
    private BufferedReader in1;
    private DataOutputStream out1;
    private BufferedReader in2;
    private DataOutputStream out2;
    ArrayList<ClientHandler> clients;
    String nome = "";
    String nome1 = "";
    String destinatario = "";
    String messaggio = "";

    public ClientHandler(Socket client, ArrayList<ClientHandler> clients){
        this.client = client;
        System.out.println("New client connected on port " + client.getPort());
        this.clients = clients;
    }
    public void run(){
        
        try {
            in1 = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out1 = new DataOutputStream(client.getOutputStream());

            do {
                
                //ricevo nome client
                nome1 = in1.readLine();
                System.out.println(ANSI_YELLOW + "[Client" + client.getPort() + "]" + ANSI_RESET + ": " + nome1);

            //controllo se il nome è già stato usato
            } while (checkNome(nome1));


            //salvo nome client
            nome = nome1;
            //invio a tutti che si è connesso un nuovo client
            inoltroBroadcast(ANSI_BLUE + nome + ANSI_RESET + " benvenuto nella chat!\n", ANSI_GREEN + "[SERVER]" + ANSI_RESET );
            System.out.println(ANSI_GREEN + "Nuovo client connesso: " + ANSI_BLUE + nome + ANSI_RESET + "\n");

            do {
                //ricezione messaggio
                messaggio = in1.readLine();
                
                

                //controllo comandi ricevuti

                //comando /tell
                if (messaggio.contains("/tell")) {

                    //comando /tell @all
                    if (messaggio.contains("@all")) {
                        System.out.println("Client" + client.getPort() + ": " + messaggio + " -> " + "tutti");
                        inoltroBroadcast(messaggio, nome);

                    //comando /tell @destinatario
                    } else if (messaggio.contains("@")){

                        //salvo destinatario client
                        String parts[] = messaggio.split(" ", 3);
                        if (parts.length < 3) {
                            out1.writeBytes(ANSI_RED + "Errore: formato del comando non valido\n" + ANSI_RESET);
                            continue;
                        }
                        destinatario = parts[1].substring(1); // Rimuove il simbolo '@'
                        String messaggio = parts[2];
                        client2 = getClientFromName(destinatario);

                        if (client2 != null) {
                            in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
                            out2 = new DataOutputStream(client2.getOutputStream());
                            System.out.println(ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + messaggio + ANSI_PURPLE + " -> " + ANSI_BLUE + destinatario + ANSI_RESET);
                            out2.writeBytes(ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + messaggio + '\n');
                        }
                    } else {
                        out1.writeBytes(ANSI_RED + "Errore: sintassi non corretta: assicurati di aver inserito @ prima del nome" + ANSI_RESET + '\n');
                    }
                } else if (messaggio.contains("/lista")) {
                    System.out.println("Client" + client.getPort() + ": " + messaggio);
                    out1.writeBytes(ANSI_GREEN + "Lista client connessi:" + ANSI_RESET + '\n');
                    for (ClientHandler c : clients) {
                        out1.writeBytes(ANSI_BLUE + c.getNome() + ANSI_RESET + '\n');
                    }
                } else if (messaggio.contains("/exit")) {
                    System.out.println("Client" + client.getPort() + ": " + messaggio);
                    out1.writeBytes(ANSI_ORANGE + "Disconnessione in corso..." + ANSI_RESET + '\n');
                    break;
                } else {
                    out1.writeBytes(ANSI_RED + "Errore: comando non riconosciuto" + ANSI_RESET + '\n');
                }

            } while (!messaggio.contains("/exit"));

            /* do {
                //ricevo destinatario client
                destinatario = in1.readLine();
                System.out.println("Client" + client.getPort() + ": " + destinatario);

                //controllo se destinatario è tutti
                if (destinatario.equals("TUTTI")) {
                    out1.writeBytes("Pronto per comunicare!\n");
                    break;
                }
                //inizializzo il client2
                client2 = getClientFromName(destinatario);

            } while (client2 == null);
            

            if (client2 != null) {
                in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
                out2 = new DataOutputStream(client2.getOutputStream());
            }

            do {
                //ricevo messaggio client
                messaggio = in1.readLine();
                System.out.println("Client" + client.getPort() + ": " + messaggio + " -> " + destinatario);

                //controllo se destinatario è tutti
                if (destinatario.equals("TUTTI")) {
                    inoltroBroadcast(nome + ": " + messaggio);
                } else {
                    out2.writeBytes(nome + ": " + messaggio + '\n');
                }
                
            } while (!messaggio.equals("/exit"));*/

            client.close();
            System.out.println(ANSI_RED + "Client" + client.getPort() + " disconnected" + ANSI_RESET + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClientFromName(String destinatario) {
        try {
            //controllo se vuoto
            if (clients.isEmpty()) {
                out1.writeBytes(ANSI_RED + "Errore: nessun altro host si è connesso" + ANSI_RESET + '\n');
                return null;
            }
            //fare controllino per lo stesso nome

            //controllo se destinatario è se stesso
            if (destinatario.equals(nome)) {
                out1.writeBytes(ANSI_RED + "Errore: non puoi inviare messaggi a te stesso :/" + ANSI_RESET + '\n');
                return null;
            }

            //controllo se destinatario esiste
            for (ClientHandler c : clients) {
                if (c.getNome().equals(destinatario)) {
                    return c.getClient();
                }
            }
            out1.writeBytes(ANSI_RED + "Errore: destinatario non trovato" + ANSI_RESET + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void inoltroBroadcast(String messaggio, String nome) { //da fixare: il destinatario riceve /tell @all
        try {
            for (ClientHandler c : clients) {
                if (c.getClient() != client) {
                    c.getout().writeBytes(ANSI_PURPLE + "[ALL] " + ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET + ": " + messaggio + '\n');
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkNome(String nome1) {
        try {
            for (ClientHandler c : clients) {
                if (c.getNome().equals(nome1)) {
                    out1.writeBytes(ANSI_RED + "Errore: nome già utilizzato" + ANSI_RESET + '\n');
                    out1.writeBytes("CODICE_ERRORE: 0001\n");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Socket getClient() {
        return client;
    }

    public String getNome() {
        return nome;
    }
    public DataOutputStream getout() {
        return out1;
    }
}
