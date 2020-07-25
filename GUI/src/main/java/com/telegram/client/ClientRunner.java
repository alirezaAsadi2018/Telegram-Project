package com.telegram.client;


import com.telegram.utility.Message;
import com.telegram.utility.Query;
import com.telegram.utility.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;

public class ClientRunner implements Serializable {
    private final static ConcurrentHashMap<User, ArrayBlockingQueue<Query>> cachedRequests = new ConcurrentHashMap<>();
    private static int nThreads = 20;
    private static ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
    private final Object lock;
    private final Exchanger<Object> exchanger;
    private final ArrayBlockingQueue<Query> requests = new ArrayBlockingQueue<>(20);
    private final Client client;

    public ClientRunner(Client client, Exchanger<Object> exchanger, Object lock) {
//        new Thread(this::handleCachedRequests).start();
        this.client = client;
        this.exchanger = exchanger;
        this.lock = lock;
        ClientRunnerSender senderTask = new ClientRunnerSender(client, requests);
        executorService.execute(senderTask);
        ClientRunnerReceiver recieverTask = new ClientRunnerReceiver(client, exchanger, lock);
        executorService.execute(recieverTask);
    }

    public static void setCachedRequests(User user, Query query) {
        if (cachedRequests.get(user) == null) {
            ArrayBlockingQueue<Query> requests = new ArrayBlockingQueue<>(100);
            requests.add(query);
            cachedRequests.put(user, requests);
        } else
            cachedRequests.get(user).add(query);
        System.out.println(cachedRequests);
    }

    private void handleCachedRequests() {
        while (cachedRequests.size() > 0) {
            if (client.getStatus() == ClientState.closedSocket)
                return;
            for (User user : cachedRequests.keySet()) {
                if (user.equals(client)) {
                    requests.addAll(cachedRequests.get(user));
                    break;
                }
            }
        }
    }

