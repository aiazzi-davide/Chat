package com.example;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.xml.crypto.Data;
public class ClientHandler extends Thread{
    private Socket client;
    private BufferedReader in;
    private DataOutputStream out;
    ArrayList<ClientHandler> clients;
    String nome;
    public ClientHandler(Socket client, ArrayList<ClientHandler> clients){
        this.client = client;
        System.out.println("New client connected on port " + client.getPort());
        this.clients = clients;
    }
    public void run(){
        
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());

            //ricevo nome client
            nome = in.readLine();
            System.out.println("Client" + client.getPort() + ": " + nome);

            //ricevo destinatario client
            String destinatario = in.readLine();
            System.out.println("Client" + client.getPort() + ": " + destinatario);

            for (ClientHandler clientHandler : clients) {
                if(clientHandler.nome.equals(destinatario)){
                    
                    break;
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public String getClientFromName(String destinatario) {
        for (ClientHandler clientHandler : clients) {
                if(clientHandler.nome.equals(destinatario)){
                    return "null";
                }
            }
    }*/
    public Socket getClient(){
        return client;
    }
}
