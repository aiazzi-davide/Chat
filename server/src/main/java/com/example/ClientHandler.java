package com.example;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;   

public class ClientHandler extends Thread {

    String ANSI_GREEN = "\u001B[32m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_YELLOW = "\u001B[33m";   
    String ANSI_PURPLE = "\u001B[35m";  
    String ANSI_ORANGE = "\u001B[38;5;208m";
    String ANSI_RESET = "\u001B[0m";
    String ANSI_CYAN_BOLD = "\033[1;36m";

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
    String list = "";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
    LocalDateTime now = LocalDateTime.now();  
    String encryptedMessage = "";
    String Secretkey = "1234567890123456";

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
                nome1 = decryptMessage(in1.readLine(), Secretkey);
                System.out.println(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_YELLOW + "[Client" + client.getPort() + "]" + ANSI_RESET + ": " + nome1);

            //controllo se il nome è già stato usato
            } while (checkNome(nome1));


            //salvo nome client
            nome = nome1;
            //invio a tutti che si è connesso un nuovo client
            inoltroBroadcast(ANSI_BLUE + nome + ANSI_RESET + " benvenuto nella chat!\n", ANSI_GREEN + "[SERVER]" + ANSI_RESET, true);
            System.out.println(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_GREEN + "Nuovo client connesso: " + ANSI_BLUE + nome + ANSI_RESET);

            do {
                //ricezione messaggio
                messaggio = decryptMessage(in1.readLine(), Secretkey);
                
                //controllo messaggio
                parts = messaggio.split(" ", 3);
                    if (parts.length == 0) {
                        encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: formato del comando non valido" + ANSI_RESET, Secretkey);
                        out1.writeBytes(encryptedMessage + '\n');
                        continue;
                    }
                //controllo comandi ricevuti
                //comando /tell
                    switch (parts[0]) {
                        case "/tell":
                            //comando /tell @all
                            //controllo se il messaggio è corretto
                            if (parts.length < 3) {
                                    encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: formato del comando non valido" + ANSI_RESET, Secretkey);
                                    out1.writeBytes(encryptedMessage + '\n');
                                    continue;
                                }
                            if (parts[1].equals("@all")) {
                                 //controllo se c'è un solo utente
                                if (clients.size() == 1) {
                                    encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: non puoi inviare messaggi perchè sei da solo nella chat :/" + ANSI_RESET, Secretkey);
                                    out1.writeBytes(encryptedMessage + '\n');
                                    continue;
                                }
                                else
                                {
                                    System.out.println(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + parts[2] + ANSI_PURPLE + " -> " + ANSI_BLUE + "tutti" + ANSI_RESET);
                                    inoltroBroadcast(parts[2],ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET, false);
                                }
                            //comando /tell @destinatario
                            } else if (parts[1].contains("@")){

                                //salvo destinatario client
                                
                                destinatario = parts[1].substring(1); // Rimuove il simbolo '@'
                                client2 = getClientFromName(destinatario);

                                if (client2 != null) {
                                    out2 = new DataOutputStream(client2.getOutputStream());
                                    System.out.println(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + parts[2] + ANSI_PURPLE + " -> " + ANSI_BLUE + destinatario + ANSI_RESET);
                                    encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + parts[2], Secretkey);
                                    out2.writeBytes(encryptedMessage + '\n');
                                }
                            } else {
                                encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: sintassi non corretta: assicurati di aver inserito @ prima del nome" + ANSI_RESET, Secretkey);
                                out1.writeBytes(encryptedMessage + '\n');
                            }
                        break;

                        case "/list":
                            //controllo se il messaggio è corretto
                            if (parts.length != 1) {
                                    encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: formato del comando non valido" + ANSI_RESET, Secretkey);
                                    out1.writeBytes(encryptedMessage + '\n');
                                    continue;
                                }
                            System.out.println(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + messaggio);
                            list = ANSI_GREEN + "Lista client connessi:, " + ANSI_BLUE;
                            for (ClientHandler c : clients) {
                                list += c.getNome() + ", ";
                            }
                            encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + list + ANSI_RESET , Secretkey);
                            out1.writeBytes(encryptedMessage + '\n');
                        break;

                        case "/exit":
                            System.out.println(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_YELLOW + "[" + nome + "]" + ANSI_RESET  + ": " + messaggio);
                            clients.remove(this);
                            inoltroBroadcast(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_BLUE + nome + ANSI_RESET + " ha lasciato la chat", ANSI_GREEN + "[SERVER]" + ANSI_RESET, false);
                            System.out.println(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_ORANGE + "[" + nome + "]"+ ANSI_RESET + ": " + ANSI_RED + " disconnected" + ANSI_RESET + "\n");
                            in1.close();
                            out1.close();
                            client.close();
                        break;

                        default:
                            encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: comando non riconosciuto" + ANSI_RESET, Secretkey);
                            out1.writeBytes(encryptedMessage + '\n');
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
                encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: nessun altro host si è connesso" + ANSI_RESET, Secretkey);
                out1.writeBytes(encryptedMessage + '\n');
                return null;
            }

            //controllo se destinatario è se stesso
            if (destinatario.equals(nome)) {
                encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: non puoi inviare messaggi a te stesso :/" + ANSI_RESET, Secretkey);
                out1.writeBytes(encryptedMessage + '\n');
                return null;
            }

            //controllo se destinatario esiste
            for (ClientHandler c : clients) {
                if (c.getNome().equals(destinatario)) {
                    return c.getClient();
                }
            }
            //controllo se c'è un solo utente
            if (clients.size() == 1) {
                encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: non puoi inviare messaggi perchè sei da solo nella chat :/" + ANSI_RESET, Secretkey);
                out1.writeBytes(encryptedMessage + '\n');
                return null;
            }
            encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: destinatario non trovato" + ANSI_RESET, Secretkey);
            out1.writeBytes(encryptedMessage + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void inoltroBroadcast(String messaggio, String nome, boolean self) { 
        try {
            encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_PURPLE + "[ALL]" + nome + ANSI_RESET + ": " + messaggio, Secretkey);
            for (ClientHandler c : clients) {
                if (c.getClient() != client) {
                    c.getout().writeBytes(encryptedMessage + '\n');
                }
                
            }
            if (self) {
                    out1.writeBytes(encryptedMessage + '\n');
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkNome(String nome1) {
        try {
            
            for (ClientHandler c : clients) {
                if (c.getNome().equals(nome1)) {
                    encryptedMessage = encryptMessage(ANSI_CYAN_BOLD +"["+ dtf.format(now) + "]" + ANSI_RESET + ANSI_RED + "Errore: nome già utilizzato" + ANSI_RESET, Secretkey);
                    out1.writeBytes(encryptedMessage + '\n');
                    encryptedMessage = encryptMessage("CODICE_ERRORE: 0002", Secretkey);
                    out1.writeBytes(encryptedMessage + "\n");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //metodo per decriptare il messaggio
    public String decryptMessage(String encryptedMessage, String secretKey) {
        try{
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
        decryptedMessageBytes = cipher.doFinal(decryptedMessageBytes);
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
         } catch (Exception e) {
            System.out.println("Errore di decriptazione");
            return null;
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
