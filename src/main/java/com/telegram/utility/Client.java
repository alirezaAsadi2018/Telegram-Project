package com.telegram.utility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends User {
    static final long serialVersionUID = 9877;
    private transient ClientState status = ClientState.offline;
    private static int nThreads = 20;
    private static ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
    private String serverIp = "172.20.174.125";
    private String serverPort = "78";
    private transient Socket primarySocket;
    private transient Socket textSocket;
    private transient Socket videoSocket;
    private transient Socket imageSocket;
    private transient Socket musicSocket;
    private transient ObjectInputStream input;
    private transient volatile ObjectOutputStream output;

    public Client(String firstName, String lastName, String username, String password, boolean isConnected, String serverIp, String serverPort, String phone_id) throws IOException {
        super(firstName, lastName, username, password, isConnected, phone_id);
        if(serverIp != null)
            this.serverIp = serverIp;
        if(serverPort != null)
            this.serverPort = serverPort;
        primarySocket = new Socket(this.serverIp, Integer.parseInt(this.serverPort));
        output = new ObjectOutputStream(primarySocket.getOutputStream());
        input = new ObjectInputStream(primarySocket.getInputStream());
        this.isConnected = false;
        this.phone_id = phone_id;
//			groupActiveChat = false;
//			activeChatId = null;
    }
    public Client(String serverIp, String serverPort, String username, String password, String phone_id) throws IOException {
        this(null, null, username, password, false, serverIp, serverPort, phone_id);

    }
    public Client(Client client) throws IOException {
        this(client.firstName, client.lastName, client.username, client.password, client.isConnected, client.serverIp, client.serverPort, client.phone_id);
    }
    public Client(User user, boolean isConnected, String serverIp, String serverPort) throws IOException {
        this(user.firstName, user.lastName, user.username, user.password, isConnected, serverIp, serverPort, user.phone_id);
    }
    public Client(Socket primarySocket){
        this.primarySocket = primarySocket;
        try {
            output = new ObjectOutputStream(primarySocket.getOutputStream());
            input = new ObjectInputStream(primarySocket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public Client(){

    }

    @Override
    public void run() {

    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    public ClientState getStatus() {
        return status;
    }

    public void setStatus(ClientState status) {
        this.status = status;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }
    public Socket getPrimarySocket() {
        return primarySocket;
    }
    public String getServerIp() {
        return serverIp;
    }
    public String getServerPort() {
        return serverPort;
    }
    public void setSocket(Socket socket) {
        this.primarySocket = socket;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
    public Socket getVideoSocket() {
        return videoSocket;
    }

    public void setVideoSocket(Socket videoSocket) {
        this.videoSocket = videoSocket;
    }

    public Socket getImageSocket() {
        return imageSocket;
    }

    public void setImageSocket(Socket imageSocket) {
        this.imageSocket = imageSocket;
    }

    public Socket getMusicSocket() {
        return musicSocket;
    }

    public void setMusicSocket(Socket musicSocket) {
        this.musicSocket = musicSocket;
    }
    public Socket getTextSocket() {
        return textSocket;
    }

    public void setTextSocket(Socket textSocket) {
        this.textSocket = textSocket;
    }
    @Override
    public String toString() {
        return firstName + " " + lastName + " " + username + " " + password + " " + phone_id + " " + getLastConnection();
    }
}
