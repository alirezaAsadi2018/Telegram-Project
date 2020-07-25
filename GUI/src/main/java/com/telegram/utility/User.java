package com.telegram.utility;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

public class User extends Thread implements Serializable {
    public static final int MAX_CONTACTS = 100;
    static final long serialVersionUID = 9876;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected boolean isConnected;
    protected String phone_id; // phone number
    protected LocalDateTime lastConnection;
    protected ArrayBlockingQueue<User> contacts;

    public User() {

    }

    public User(String firstName, String lastName, String username, String password, boolean isConnected,
                String phone_id, List<User> contacts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isConnected = isConnected;
        this.phone_id = phone_id;
        if (contacts != null)
            this.contacts = new ArrayBlockingQueue<>(MAX_CONTACTS, true, contacts);
    }

    public User(String firstName, String lastName, String username, String password, boolean isConnected,
                String phone_id) {
        this(firstName, lastName, username, password, isConnected, phone_id, null);
    }

    public User(String username, String password) {
        this(null, null, username, password,
                false, null);
    }

    public User(User user) {
        this(user.username, user.password, user.firstName, user.lastName, user.isConnected,
                user.phone_id);
    }


    public String toString() {
        List<User> contactUsers = getContacts();
        List<String> contactIds = new LinkedList<>();
        if (contactUsers != null) {
            contactIds = contactUsers.stream().map(User::getPhone_id).collect(Collectors.
                    toList());
        }
        return firstName + " " + lastName + " " + username + " " + password +
                " " + phone_id + " " + contactIds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        return 43 + Objects.hashCode(username) + Objects.hashCode(password) + Objects.hashCode(phone_id);
    }

    @Override
    public boolean equals(Object obj) {
        User other;
        if (!(obj instanceof User))
            return false;
        other = (User) obj;
        return ((username == null || other.username == null || username.equals(other.username))
                && (password == null || other.password == null || password.equals(other.password))
                && (firstName == null || other.firstName == null || firstName.equals(other.firstName))
                && (lastName == null || other.lastName == null || lastName.equals(other.lastName))
                && (phone_id == null || other.phone_id == null || phone_id.equals(other.phone_id)));
    }

    public String getPhone_id() {
        return phone_id;
    }

    public void setPhone_id(String phone_id) {
        this.phone_id = phone_id;
    }

    public boolean getConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getContacts() {
        if (contacts != null) {
            return List.of(contacts.toArray(new User[0]));
        }
        return null;
    }

    public void setContacts(List<User> contacts) {
        for (User contact : contacts) {
            addContact(contact);
        }
    }

    public void addContact(User user) {
        if (contacts == null)
            contacts = new ArrayBlockingQueue<>(MAX_CONTACTS, true);
        if (!contacts.contains(user))
            contacts.add(user);
    }

    public String getLastConnection() {
        if (lastConnection == null)
            return null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dateTimeFormatter.format(lastConnection);
    }

    public void setLastConnection(LocalDateTime lastConnection) {
        this.lastConnection = lastConnection;
    }
}
