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

            

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
