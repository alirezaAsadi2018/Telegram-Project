package com.telegram.controllers;

import com.telegram.client.ClientRunner;
import com.telegram.utility.Client;
import com.telegram.utility.ClientState;
import com.telegram.utility.FxmlNames;
import com.telegram.utility.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Exchanger;

public class AddContactsController implements SharedDataExchanger {
    @FXML
    private ListView contactsList;
    private ObservableList<User> observableList;
    private ObservableList<User> myContacts;
    private Client currentClient;
    private ClientRunner currentClientRunner;
    private Exchanger<Object> exchanger;
    private Object lock;


    @SuppressWarnings("unchecked")
    @Override
    public void setData(Object... data) {
        if (data != null && data.length == 4) {
            currentClient = (Client) data[0];
            currentClientRunner = (ClientRunner) data[1];
            this.exchanger = (Exchanger<Object>) data[2];
            this.lock = data[3];
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Map<String, User> users = (Map<String, User>) exchanger.exchange(currentClient);
                System.err.println("in contact gui: " + users);
                observableList = FXCollections.observableList(new CopyOnWriteArrayList<>(users.values()));
                Platform.runLater(() -> contactsList.setItems(observableList));
                return null;
            }
        };
        currentClientRunner.getMembers();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public Object[] getData() {
        return new Object[0];
    }

    public void addContact(ActionEvent actionEvent) {
        User userToBeAdded = (User) contactsList.getSelectionModel().getSelectedItem();
        if(userToBeAdded == null)
            return;// set error text to nothing selected
        currentClientRunner.addContact(userToBeAdded);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(currentClient.getStatus());
                if (currentClient.getStatus() == ClientState.successfulAddContact) {
                    System.out.println(currentClient.getStatus());
                    //do sth
                } else if (currentClient.getStatus() == ClientState.failedAddContact) {
                    System.out.println(currentClient.getStatus());
                    //do sth
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        Main.getInstance().loadScene(FxmlNames.HomePage, "telegram", currentClient, currentClientRunner, exchanger, lock);
    }

    public void goBack(ActionEvent actionEvent) {
        Main.getInstance().loadScene(FxmlNames.HomePage, "homepage");
    }
}
