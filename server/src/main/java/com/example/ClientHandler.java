package com.example;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    String ANSI_GREEN = "\u001B[32m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_RESET = "\u001B[0m";

    private Socket client;
    private Socket client2;
    private BufferedReader in1;
    private DataOutputStream out1;
    private BufferedReader in2;
    private DataOutputStream out2;
    ArrayList<ClientHandler> clients;
    String nome = "";
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

            //ricevo nome client
            nome = in1.readLine();
            System.out.println("Client" + client.getPort() + ": " + nome);

            //controllo se il nome è già stato usato
            for (ClientHandler c : clients) {
                if (c.getNome().equals(nome)) {
                    out1.writeBytes(ANSI_RED + "Errore: nome già utilizzato\n" + ANSI_RESET);
                    out1.writeBytes("CODICE_ERRORE: 0001\n");
                    return;
                }
            }

            do {
                //ricezione messaggio
                messaggio = in1.readLine();
                
                

                //controllo comandi ricevuti

                //comando /tell
                if (messaggio.contains("/tell")) {

                    //comando /tell @all
                    if (messaggio.contains("@all")) {
                        System.out.println("Client" + client.getPort() + ": " + messaggio + " -> " + "tutti");
                        inoltroBroadcast(messaggio);

                    //comando /tell @destinatario
                    } else if (messaggio.contains("@")){

                        //salvo destinatario client
                        destinatario = messaggio.substring(messaggio.indexOf("@") + 1, messaggio.indexOf("//"));
                        client2 = getClientFromName(destinatario);

                        if (client2 != null) {
                            in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
                            out2 = new DataOutputStream(client2.getOutputStream());
                            System.out.println("Client" + client.getPort() + ": " + messaggio + " -> " + destinatario);
                            out2.writeBytes(nome + ": " + messaggio.substring(messaggio.indexOf("//") + 2) + '\n');
                        }
                    } else {
                        out1.writeBytes(ANSI_RED + "Errore: comando non riconosciuto\n" + ANSI_RESET);
                    }
                } else if (messaggio.contains("/lista")) {
                    System.out.println("Client" + client.getPort() + ": " + messaggio);
                    out1.writeBytes(ANSI_GREEN + "Lista client connessi:\n" + ANSI_RESET);
                    for (ClientHandler c : clients) {
                        out1.writeBytes(ANSI_BLUE + c.getNome() + ANSI_RESET + '\n');
                    }
                } else if (messaggio.contains("/exit")) {
                    System.out.println("Client" + client.getPort() + ": " + messaggio);
                    out1.writeBytes(ANSI_RED + "Disconnessione in corso...\n" + ANSI_RESET);
                    break;
                } else {
                    out1.writeBytes(ANSI_RED + "Errore: comando non riconosciuto\n" + ANSI_RESET);
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
            System.out.println(ANSI_RED + "Client" + client.getPort() + " disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClientFromName(String destinatario) {
        try {
            //controllo se vuoto
            if (clients.isEmpty()) {
                out1.writeBytes(ANSI_RED + "Errore: nessun altro host si è connesso\n" + ANSI_RESET);
                return null;
            }
            //fare controllino per lo stesso nome

            //controllo se destinatario è se stesso
            if (destinatario.equals(nome)) {
                out1.writeBytes(ANSI_RED + "Errore: non puoi inviare messaggi a te stesso :/ \n" + ANSI_RESET);
                return null;
            }

            //controllo se destinatario esiste
            for (ClientHandler c : clients) {
                if (c.getNome().equals(destinatario)) {
                    return c.getClient();
                }
            }
            out1.writeBytes(ANSI_RED + "Errore: destinatario non trovato\n" + ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void inoltroBroadcast(String messaggio) {
        try {
            for (ClientHandler c : clients) {
                if (c.getClient() != client) {
                    c.getout().writeBytes(messaggio + '\n');
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
