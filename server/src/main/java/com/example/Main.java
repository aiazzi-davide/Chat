package com.example;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) {
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_RESET = "\u001B[0m";
    ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
        try {
            ServerSocket server = new ServerSocket(4000);
            System.out.println(ANSI_GREEN + "Server started" + ANSI_RESET);
            while (true) {
                ClientHandler thread = new ClientHandler(server.accept(), clients);
                thread.start();
                clients.add(thread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}