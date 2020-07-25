package com.telegram.database.dao;

import com.telegram.utility.User;
import com.telegram.utility.exception.NoSuchUserFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class UserFileDaoImpl extends FileDao implements UserDao {

    public UserFileDaoImpl() {
        filesPath = configuration.getString("clientFilesPath");
    }

    private <T> T uncheckedCall(Callable<T> callable) {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, User> getAllUsers() {
        Path filePath = Path.of(filesPath);
        try {
            return Files.list(filePath).collect(Collectors.toMap(f -> f.getFileName().toString(),
                    f -> uncheckedGetUser(f.getFileName().toString())
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUser(String phone_id) throws NoSuchUserFoundException {
        // check if this user exists
        if (!fileExists(phone_id)) {
            throw new NoSuchUserFoundException("there is no user with this phone_id!");
        }
        Object obj = readFileById(phone_id);
        if (obj != null) {
            return (User) obj;
        } else {
            //TODO
            //Throw exceptions
        }
        return null;
    }

    private User uncheckedGetUser(String phone_id) {
        try {
            return getUser(phone_id);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addUser(String phone_id, User client) {
        writeFileById(phone_id, client);
    }

    @Override
    public void updateUser(String phone_id, User client) {
        //only update if the client already exists
        if (fileExists(phone_id)) {
            writeFileById(phone_id, client);
        }
    }

    @Override
    public void deleteUser(String phone_id) {
        deleteFileById(phone_id);
    }

    @Override
    public List<User> getUserContacts(String phone_id) throws NoSuchUserFoundException {
        return getUser(phone_id).getContacts();
    }
}
