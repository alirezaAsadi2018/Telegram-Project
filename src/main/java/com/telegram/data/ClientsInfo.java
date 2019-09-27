package com.telegram.data;

import com.telegram.utility.Client;
import com.telegram.utility.Message;
import com.telegram.utility.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class ClientsInfo implements Serializable {
    private static volatile ClientsInfo instance;
    private static final Object mutex = new Object();
    public static volatile Map<String, User> clientsData = new ConcurrentHashMap<>();
    public static volatile Map<User, ArrayBlockingQueue<Message>> cachedMessages = new ConcurrentHashMap<>();
    public static volatile Map<User, ArrayBlockingQueue<User>> contacts = new ConcurrentHashMap<>();
    private ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());


    public static ClientsInfo getInstance() { // ASingleton (thread-safe and singleton)
        ClientsInfo result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new ClientsInfo();
            }
        }
        return result;
    }

    //    public boolean addUser(User user){
//        if(findClient(user) == null) {// search for the user
//            clientsData.put(user.getPhone_id(), user);
//            System.out.println(("successfully added " + "map size: " + clientsData.size()));
//            return true;
//        }
//        return false;
//    }
    public void removeCachedMessages(User user){
        cachedMessages.remove(user);
        writeCachedMessagesMapToFile();
    }
    public Message[] getCachedMessages(User user){

        if(cachedMessages.get(user) == null)
            return null;
        System.out.println("skipped return");
        return cachedMessages.get(user).toArray(new Message[0]);
    }
    public void addCachedMessage(User user, Message message){
        if (!cachedMessages.containsKey(user) || cachedMessages.get(user) == null) {
            ArrayBlockingQueue<Message> value = new ArrayBlockingQueue<>(100);
            value.add(message);
            cachedMessages.put(user, value);
        } else{
            cachedMessages.get(user).add(message);
        }
        writeCachedMessagesMapToFile();
        System.out.println("cachedMessages: " + cachedMessages);
    }
    public void addContact(User user, User userToBeAdded) {
        if (!contacts.containsKey(user) || contacts.get(user) == null) {
            ArrayBlockingQueue<User> value = new ArrayBlockingQueue<>(100);
            value.add(userToBeAdded);
            contacts.put(user, value);
        } else if (!contacts.get(user).contains(userToBeAdded)) {
            contacts.get(user).add(userToBeAdded);
        }
        writeClientsMapToFile();
        System.out.println("contacts map: " + contacts);
    }

    public User[] getContacts(User user) {
        return contacts.get(user).toArray(new User[0]);
    }

    public void addClient(Client client) {
        System.err.println(client);
        clientsData.put(client.getPhone_id(), client);
    }

    public void addClient(User user) {
        System.err.println(user);
        clientsData.put(user.getPhone_id(), user);
        writeClientsMapToFile();
    }

    public boolean validateUserAndPass(String username, String password, String phone_id) {
        User user = findClient(username, password, phone_id);
        System.out.println("size : " + clientsData.size());
        System.out.println(clientsData);
        return user != null;
    }

    public User findClient(String username, String password, String phone_id) {
        return forkJoinPool.invoke(new DataRunner(new User(null, null, username, password, false, phone_id)));
    }

    public User findClient(Client client) {
        return forkJoinPool.invoke(new DataRunner(client));
    }

    public User findClient(User user) {
        return forkJoinPool.invoke(new DataRunner(user));
    }

    public User findClient(Socket socket) {
        Client client = new Client(socket);
        return forkJoinPool.invoke(new DataRunner(client));
    }

    public Optional<User> findClient(String id) {
        return Optional.ofNullable(clientsData.get(id));
    }

    public void writeClientsMapToFile() {
        writeFile("clientsMap", (Serializable) clientsData);
        System.err.println("writing clientsMap");
    }

    public void readClientsMapFromFile() {
        Object object = readFile("clientsMap");
        if (object instanceof Map)
            clientsData = (Map<String, User>) object;
        System.out.println("clients' map: " + clientsData);
        for (User user : clientsData.values()) {
            user.setConnected(false);
        }
    }

    public void writeContactsMapToFile() {
        writeFile("contactsMap", (Serializable) contacts);
        System.err.println("writing contactsMap");
    }
    public void readContactsMapFromFile() {
        Object object = readFile("contactsMap");
        if (object instanceof Map)
            contacts = (Map<User, ArrayBlockingQueue<User>>) object;
        System.out.println("contacts' map: " + contacts);
    }
    public void writeCachedMessagesMapToFile() {
        writeFile("cachedMessages", (Serializable) cachedMessages);
        System.err.println("writing CachedMessages");
    }

    public void readCachedMessagesMapFromFile() {
        Object object = readFile("cachedMessages");
        if (object instanceof Map)
            cachedMessages = (Map<User, ArrayBlockingQueue<Message>>) object;
        System.out.println("cachedMessages: " + cachedMessages);
    }

    private Object readFile(String name) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream("./src/data/files/" + name))) {
            return input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeFile(String name, Serializable toBeWritten) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./src/data/files/" + name))) {
            output.writeObject(toBeWritten);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean UserExists(User user) {
        for (User other : clientsData.values()) {
            return ((user.getUsername() != null && other.getUsername() != null && user.getUsername().equals(other.getUsername()))
                    || (user.getPassword() != null && other.getPassword() != null && user.getPassword().equals(other.getPassword()))
//                    || (user.getFirstName() != null  && other.getFirstName() != null &&  user.getFirstName().equals(other.getFirstName()))
//                    || (user.getLastName() != null  && other.getLastName() != null &&  user.getLastName().equals(other.getLastName()))
                    || (user.getPhone_id() != null && other.getPhone_id() != null && user.getPhone_id().equals(other.getPhone_id())));
        }

        return false;
    }
}
