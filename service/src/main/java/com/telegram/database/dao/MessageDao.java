package com.telegram.database.dao;

import com.telegram.utility.Message;
import com.telegram.utility.User;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public interface MessageDao {
    void updateMessageForUser(String userId, Message<?> message);

    void updateMessagesForUser(String userId, ArrayBlockingQueue<Message<?>> messages);

    ArrayBlockingQueue<Message<?>> getMessagesForUser(String userId);

    void deleteMessagesForUser(String userId);

    Map<User, ArrayBlockingQueue<Message<?>>> getUnsentMessages();

    void writeUnsentMessages(Map<User, ArrayBlockingQueue<Message<?>>> messagesMap);

    void updateUnsentMessages(Map<User, ArrayBlockingQueue<Message<?>>> messagesMap);

    void deleteUnsentMessages();
}
