package com.telegram.database.dao;

import com.telegram.database.ClientsInfo;
import com.telegram.utility.Message;
import com.telegram.utility.User;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageFileDaoImpl extends FileDao implements MessageDao {
    private final String unsentMessagesFileId;

    public MessageFileDaoImpl() {
        filesPath = configuration.getString("messageFilesPath");
        unsentMessagesFileId = configuration.getString("unsentMessagesFileName");
    }

    @Override
    public void updateMessageForUser(String userId, Message<?> message) {
        ArrayBlockingQueue<Message<?>> messagesForUser = getMessagesForUser(userId);
        if(messagesForUser == null){
            messagesForUser = new ArrayBlockingQueue<>(ClientsInfo.MAX_MESSAGES);
        }
        messagesForUser.add(message);
        writeFileById(userId, messagesForUser);
    }

    @Override
    public void updateMessagesForUser(String userId, ArrayBlockingQueue<Message<?>> messages){
        ArrayBlockingQueue<Message<?>> messagesForUser = getMessagesForUser(userId);
        if(messagesForUser == null){
            messagesForUser = new ArrayBlockingQueue<>(ClientsInfo.MAX_MESSAGES);
        }
        messagesForUser.addAll(Arrays.asList(messages.toArray(new Message<?>[0])));
        writeFileById(userId, messagesForUser);
    }

    @Override
    public void deleteMessagesForUser(String userId){
        deleteFileById(userId);
    }

    @Override
    public Map<User, ArrayBlockingQueue<Message<?>>> getUnsentMessages() {
        if (fileExists(unsentMessagesFileId)) {
            Object o = readFileById(unsentMessagesFileId);
            if (o instanceof Map) {
                return (Map<User, ArrayBlockingQueue<Message<?>>>) o;
            }
        }
        return null;
    }

    @Override
    public void writeUnsentMessages(Map<User, ArrayBlockingQueue<Message<?>>> messagesMap) {
        writeFileById(unsentMessagesFileId, (Serializable) messagesMap);
    }

    @Override
    public void updateUnsentMessages(Map<User, ArrayBlockingQueue<Message<?>>> messagesMap) {
        Object o = readFileById(unsentMessagesFileId);
        if(o instanceof Map){
            var unsentMap = (Map<User, ArrayBlockingQueue<Message<?>>>)o;
            unsentMap.putAll(messagesMap);
            writeFileById(unsentMessagesFileId, (Serializable) unsentMap);
        }else if(o == null){
            writeFileById(unsentMessagesFileId, (Serializable) messagesMap);
        }else{
            // TODO
            // Error: file has encountered an error
            return;
        }
    }

    @Override
    public ArrayBlockingQueue<Message<?>> getMessagesForUser(String userId) {
        if(fileExists(userId)) {
            Object o = readFileById(userId);
            if(o instanceof ArrayBlockingQueue){
                return (ArrayBlockingQueue<Message<?>>) o;
            }
        }
        return null;
    }

    @Override
    public void deleteUnsentMessages() {
        deleteFileById(unsentMessagesFileId);
    }
}
