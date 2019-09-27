package com.telegram;


import com.telegram.utility.Client;
import com.telegram.utility.SignInQuery;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {
    String ip = "localhost";
    String port = "78";
    int nThreads = 20;

    public ServerTest(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }


    public static void main(String[] args) {
        ServerTest serverTest = new ServerTest("localhost", "78");
        try {
            serverTest.startServer();
        } catch (ClassNotFoundException e) {
            System.out.println("in main method of serverRunner: " + e.getMessage());
        }
    }

    private void startServer() throws ClassNotFoundException {
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(port))) {
            while (true) {
                Socket socket = serverSocket.accept();
                Client client = new Client(socket);
                Run task = new Run(client  /*(Client) data.ClientsInfo.getInstance().findClient(client)*/);
                executorService.execute(task);
                System.out.println("Thread newed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Run implements Runnable {
    private Client client;

    public Run(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(client.getInput());
        PrintStream formatter = new PrintStream(client.getOutput());
        while (true) {
            try {
                SignInQuery query = (SignInQuery) client.getInput().readObject();
                client.setUsername(query.getUser().getUsername());
                client.setPassword(query.getUser().getPassword());
                System.out.println("user and pass gotten");
                System.out.println(scanner.nextLine());
                formatter.format("hi! welcome dear client%n");
                formatter.flush();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}