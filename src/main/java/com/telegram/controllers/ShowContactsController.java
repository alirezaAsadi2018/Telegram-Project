package com.telegram.controllers;

import com.telegram.client.ClientRunner;
import com.telegram.utility.Client;
import com.telegram.utility.FxmlNames;
import com.telegram.utility.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Exchanger;

public class ShowContactsController implements SharedDataExchanger {
    @FXML
    private ListView contactsList;

    private ObservableList<User> observableList;
    private ObservableList<User> myContacts;
    private Client currentClient;
    private ClientRunner currentClientRunner;
    private Exchanger<Object> exchanger;
    private Object lock;

    public void goToContactPage(ActionEvent actionEvent) {
        User userToChatWith = (User) contactsList.getSelectionModel().getSelectedItem();
        Main.getInstance().loadScene(FxmlNames.Chat, "chat", currentClient, currentClientRunner, userToChatWith, exchanger, lock);
    }

    @Override
    public Object[] getData() {
        return new Object[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setData(Object... data) {
        if (data != null && data.length == 4) {
            currentClient = (Client) data[0];
            currentClientRunner = (ClientRunner) data[1];
            exchanger = (Exchanger<Object>) data[2];
            lock = data[3];
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                User[] users = (User[]) exchanger.exchange(currentClient);
                observableList = FXCollections.observableList(new CopyOnWriteArrayList<>(users));
                Platform.runLater(() -> contactsList.setItems(observableList));
                return null;
            }
        };
        currentClientRunner.getContacts();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void goBack(ActionEvent actionEvent) {
        Main.getInstance().loadScene(FxmlNames.HomePage, "homepage");
    }
}
