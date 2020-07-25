package com.telegram.database.dao;

import com.telegram.utility.Message;
import com.telegram.utility.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

@RunWith(JUnit4.class)
public class MessageFileDaoImplTest extends TestCase {
    private static final Map<User, ArrayBlockingQueue<Message<?>>> messagesMap = new HashMap<>();
    private static final Map<User, ArrayBlockingQueue<Message<?>>> unsentMessagesMap = new HashMap<>();
    private static final List<User> users = new LinkedList<>();
    MessageDao messageDao = new MessageFileDaoImpl();
    SecureRandom random = new SecureRandom();

    @BeforeClass
    public static void init() {
        ArrayBlockingQueue<Message<?>> messages1 = new ArrayBlockingQueue<>(1000);
        ArrayBlockingQueue<Message<?>> messages2 = new ArrayBlockingQueue<>(1000);
        ArrayBlockingQueue<Message<?>> messages3 = new ArrayBlockingQueue<>(1000);
        messages1.add(new Message<>(List.of(12.3, 45.3, 12.4)));
        messages2.add(new Message<>("highly configurable. For instance, you may set the theme"));
        messages2.add(new Message<>("You need to be built to call your web application's API"));
        messages3.add(new Message<>(7));
        messages3.add(new Message<>(12));
        messages3.add(new Message<>(40));
        User user1 = new User("a", "b", "c", "d",
                false, "+981212343");
        User user2 = new User("firstName", "lastName", "username",
                "password", true, "phone_id");
        User user3 = new User("csa", "sad", "sd", "wad",
                false, "2134");
        messagesMap.put(user1, messages1);
        messagesMap.put(user2, messages2);
        messagesMap.put(user3, messages3);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        messages1 = new ArrayBlockingQueue<>(1000);
        messages1.add(new Message<>("watch this later!"));
        messages1.add(new Message<>("This is unsent"));
        unsentMessagesMap.put(user1, messages1);
        messages2 = new ArrayBlockingQueue<>(1000);
        messages2.add(new Message<>('I'));
        messages2.add(new Message<>('R'));
        messages2.add(new Message<>('A'));
        messages2.add(new Message<>('N'));
        unsentMessagesMap.put(user2, messages2);
    }

    @Before
    public void addAllUserMessages() {
        for (var entry : messagesMap.entrySet()) {
            messageDao.updateMessagesForUser(entry.getKey().getPhone_id(), entry.getValue());
        }
    }

    @After
    public void deleteAllUserMessages() {
        for (User user : messagesMap.keySet()) {
            messageDao.deleteMessagesForUser(user.getPhone_id());
        }
    }

    @Before
    public void addAllUnsentMessages() {
        messageDao.writeUnsentMessages(unsentMessagesMap);
    }

    @After
    public void deleteAllUnsentMessages() {
        messageDao.deleteUnsentMessages();
    }

    @Test
    public void testUpdateMessageForUser() {
        // get user with this index
        int index = random.nextInt(messagesMap.size());
        User user = users.get(index);

        // add this new message1
        Message<?> added = new Message<>(List.of("added", "this"));
        messageDao.updateMessageForUser(user.getPhone_id(), added);
        Set<Message<?>> messagesReturned = new HashSet<>(messageDao.getMessagesForUser(
                user.getPhone_id()));
        Set<Message<?>> messagesExpected = new HashSet<>(messagesMap.get(user));
        messagesExpected.add(added);
        assertEquals(messagesExpected, messagesReturned);

        // add this new message2
        added = new Message<>("added this");
        messageDao.updateMessageForUser(user.getPhone_id(), added);
        messagesReturned = new HashSet<>(messageDao.getMessagesForUser(
                user.getPhone_id()));
        messagesExpected.add(added);
        assertEquals(messagesExpected, messagesReturned);

        // add this new message3
        added = new Message<>(1234);
        messageDao.updateMessageForUser(user.getPhone_id(), added);
        messagesReturned = new HashSet<>(messageDao.getMessagesForUser(
                user.getPhone_id()));
        messagesExpected.add(added);
        assertEquals(messagesExpected, messagesReturned);
    }

    @Test
    public void testUpdateMessagesForUser() {
        // get user with this index
        int index = random.nextInt(messagesMap.size());
        User user = users.get(index);

        // add these 3 new messages
        Message<?> added1 = new Message<>(List.of("added", "this"));
        Message<?> added2 = new Message<>("added this");
        Message<?> added3 = new Message<>(1234);
        ArrayBlockingQueue<Message<?>> added = new ArrayBlockingQueue<>(100);
        added.add(added1);
        added.add(added2);
        added.add(added3);
        messageDao.updateMessagesForUser(user.getPhone_id(), added);
        Set<Message<?>> messagesReturned = new HashSet<>(messageDao.getMessagesForUser(
                user.getPhone_id()));
        Set<Message<?>> messagesExpected = new HashSet<>(messagesMap.get(user));
        messagesExpected.add(added1);
        messagesExpected.add(added2);
        messagesExpected.add(added3);
        assertEquals(messagesExpected, messagesReturned);
    }

    @Test
    public void testDeleteMessagesForUser(){
        // delete user with this index
        int index = random.nextInt(messagesMap.size());
        User user = users.get(index);

        messageDao.deleteMessagesForUser(user.getPhone_id());
        ArrayBlockingQueue<Message<?>> messagesReturned = messageDao.getMessagesForUser(user.getPhone_id());
        assertNull(messagesReturned);
    }

    @Test
    public void testGetUnsentMessages() {
        var unsentMessagesReturned = messageDao.getUnsentMessages();
        for (User user : unsentMessagesMap.keySet()) {
            HashSet<Message<?>> messagesReturned = new HashSet<>(unsentMessagesReturned.get(user));
            HashSet<Message<?>> messagesExpected = new HashSet<>(unsentMessagesMap.get(user));
            assertEquals(messagesExpected, messagesReturned);
        }
    }

    @Test
    public void testGetUnsentMessagesNotExists() {
        messageDao.deleteUnsentMessages();
        var unsentMessagesReturned = messageDao.getUnsentMessages();
        assertNull(unsentMessagesReturned);
    }

    @Test
    public void testUpdateUnsentMessages() {
        // update unsent for user with this index
        int index = random.nextInt(messagesMap.size());
        User user = users.get(index);

        var messages = new ArrayBlockingQueue<Message<?>>(1000);
        messages.add(new Message<>("This is added later!"));
        var addedMap = new HashMap<>(unsentMessagesMap);
        addedMap.put(user, messages);
        messageDao.updateUnsentMessages(addedMap);
        var unsentMessagesReturned = messageDao.getUnsentMessages();
        for (User u : addedMap.keySet()) {
            HashSet<Message<?>> messagesReturned = new HashSet<>(unsentMessagesReturned.get(u));
            HashSet<Message<?>> messagesExpected = new HashSet<>(addedMap.get(u));
            assertEquals(messagesExpected, messagesReturned);
        }
    }
}