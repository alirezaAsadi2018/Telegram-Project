package com.telegram;


import com.telegram.database.ClientsInfo;
import com.telegram.utility.Client;
import com.telegram.utility.User;
import org.junit.Test;

import java.io.IOException;

public class ClientsInfoTest {
    @Test
    public void findClientTest() {
//        Socket socket = new Socket();
//        Socket socket1 = new Socket();
//        System.out.println(socket.hashCode());
//        System.out.println(socket1.hashCode());
//        Client client = new Client(socket);
//        client.setPhone_id("123");
//        Client clientTheSame = new Client(socket);
//        clientTheSame.setPhone_id("67");
//        Client clientDifferent = new Client(socket1);
//        clientDifferent.setPhone_id("45");
        System.out.println(ClientsInfo.getInstance().findClient("234").isPresent());
        if (ClientsInfo.getInstance().findClient("234").isPresent())
            System.out.println(ClientsInfo.getInstance().findClient("234").get());
        try {
            Client client1 = new Client("ali", "asa", "username", "password", false, null, null, "0912");
            ClientsInfo.getInstance().addClient(client1);
            Client client2 = new Client("ali", "asa", "username", "password", false, null, null, "0912");
            User user = new User("aali", "asa", null, null, false, "0912");
            System.out.println(ClientsInfo.getInstance().findClient(client1));
            System.out.println(ClientsInfo.getInstance().findClient(client2));
            System.out.println(ClientsInfo.getInstance().findClient(user));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ClientsInfofo.getInstance().addClient(client1);
//        Client client = ClientsInfo.getInstance().findClient("username", "password");
//        boolean result = ClientsInfo.getInstance().validateUserAndPass("username", "password");
//
//        System.out.println(result);
//        System.out.println(client);
    }
}
