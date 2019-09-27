package com.telegram;

import com.telegram.utility.Client;
import com.telegram.utility.SignInQuery;
import com.telegram.utility.User;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException {
//        Client client = new Client(null, null, "username", "password", null);
//        System.out.println(Thread.currentThread().getId());
//        Socket socket = new Socket("localhost", 78);
//        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
//        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        User user = new User("username", "password");
        Client client = new Client(user, false, null, null);
        System.out.println("user created");
        try {
            client.getOutput().writeObject(new SignInQuery(user));
        } catch (IOException e) {
            System.out.println("in FirstPageController-signIn-method: " + e.getMessage());
        }
        Scanner scanner = new Scanner(client.getInput());
        PrintStream formatter = new PrintStream(client.getOutput());
        formatter.format("hi! dear server\n");
        formatter.flush();
        System.out.println("message sent");
        System.out.println(scanner.nextLine());
    }
}