    public void sendFile(File file, String destinationId) {
        User user = new User(null, null, client.getUsername(), client.getPassword(), false, client.getPhone_id());
        FileMessage fileMessage = new FileMessage(user, destinationId, file);
        try {
            requests.put(fileMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String text, String destinationId) {
        User user = new User(null, null, client.getUsername(), client.getPassword(), false, client.getPhone_id());
        TextMessage textMessage = new TextMessage(user, text, destinationId);// doesn't need the user
        try {
            requests.put(textMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//            ObjectOutputStream objectOutputStream = connectingClients.get(client.getPhone_id()).getOutput();
//            objectOutputStream.reset();
//            objectOutputStream.writeObject(textMessage);
//            objectOutputStream.flush();
    }

    public void addContact(User userToBeAdded) {
        User user = new User(null, null, client.getUsername(), client.getPassword(), false, client.getPhone_id());
        try {
            requests.put(new AddContactQuery(user, userToBeAdded));
            System.out.println("add contacts request was added");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getMembers() {
        User user = new User(null, null, client.getUsername(), client.getPassword(), false, client.getPhone_id());
        try {
            requests.put(new GetUsersListQuery(user));
            System.out.println("get members request was added");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getContacts() {
        User user = new User(null, null, client.getUsername(), client.getPassword(), false, client.getPhone_id());
        try {
            requests.put(new GetContactsQuery(user));
            System.out.println("get contacts request was added");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void signMeIn() {
        User user = new User(null, null, client.getUsername(), client.getPassword(), false, client.getPhone_id());
        try {
            requests.put(new SignInQuery(user));
            System.out.println("sign in request was added");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void registerMe() {
        User user = new User(client.getFirstName(), client.getLastName(), client.getUsername(), client.getPassword(), false, client.getPhone_id());
        try {
            requests.put(new RegisterQuery(user));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class ClientRunnerReceiver implements Runnable {
    private final Client client;
    private final Exchanger<Object> exchanger;
    private final Object lock;
    private ObjectInputStream input;

    public ClientRunnerReceiver(Client client, Exchanger<Object> exchanger, Object lock) {
        this.client = client;
        this.exchanger = exchanger;
        this.lock = lock;
    }

    @Override
    public void run() {
        input = client.getInput();
        Object queryObject;
        Query query;
        while (true) {
            try {
                queryObject = input.readObject();
                if (queryObject == null)
                    continue;
                query = (Query) queryObject;
                if (query instanceof SignInAnswer) {
                    boolean result = signInHandler((SignInAnswer) query);
                    if (!result)
                        break;
                } else if (query instanceof RegisterAnswer) {
                    boolean result = registerHandler((RegisterAnswer) query);
                    if (!result)
                        break;
                } else if (query instanceof TextMessage) {// for receiving text message sent from another user
                    TextMessageHandler((TextMessage) query);
                } else if (query instanceof TextMessageSenderAnswer) {// for getting the result of message sent to the destination to the sender
                    TextMessageSenderAnswerHandler((TextMessageSenderAnswer) query);
                } else if (query instanceof GetUsersListAnswer) {
                    handleGetUsersListQuery((GetUsersListAnswer) query);
                } else if (query instanceof AddContactAnswer) {
                    handleAddContactQuery((AddContactAnswer) query);
                } else if (query instanceof FileMessage) {
                    handleFileMessage((FileMessage) query);
                } else if (query instanceof GetContactsAnswer) {
                    handleGetContactsAnswer((GetContactsAnswer) query);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                client.setStatus(ClientState.closedSocket);
                // server is disconnected
                // do sth
                break;
            }
        }
    }

    private void handleGetContactsAnswer(GetContactsAnswer getContactsAnswer) {
        try {
            User[] contacts = (User[]) getContactsAnswer.getData();
            System.err.println("in handler receiver: " + Arrays.toString(contacts));
            exchanger.exchange(contacts);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleFileMessage(FileMessage fileMessage) {
        System.out.println("FileMessage from server received");
        if (client.getPhone_id().equalsIgnoreCase(fileMessage.getDestinationId())) {
            try {
                exchanger.exchange(fileMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (lock) {
            lock.notifyAll();
        }
        //todo empty for now
    }

    private void TextMessageHandler(TextMessage textMessage) {
        String text = textMessage.getText();
        User from = textMessage.getUser();
        try {
            exchanger.exchange(textMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void TextMessageSenderAnswerHandler(TextMessageSenderAnswer textMessageSenderAnswer) {
        String str = textMessageSenderAnswer.getData();
        if (str.equalsIgnoreCase("send-Text-Message-failed")) {
            client.setStatus(ClientState.textMessageFailed);
        } else if (str.equalsIgnoreCase("send-Text-Message-succeeded")) {
            client.setStatus(ClientState.textMessageSent);
        }
        synchronized (lock) {
            lock.notifyAll();
            System.out.println("text message notified");
        }
    }

    private void handleAddContactQuery(AddContactAnswer addContactAnswer) {
        try {
            Message<String> message = addContactAnswer;
            String string = message.getData();

            synchronized (lock) {
                lock.notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleGetUsersListQuery(GetUsersListAnswer getUsersListAnswer) {
        try {
            Message<String> message;
            Map<String, User> clientsData = (Map<String, User>) getUsersListAnswer.getData();
            System.err.println("in handler receiver: " + clientsData);
            exchanger.exchange(clientsData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean signInHandler(Message<String> message) throws IOException, ClassNotFoundException {
        String str = message.getData();
        if (str.equals("loginQuery received")) {
            System.out.println("message got is: " + str);
            message = (Message<String>) input.readObject();
            str = message.getData();
            if (str.equals("SignedIn")) {
                System.out.println("signed in with joy!!");
                client.setStatus(ClientState.successfulSignIn);
                synchronized (lock) {
                    lock.notifyAll();
                }
                System.err.println("returned true");
                return true;
            } else if (str.equals("Failed")) {
                System.out.println("failed with joy!! " + client.hashCode());// not registered or wrong password
                client.setStatus(ClientState.failedSignIn);
                client.getOutput().close();
                synchronized (lock) {
                    lock.notifyAll();
                }
                System.err.println("returned false");
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean registerHandler(Message<String> message) throws IOException, ClassNotFoundException {
        String str = message.getData();
        if (str.equals("registerQuery received")) {
            message = (Message<String>) input.readObject();
            str = message.getData();
            if (str.equals("Registered")) {
                client.getOutput().close();
                client.setStatus(ClientState.successfulRegistration);
                synchronized (lock) {
                    lock.notifyAll();
                }
                return false;
            } else if (str.equals("Failed")) {
                client.getOutput().close();
                client.setStatus(ClientState.failedRegistration);
                synchronized (lock) {
                    lock.notifyAll();
                }
                return false; // available info
            }
        }
        return false;
    }
}

class ClientRunnerSender implements Runnable {
    private final ArrayBlockingQueue<Query> requests;
    private final Client client;

    public ClientRunnerSender(Client client, ArrayBlockingQueue<Query> requests) {
        this.client = client;
        this.requests = requests;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (client.getPrimarySocket().isClosed()) {
                    System.err.println("socket closed for client: " + client);
                    client.setStatus(ClientState.closedSocket);
                    break;
                }
                if (requests.size() > 0) {
                    Query query = requests.take();
                    if (query instanceof SignInQuery) {
                        System.out.println("sent signIn query");
                        client.getOutput().reset();
                        client.getOutput().writeObject(query);
                        client.getOutput().flush();
                    }
                    if (query instanceof RegisterQuery) {
                        System.out.println("sent register query");
                        client.getOutput().reset();
                        client.getOutput().writeObject(query);
                        client.getOutput().flush();
                    }
                    if (query instanceof TextMessage) {
                        System.out.println("TextMessage sent");
                        client.getOutput().reset();
                        client.getOutput().writeObject(query);
                        client.getOutput().flush();
//                        client.getOutput().reset();
//                        client.getOutput().writeObject(new Message<>("textMessage finished"));
//                        client.getOutput().flush();
                    }
                    if (query instanceof GetUsersListQuery) {
                        System.out.println("GetUsersListQuery was sent");
                        client.getOutput().reset();
                        client.getOutput().writeObject(query);
                        client.getOutput().flush();
                    }
                    if (query instanceof GetContactsQuery) {
                        System.out.println("GetContactsQuery was sent");
                        client.getOutput().reset();
                        client.getOutput().writeObject(query);
                        client.getOutput().flush();
                    }
                    if (query instanceof AddContactQuery) {
                        System.out.println("addContactQuery was sent");
                        client.getOutput().reset();
                        client.getOutput().writeObject(query);
                        client.getOutput().flush();
                    }
                    if (query instanceof FileMessage) {
                        System.out.println("FileMessage was sent");
                        client.getOutput().reset();
                        client.getOutput().writeObject(query);
                        client.getOutput().flush();
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                client.setStatus(ClientState.closedSocket);
                // server is disconnected
                // do sth
                break;
            }
        }
    }
}
