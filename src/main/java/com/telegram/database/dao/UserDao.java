package com.telegram.database.dao;

import com.telegram.utility.User;
import com.telegram.utility.exception.NoSuchUserFoundException;

import java.util.List;
import java.util.Map;

public interface UserDao {
    Map<String, User> getAllUsers();

    User getUser(String phone_id) throws NoSuchUserFoundException;

    void addUser(String phone_id, User client);

    void updateUser(String phone_id, User client);

    void deleteUser(String phone_id);

    List<User> getUserContacts(String phone_id) throws NoSuchUserFoundException;
}
