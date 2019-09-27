package com.telegram;

import com.telegram.utility.Client;
import com.telegram.utility.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileWritingTest implements Serializable {
    public static Map<String, User> clientsData = new ConcurrentHashMap<>();

    public static void addClientHere(User client) {
        clientsData.put(client.getPhone_id(), client);
    }

    public static void main(String[] args) {
        Client client = null;
        try {
            client = new Client("firstname", "lastname", "username", "password", false, null, null, "phone_id");
        } catch (IOException e) {
            e.printStackTrace();
        }
        User user = new User(client.getFirstName(), client.getLastName(), client.getUsername(), client.getPassword(), false, client.getPhone_id());
        addClientHere(client);
        try {
            ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(Paths.get("./src/Test/file")));
            output.writeObject(clientsData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
