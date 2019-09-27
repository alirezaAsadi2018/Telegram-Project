package com.telegram;

import com.telegram.utility.Client;
import com.telegram.utility.User;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileReadingTest implements Serializable{
    public static Map<String, User> clientsData = new ConcurrentHashMap<>();

    public static void addClientHere(User client){
        clientsData.put(client.getPhone_id(), client);
    }
    public static void main(String[] args) {
        try {
            ObjectInputStream output = new ObjectInputStream(new FileInputStream("./src/Test/file"));
             Object object = output.readObject();
             if (object instanceof Map)
                clientsData = (Map<String, User>) object;
            System.out.println(clientsData.get("phone_id") instanceof Client);
            System.out.println(((Client)clientsData.get("phone_id")).getPrimarySocket());
            System.out.println(clientsData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
