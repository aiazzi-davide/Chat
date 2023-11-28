package com.example;

public class Main {
    public static void main(String[] args) {
        client client = new client("localhost", 4000);
        client.start();
    }
}