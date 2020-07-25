package com.telegram.server;

import com.telegram.database.ClientsInfo;
import com.telegram.database.dao.UserDao;
import com.telegram.database.dao.UserFileDaoImpl;
import com.telegram.database.dao.MessageDao;
import com.telegram.database.dao.MessageFileDaoImpl;
import com.telegram.utility.Message;
import com.telegram.utility.Query;
import com.telegram.utility.*;
import com.telegram.utility.exception.NoSuchClientInServerMapException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * created by Alireza Asadi on 2018/6/28
 * */
public class ServerRunner {
    private static String serverIp = "localhost";
    private static String primaryServerPort = "78";// primary and text port
    private static String videoServerPort = "4050";// video port
    private int nThreads = 20;
    private ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    public ServerRunner(String _serverIp, String _primaryServerPort, String _videoServerPort) {
        serverIp = _serverIp;
        primaryServerPort = _primaryServerPort;
        videoServerPort = _videoServerPort;
    }

    public void startServer() {
        VideoServer videoServer = new VideoServer();
        videoServer.start();
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(primaryServerPort))) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientChores task = new ClientChores(socket);
                executorService.execute(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class VideoServer extends Thread {
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(videoServerPort))) {
                while (true) {
                    Socket videoSocket = serverSocket.accept();// video socket
                    System.out.println("video socket connected");
                    VideoDownloadRunner videoDownloadRunner = new VideoDownloadRunner(videoSocket);
                    videoDownloadRunner.setDaemon(true);
                    videoDownloadRunner.setName("videoDownloader serverThread");
                    videoDownloadRunner.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ServerRunner serverRunner = new ServerRunner("localhost", "78", "4050");
        UserDao userDao = new UserFileDaoImpl();
        MessageDao messageDao = new MessageFileDaoImpl();
        ClientsInfo.getInstance().setUserDao(userDao);
        ClientsInfo.getInstance().setMessageDao(messageDao);
        ClientsInfo.getInstance().readAllUsers();
        ClientsInfo.getInstance().readAllUnsentMessages();
        serverRunner.startServer();
    }
}

