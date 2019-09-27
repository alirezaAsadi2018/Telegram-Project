package com.telegram.utility;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User extends Thread implements Serializable{
    static final long serialVersionUID = 9876;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected boolean isConnected;
    protected String phone_id; // phone number
    protected LocalDateTime lastConnection;

    public User(){

    }

    public User(String firstName, String lastName, String username, String password, boolean isConnected, String phone_id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isConnected = isConnected;
        this.phone_id = phone_id;
    }

    public User(String username, String password) {
        this(null, null, username, password, false, null);
    }
    public User(User user)
    {
        this(user.username, user.password, user.firstName, user.lastName, user.isConnected, user.phone_id);
    }


    public String toString()
    {
        return firstName + " " + lastName + " " + username + " " + password + " " + phone_id;
    }
    public String getUsername()
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }
    @Override
    public int hashCode() {
        return 43 + username.hashCode() + password.hashCode() + phone_id.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        User other;
        if(!(obj instanceof User))
            return false;
        other = (User)obj;
        return ((username == null || other.username == null || username.equals(other.username))
                && (password == null  || other.password == null ||  password.equals(other.password))
                && (firstName == null  || other.firstName == null ||  firstName.equals(other.firstName))
                && (lastName == null  || other.lastName == null ||  lastName.equals(other.lastName))
                && (phone_id == null  || other.phone_id == null ||   phone_id.equals(other.phone_id)));
    }
    public String getPhone_id() {
        return phone_id;
    }
    public void setPhone_id(String phone_id) {
        this.phone_id = phone_id;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setConnected(boolean connected) {
        isConnected = connected;
    }
    public boolean getConnected() {
        return isConnected;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastConnection(LocalDateTime lastConnection) {
        this.lastConnection = lastConnection;
    }
    public String getLastConnection() {
        if(lastConnection == null)
            return null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dateTimeFormatter.format(lastConnection);
    }
}
