package com.telegram;

import com.telegram.utility.Client;
import com.telegram.utility.FileUploadMessage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

public class FileSendingTest {
    public static void main(String[] args) {
        Socket socket;

        {
            try {
                socket = new Socket("localhost", 4050);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                Client client = new Client();
                client.setPhone_id("091234");
                String dst = "2134";
                File file = Paths.get("C:\\Users\\Iran Mobile\\Desktop/Bazaar.jpg").toFile();
                out.writeObject(new FileUploadMessage(client, dst, file));
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                FileInputStream i = new FileInputStream(file);
//            outputStream.writeLong(file.length()); //First Of All Send FileSize!!
                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read = i.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