class VideoDownloadRunner extends Thread {
    private Socket videoSocket;
    private Client client;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public VideoDownloadRunner(Socket videoSocket) {
        this.videoSocket = videoSocket;
        try {
            output = new ObjectOutputStream(videoSocket.getOutputStream());
            input = new ObjectInputStream(videoSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("in constructor of server VideoDownloadRunner class" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void run() {
        Object queryObject;
        Query query = null;
        OutputStream o = null;
        try {
            queryObject = input.readObject();
            if (queryObject != null)
                query = (Query) queryObject;
            else
                return;
            if (query instanceof GetCachedMessagesQuery) {
                System.out.println("GetCachedMessagesQuery received");
                GetCachedMessagesQuery cachedMessagesQuery = (GetCachedMessagesQuery) query;
                if (ClientsInfo.getInstance().getUnsentMessages(cachedMessagesQuery.getUser()) != null) {
                    System.out.println("there is data in map");
                    Message[] messages = ClientsInfo.getInstance().getUnsentMessages(cachedMessagesQuery.getUser());
                    output.reset();
                    synchronized (output) {
                        output.writeObject(messages);
                    }
                    output.flush();
                }
            }
            if (query instanceof DownloadFileMessage) {
                DownloadFileMessage downloadMessage = (DownloadFileMessage) query;
                String fileName = downloadMessage.getData();
                File file = Paths.get("./src/data/archivedFiles/" + fileName).toAbsolutePath().toFile();
                InputStream i;
                if (!file.exists())
                    i = new FileInputStream(Paths.get("./src/data/archivedFiles/" + fileName).toAbsolutePath().toFile());
                else {
                    i = new FileInputStream(file);
                }
                int totalDownloaded = 0, read;
                byte[] buffer = new byte[2048];
                while ((read = i.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                    totalDownloaded += read;
                }
                output.writeObject(new DownloadFinishedMessage());
            }
            if (query instanceof FileMessage) {// file upload message
                System.out.println("FileUploadMessage received");
                FileMessage fileMessage = (FileMessage) query;
                ObjectInputStream inputStream = new ObjectInputStream(videoSocket.getInputStream());
                String from = fileMessage.getUser().getPhone_id();
                String to = fileMessage.getDestinationId();
                long totalFileSize = fileMessage.getData().length(); //First Of All Read File Size!!
                String sentFileName = fileMessage.getData().getName();
                String fileExtension = ".dat";
                if (sentFileName.contains("."))
                    fileExtension = sentFileName.substring(sentFileName.indexOf("."));
                String fileName = fileMessage.getData().getName() + "-length= " + totalFileSize + "-from= " + from + "-to= " + to + fileExtension;
                File file = Paths.get("./src/data/archivedFiles/" + fileName).toAbsolutePath().toFile();
                if (!file.exists())
                    o = new FileOutputStream(Paths.get("./src/data/archivedFiles/" + fileName).toAbsolutePath().toFile());
                else {
                    o = new FileOutputStream(file);
                }
                int totalDownloaded = 0, read;
                byte[] buffer = new byte[2048];
                System.out.println("FileUploadMessage writing");
                try {
                    while ((read = inputStream.read(buffer)) > 0) {
                        o.write(buffer, 0, read);
                        totalDownloaded += read;
                    }
                } catch (IOException e1) {
                    System.err.println(e1.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("exception");
        } finally {
            System.err.println("finally");
            System.err.println("reading server finished");
            if(query instanceof GetCachedMessagesQuery){
                ClientsInfo.getInstance().removeUnsentMessages(query.getUser());
            }
            if (query instanceof FileMessage) {
                FileMessage fileMessage = (FileMessage) query;
                System.err.println(ClientsInfo.getInstance().findClient(fileMessage.getDestinationId()).get());
                if (ClientsInfo.getInstance().findClient(fileMessage.getDestinationId()).isPresent() && !ClientsInfo.getInstance().findClient(fileMessage.getDestinationId()).get().getConnected())
                    ClientsInfo.getInstance().addUnsentMessage(ClientsInfo.getInstance().findClient(fileMessage.getDestinationId()).get(), fileMessage);
            }
            try {
                if (output != null)
                    output.close();// input and socket will be closed
                if (o != null)
                    o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void VideoMessageHandler() throws IOException, ClassNotFoundException {
//        query.getUser();
        Message<String> message = (Message<String>) input.readObject();// get information(phoneId) from client
        String id = message.getData();
        if (ClientsInfo.getInstance().findClient(id).isPresent())
            client = (Client) ClientsInfo.getInstance().findClient(id).get();
//        client.setVideoSocket(videoSocket);// not necessary because I close socket after finishing

    }
}

class ClientChores implements Runnable {
    private final Client client;
    private ObjectInputStream primaryInput;
    private ObjectOutputStream primaryOutput;

    public ClientChores(Socket primarySocket) {
        client = new Client(primarySocket);
        primaryOutput = client.getOutput();
        primaryInput = client.getInput();

    }

    @Override
    public void run() {
        Object queryObject;
        Query query;
        while (true) {
            try {
                queryObject = primaryInput.readObject();
                if (queryObject == null) {
                    System.out.println("continued");
                    continue;
                } else {
                    query = (Query) queryObject;
                }

                if (query instanceof SignInQuery) {
                    handleSignInQuery((SignInQuery) query);
                } else if (query instanceof RegisterQuery) {
                    handleRegisterQuery((RegisterQuery) query);
                } else if (query instanceof TextMessage) {
                    handleTextMessage((TextMessage) query);
                } else if (query instanceof GetUsersListQuery) {
                    handleGetUsersListQuery((GetUsersListQuery) query);
                }
                if (query instanceof AddContactQuery) {
                    handleAddContactQuery((AddContactQuery) query);
                }
                if (query instanceof FileMessage) {
                    handleFileMessage((FileMessage) query);
                }
                if (query instanceof FileMessage) {
                    handleFileMessage((FileMessage) query);
                }
                if(query instanceof GetContactsQuery){
                    handleGetContactQuery((GetContactsQuery)query);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("in serverClientChores-run-method: " + e.getMessage() + " == socket closed from client side!");
                try {
                    client.getOutput().close();
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void handleGetContactQuery(GetContactsQuery getContactsQuery) {
        try {
            primaryOutput.reset();
            synchronized (client.getOutput()){
                if(ClientsInfo.getInstance().getContacts(getContactsQuery.getUser()) != null){
                    primaryOutput.writeObject(new GetContactsAnswer(ClientsInfo.getInstance().getContacts(getContactsQuery.getUser())));
                    primaryOutput.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFileMessage(FileMessage fileMessage) {
        try {
            primaryOutput.reset();
            synchronized (client.getOutput()) {
                primaryOutput.writeObject(new FileMessage());// send answer to the sender
                Client destination;
                if(ClientsInfo.getInstance().findClient(fileMessage.getDestinationId()).isPresent()) {
                    if (ClientsInfo.getInstance().findClient(fileMessage.getDestinationId()).get().getConnected())
                        destination = (Client) ClientsInfo.getInstance().findClient(fileMessage.getDestinationId()).get();
                    else
                        return;
                }else return;
                destination.getOutput().writeObject(fileMessage);
            }
            primaryOutput.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAddContactQuery(AddContactQuery addContactQuery) {
        try {
            User userToBeAdded = addContactQuery.getUserToBeAdded();
            primaryOutput.reset();
            if (ClientsInfo.getInstance().findClient(userToBeAdded.getPhone_id()).isPresent()) {
                ClientsInfo.getInstance().addContact(addContactQuery.getUser(), userToBeAdded);
                synchronized (client.getOutput()) {
                    primaryOutput.writeObject(new AddContactAnswer("successful-add-contact"));
                }
            } else {
                synchronized (client.getOutput()) {
                    primaryOutput.writeObject(new AddContactAnswer("failed-add-contact"));
                }
            }
            primaryOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGetUsersListQuery(GetUsersListQuery getUsersListQuery) {
        try {
            primaryOutput.reset();
            synchronized (client.getOutput()) {
                Map<String, User> existingUsersExceptMe = new ConcurrentHashMap<>(ClientsInfo.clients);
                existingUsersExceptMe.remove(getUsersListQuery.getUser().getPhone_id());
                primaryOutput.writeObject(new GetUsersListAnswer(existingUsersExceptMe));
            }
            primaryOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleTextMessage(TextMessage messageQuery) {
        try {
            String destinationID = messageQuery.getDestinationId();
            String text = messageQuery.getText();
            User destinationUser = null;
            if (destinationID != null && ClientsInfo.getInstance().findClient(destinationID).isPresent())
                destinationUser = ClientsInfo.getInstance().findClient(destinationID).get();
            System.out.println(text);
//            Message<String> message = (Message<String>) primaryInput.readObject();
//            if(message.getData().equalsIgnoreCase("textMessage finished"))
            if (destinationUser == null)
                throw new NoSuchClientInServerMapException("NoSuchClient In Server map");
            if (!destinationUser.getConnected()) {
                System.out.println("destination is not connected");
                ClientsInfo.getInstance().addUnsentMessage(destinationUser, messageQuery);
            } else {
                System.out.println("destination is connected.. I'm on my way sending message!");
                final Client destinationClient = (Client) destinationUser;// destinationUser is definitely a client because it is online
                synchronized (destinationClient.getOutput()) {
                    destinationClient.getOutput().reset();
                    destinationClient.getOutput().writeObject(new TextMessage(text, client));
                    destinationClient.getOutput().flush();
                }
            }
            primaryOutput.reset();
            primaryOutput.writeObject(new TextMessageSenderAnswer("send-Text-Message-succeeded"));
            primaryOutput.flush();
        } catch (IOException | NoSuchClientInServerMapException e) {
            e.printStackTrace();
            if (e instanceof NoSuchClientInServerMapException) {
                try {
                    primaryOutput.reset();
                    primaryOutput.writeObject(new TextMessageSenderAnswer("send-Text-Message-failed"));
                    primaryOutput.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void handleSignInQuery(SignInQuery signInQuery) {
        try {
            System.err.println("signIn");
            client.setUsername(signInQuery.getUser().getUsername());
            client.setPassword(signInQuery.getUser().getPassword());
            client.setPhone_id(signInQuery.getUser().getPhone_id());
            primaryOutput.reset();
            synchronized (client.getOutput()) {
                primaryOutput.writeObject(new SignInAnswer("loginQuery received"));
            }
            primaryOutput.flush();
            System.err.println("signIn sent");
            primaryOutput.reset();

            if (!ClientsInfo.getInstance().findClient(signInQuery.getUser().getPhone_id()).isPresent()
                    || !ClientsInfo.getInstance().validateUserAndPass(signInQuery.getUser().getUsername(), signInQuery.getUser().getPassword(), signInQuery.getUser().getPhone_id())) {
                System.out.println("in SignInQuery of server no client with such properties found!! or incompatible password!!");
                synchronized (client.getOutput()) {
                    primaryOutput.writeObject(new Message<>("Failed"));
                }
            } else {
                User usr = ClientsInfo.getInstance().findClient(signInQuery.getUser());
                client.setFirstName(usr.getFirstName());
                client.setLastName(usr.getLastName());
                System.err.println(client);
                client.setConnected(true);
                client.setLastConnection(LocalDateTime.now());
                ClientsInfo.getInstance().addClient(client);// replace
                synchronized (client.getOutput()) {
                    primaryOutput.writeObject(new Message<>("SignedIn"));
                }
            }
            primaryOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRegisterQuery(RegisterQuery registerQuery) {
        try {
            primaryOutput.reset();
            synchronized (client.getOutput()) {
                primaryOutput.writeObject(new RegisterAnswer("registerQuery received"));
            }
            primaryOutput.flush();
            System.err.println("in register: " + registerQuery.getUser());
            primaryOutput.reset();
            System.err.println(ClientsInfo.getInstance().findClient(registerQuery.getUser()));
            if (ClientsInfo.getInstance().UserExists(registerQuery.getUser())) {
                System.out.println("in RegisterQuery of server clients with such properties found!!");
                synchronized (client.getOutput()) {
                    primaryOutput.writeObject(new Message<>("Failed"));
                }
            } else {
                ClientsInfo.getInstance().addClient(registerQuery.getUser());
                synchronized (client.getOutput()) {
                    primaryOutput.writeObject(new Message<>("Registered"));
                }
            }
            primaryOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
