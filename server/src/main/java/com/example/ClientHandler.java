package com.example;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler extends Thread{
    private Socket client;
    private Socket client2;
    private BufferedReader in1;
    private DataOutputStream out1;
    private BufferedReader in2;
    private DataOutputStream out2;
    ArrayList<ClientHandler> clients;
    String nome;
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

            //ricevo destinatario client
            String destinatario = in1.readLine();
            System.out.println("Client" + client.getPort() + ": " + destinatario);

            //inizializzo il client2
            client2 = getClientFromName(destinatario);

            if (client2 != null) {
                in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
                out2 = new DataOutputStream(client2.getOutputStream());
            }
            
            //ricevo messaggio client
            String messaggio = in1.readLine();
            System.out.println("Client" + client.getPort() + ": " + messaggio);

            //controllo se destinatario è tutti
            if (destinatario.equals("TUTTI")) {
                inoltroBroadcast(nome + ": " + messaggio);
            } else {
                out2.writeBytes(nome + ": " + messaggio + '\n');
            }
            
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClientFromName(String destinatario) {
        try {
            //controllo se vuoto
            if (clients.isEmpty()) {
                out1.writeBytes("nessun altro host si è connesso\n");
                return null;
            }

            //controllo se destinatario esiste
            for (ClientHandler c : clients) {
                if (c.getNome().equals(destinatario)) {
                    return c.getClient();
                }
            }
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
