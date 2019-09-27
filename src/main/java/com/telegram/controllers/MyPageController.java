package com.telegram.controllers;

import com.telegram.client.ClientRunner;
import com.telegram.utility.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Exchanger;


public class MyPageController implements SharedDataExchanger {
    @FXML
    private ListView<MessageController> messageList;

    private ObservableList<MessageController> observableList;
    private ObservableList<User> myContacts;
    private Client currentClient;
    private ClientRunner currentClientRunner;
    private Exchanger<Object> exchanger;
    private Object lock;

    public void answerBack(ActionEvent actionEvent) {

    }

    private TextDownloadController uploadTextMessages(TextMessage textMessage) {
        return new TextDownloadController(textMessage);
    }

    private FileDownloadController uploadFileMessages(Client requester, FileMessage fileMessage) {
        return new FileDownloadController(requester, fileMessage);
    }

    @Override
    public Object[] getData() {
        return new Object[0];
    }

    @Override
    public void setData(Object... data) {
        if (data != null && data.length == 3) {
            currentClient = (Client) data[0];
            currentClientRunner = (ClientRunner) data[1];
            exchanger = (Exchanger<Object>) data[2];
        }
        observableList = FXCollections.observableList(new LinkedList<>());
        messageList.setItems(observableList);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Socket socket = new Socket("localhost", 4050);
                currentClient.setVideoSocket(socket);
                ObjectOutputStream out = new ObjectOutputStream(currentClient.getVideoSocket().getOutputStream());
                ObjectInputStream in = new ObjectInputStream(currentClient.getVideoSocket().getInputStream());
                out.reset();
                synchronized (currentClient.getVideoSocket().getInputStream()) {
                    out.writeObject(new GetCachedMessagesQuery(currentClient));// answers with video port
                }
                out.flush();
                DataInputStream inputStream = new DataInputStream(currentClient.getVideoSocket().getInputStream());
                Message[] messages = (Message[]) in.readObject();
                for (Message message : messages) {
                    if (message instanceof TextMessage) {
                        System.out.println("one text message received");
                        try {
                            TextDownloadController textDownloadController = uploadTextMessages((TextMessage) message);
                            Platform.runLater(() -> observableList.add(textDownloadController));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("text message reached");
                    } else if (message instanceof FileMessage) {
                        System.out.println("one File message received");
                        FileDownloadController fileDownloadController = uploadFileMessages(currentClient, (FileMessage) message);
                        Platform.runLater(() -> observableList.add(fileDownloadController));
                    }
                    System.out.println("text message finished");
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
