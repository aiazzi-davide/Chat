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
    String ANSI_ORANGE = "\u001B[38;5;208m";
    String ANSI_RESET = "\u001B[0m";

    private Socket client;
    private Socket client2;
    private BufferedReader in1;
    private DataOutputStream out1;
    private DataOutputStream out2;
    ArrayList<ClientHandler> clients;
    String parts[];
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
                messaggio = in1.readLine().trim();
                //controllo messaggio
                parts = messaggio.split(" ", 3);
                    if (parts.length == 0) {
                        out1.writeBytes(ANSI_RED + "Errore: formato del comando non valido" + ANSI_RESET + '\n');
                        continue;
                    }
                //controllo comandi ricevuti
                //comando /tell
                    switch (parts[0]) {
                        case "/tell":
                            //comando /tell @all
                            //controllo se il messaggio è corretto
                            if (parts.length < 3) {
                                    out1.writeBytes(ANSI_RED + "Errore: formato del comando non valido" + ANSI_RESET + '\n');
                                    continue;
                                }
                            if (parts[1].equals("@all")) {
                                System.out.println(ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + parts[2] + ANSI_PURPLE + " -> " + ANSI_BLUE + "tutti" + ANSI_RESET);
                                inoltroBroadcast(parts[2], ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET);

                            //comando /tell @destinatario
                            } else if (parts[1].contains("@")){

                                //salvo destinatario client
                                
                                destinatario = parts[1].substring(1); // Rimuove il simbolo '@'
                                client2 = getClientFromName(destinatario);

                                if (client2 != null) {
                                    out2 = new DataOutputStream(client2.getOutputStream());
                                    System.out.println(ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + parts[2] + ANSI_PURPLE + " -> " + ANSI_BLUE + destinatario + ANSI_RESET);
                                    out2.writeBytes(ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + parts[2] + '\n');
                                }
                            } else {
                                out1.writeBytes(ANSI_RED + "Errore: sintassi non corretta: assicurati di aver inserito @ prima del nome" + ANSI_RESET + '\n');
                            }
                        break;

                        case "/lista":
                            //controllo se il messaggio è corretto
                            if (parts.length != 1) {
                                    out1.writeBytes(ANSI_RED + "Errore: formato del comando non valido" + ANSI_RESET + '\n');
                                    continue;
                                }

                            System.out.println(ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + messaggio);
                            out1.writeBytes(ANSI_GREEN + "Lista client connessi:" + ANSI_RESET + '\n');
                            for (ClientHandler c : clients) {
                                out1.writeBytes(c.getNome() + ANSI_RESET + '\n');
                            }
                        break;

                        case "/exit":
                            System.out.println(ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + messaggio);
                            clients.remove(this);
                            //out1.writeBytes("CODICE_ERRORE: 0001\n");
                            inoltroBroadcast(ANSI_BLUE + nome + ANSI_RESET + " ha lasciato la chat", ANSI_GREEN + "[SERVER]" + ANSI_RESET + '\n');
                            System.out.println(ANSI_ORANGE + "[" + nome + "]:" + " disconnected" + ANSI_RESET + "\n");
                            in1.close();
                            out1.close();
                            client.close();
                        break;

                        default:
                            out1.writeBytes(ANSI_RED + "Errore: comando non riconosciuto" + ANSI_RESET + '\n');
                        break;
                    }

            } while (!parts[0].equals("/exit"));  
            return;
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
    
    public void inoltroBroadcast(String messaggio, String nome) { 
        try {
            for (ClientHandler c : clients) {
                if (c.getClient() != client) {
                    c.getout().writeBytes(ANSI_PURPLE + "[ALL]" + nome + ANSI_RESET + ": " + messaggio + '\n');
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
                    out1.writeBytes("CODICE_ERRORE: 0002\n");
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
