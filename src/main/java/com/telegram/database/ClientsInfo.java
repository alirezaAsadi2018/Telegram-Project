package com.telegram.database;

import com.telegram.database.dao.UserDao;
import com.telegram.database.dao.MessageDao;
import com.telegram.utility.Client;
import com.telegram.utility.Message;
import com.telegram.utility.User;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class ClientsInfo implements Serializable {
    public static final int MAX_USERS = 1000;
    public static final int MAX_MESSAGES = 1000;
    private static volatile ClientsInfo instance;
    private static final Object mutex = new Object();
    public static volatile Map<String, User> clients = new ConcurrentHashMap<>();
    public static volatile Map<User, ArrayBlockingQueue<Message<?>>> unsentMessages =
            new ConcurrentHashMap<>();
//    public static volatile Map<User, ArrayBlockingQueue<User>> contacts = new ConcurrentHashMap<>();
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    private MessageDao messageDao;
    private UserDao userDao;

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

    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void readAllUsers() {
        clients = userDao.getAllUsers();
        System.out.println("clients' map: " + clients);
        for (User user : clients.values()) {
            user.setConnected(false);
        }
    }

    public void readAllUnsentMessages() {
        unsentMessages = messageDao.getUnsentMessages();
        System.out.println("cachedMessages: " + unsentMessages);
    }

    public void removeUnsentMessages(User user){
        unsentMessages.remove(user);
        messageDao.writeUnsentMessages(unsentMessages);
    }

    public Message<?>[] getUnsentMessages(User user){
        if(unsentMessages.get(user) == null) {
            // TODO
            // take correct action in UI
            System.out.println("no cached messages found for this user!");
            return null;
        }
        return unsentMessages.get(user).toArray(new Message[0]);
    }

    public void addUnsentMessage(User user, Message<?> message){
        if (!unsentMessages.containsKey(user) || unsentMessages.get(user) == null) {
            ArrayBlockingQueue<Message<?>> value = new ArrayBlockingQueue<>(MAX_USERS);
            value.add(message);
            unsentMessages.put(user, value);
        } else{
            unsentMessages.get(user).add(message);
        }
        messageDao.updateMessageForUser(user.getPhone_id(), message);
        System.out.println("cachedMessages: " + unsentMessages);
    }

    public void addContact(User user, User userToBeAdded) {
        // this methods only adds contacts to the users who are in the clientsMap
        if(clients.containsKey(user.getPhone_id())){
            if(clients.containsKey(userToBeAdded.getPhone_id())) {
                clients.get(user.getPhone_id()).addContact(userToBeAdded);
            }else{
                // TODO
                // userToBeAdded is not in the clients list
                // take correct action in UI
                System.out.println("user you want to add in ur contacts is not using this app!");
            }
        }
        System.out.println("user new contacts: " + user.getContacts());
    }

    public List<User> getContacts(User user) {
        return user.getContacts();
    }

    public void addClient(Client client) {
        System.err.println("adding client: " + client);
        clients.put(client.getPhone_id(), client);
    }

    public void addClient(User user) {
        System.err.println("adding user: " + user);
        clients.put(user.getPhone_id(), user);
        userDao.addUser(user.getPhone_id(), user);
    }

    public boolean validateUserAndPass(String username, String password, String phone_id) {
        User user = findClient(username, password, phone_id);
        return user != null;
    }

    public User findClient(String username, String password, String phone_id) {
        return forkJoinPool.invoke(new DataRunner(new User(null, null, username,
                password, false, phone_id)));
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
        return Optional.ofNullable(clients.get(id));
    }

    public boolean UserExists(User user) {
        for (User other : clients.values()) {
            if(user.equals(other))
                return true;
        }
        return false;
    }
}
