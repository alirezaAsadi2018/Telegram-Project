package com.telegram.database.dao;

import com.telegram.utility.User;
import com.telegram.utility.exception.NoSuchUserFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.SecureRandom;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class ClientFileDaoImplTest {
    private static final List<User> users = new LinkedList<>();
    UserDao userDao = new UserFileDaoImpl();
    SecureRandom random = new SecureRandom();

    @BeforeClass
    public static void init() {
        User user1 = new User("a", "b", "c", "d",
                false, "+981212343");
        User user2 = new User("firstName", "lastName", "username",
                "password", true, "phone_id");
        User user3 = new User("csa", "sad", "sd", "wad",
                false, "2134");
        user1.addContact(user2);
        user1.addContact(user3);
        users.add(user1);
        user2.addContact(user1);
        users.add(user2);
        user3.addContact(user1);
        user3.addContact(user2);
        users.add(user3);
    }

    @Before
    public void addAllUsers() {
        for (User user : users) {
            userDao.addUser(user.getPhone_id(), user);
        }
    }

    @After
    public void deleteAllUsers() {
        for (User user : users) {
            userDao.deleteUser(user.getPhone_id());
        }
    }

    @Test
    public void testGetAllUsers() {
        Collection<User> usersReturned = userDao.getAllUsers().values();
        assertEquals(usersReturned.size(), users.size());
        // because order of the values of the map returned by 'getAllUsers' maybe different
        // from 'users', we will convert them to a set and then compare them
        assertEquals(new HashSet<>().addAll(users), new HashSet<>().addAll(usersReturned));
    }

    @Test
    public void testGetUser() throws NoSuchUserFoundException {
        // get user with this index
        int index = random.nextInt(users.size());
        String phone_id = users.get(index).getPhone_id();
        User userReturned = userDao.getUser(phone_id);
        User userExpected = users.get(index);
        assertEquals(userExpected, userReturned);
    }

    @Test
    public void testGetUserNotExists() {
        // get a user who is not added
        String phone_id = "";
        User userReturned = null;
        try {
            userReturned = userDao.getUser(phone_id);
            fail();
        } catch (NoSuchUserFoundException e) {
            // test passed
        }
    }

    @Test
    public void testUpdateUser() {
        // update user with this id
        String phone_id = users.get(random.nextInt(users.size())).getPhone_id();
        User updatedUser = new User("a2", "b2", "c2", "d2",
                false, phone_id);
        userDao.updateUser(phone_id, updatedUser);
        Map<String, User> usersMap = userDao.getAllUsers();
        Optional<User> optional = usersMap.values().stream().filter(u -> u.getPhone_id().equals(phone_id)).
                findFirst();
        User userReturned = null;
        if (optional.isPresent()) {
            userReturned = optional.get();
        }
        assertEquals(updatedUser, userReturned);
    }

    @Test
    public void testDeleteUser() {
        // delete user with this index
        int index = random.nextInt(users.size());
        String phone_id = users.get(index).getPhone_id();
        userDao.deleteUser(phone_id);
        try {
            userDao.getUser(phone_id);
            fail();
        } catch (NoSuchUserFoundException e) {
            // test passed -> deleted user successfully
            // check clients' list size
            assertEquals(users.size() - 1, userDao.getAllUsers().values().size());
        }
    }

    @Test
    public void testGetUserContacts() throws NoSuchUserFoundException {
        // get contacts of the user with this index
        int index = random.nextInt(users.size());
        String phone_id = users.get(index).getPhone_id();
        List<User> contactsReturned = userDao.getUserContacts(phone_id);
        assertEquals(new HashSet<>(users.get(index).getContacts()), new HashSet<>(contactsReturned));
    }

    @Test
    public void testGetUserNotExistsContacts() {
        // get contacts of the user with this index
        String phone_id = "";
        try {
            userDao.getUserContacts(phone_id);
            fail();
        } catch (NoSuchUserFoundException e) {
            // test passed -> got user contacts successfully
        }
    }
}