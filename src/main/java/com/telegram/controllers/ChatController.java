package com.telegram.controllers;

import com.jfoenix.controls.JFXTextField;
import com.telegram.client.ClientRunner;
import com.telegram.utility.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Exchanger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatController implements SharedDataExchanger {
    @FXML
    private ListView<MessageController> messageList;
    @FXML
    private JFXTextField textEntered;
    @FXML
    private Label chatInfoLabel;
    @FXML
    private Label directory;

    private Desktop desktop = Desktop.getDesktop();
    private File chosenFile;
    private ObservableList<MessageController> observableList;
    private static String destinationId;
    private Client currentClient;
    private ClientRunner currentClientRunner;
    private Exchanger<Object> exchanger;
    private Object lock;
    private User userToChatWith;

    public void sendText(ActionEvent actionEvent) {
        if(textFieldValidation.checkNullAndNonEmpty(textEntered.getText()))
            return;
        if(currentClientRunner != null)
            currentClientRunner.sendTextMessage(textEntered.getText(), userToChatWith.getPhone_id());
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(currentClient.getStatus() == ClientState.textMessageSent)
            observableList.add(uploadTextMessages(new TextMessage(textEntered.getText(), currentClient)));
    }

    @Override
    public void setData(Object... data) {
        if(data != null && data.length == 2) {
            currentClient = (Client) data[0];
            currentClientRunner = (ClientRunner)data[1];
        }
        if(data != null && data.length == 5) {
            currentClient = (Client) data[0];
            currentClientRunner = (ClientRunner)data[1];
            userToChatWith = (User)data[2];
            exchanger = (Exchanger<Object>)data[3];
            this.lock = data[4];
        }
        chatInfoLabel.setText(chatInfoLabel.getText().concat(userToChatWith.getFirstName() + " " + userToChatWith.getLastName()));
        observableList = FXCollections.observableList(new LinkedList<>());
        messageList.setItems(observableList);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    Object messageObject = exchanger.exchange(new Object());
                    if (messageObject instanceof TextMessage) {
                        try {
                            TextDownloadController textDownloadController = uploadTextMessages((TextMessage) messageObject);
                            Platform.runLater(() -> observableList.add(textDownloadController));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if (messageObject instanceof FileMessage) {
                        System.out.println("one File message received");
                        FileDownloadController fileDownloadController =  uploadFileMessages(currentClient, (FileMessage) messageObject);
                        Platform.runLater(() -> observableList.add(fileDownloadController));
                    }
                }
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    public void chooseFile(ActionEvent actionEvent){
        final FileChooser fileChooser = new FileChooser();
//        final Button openMultipleButton = new Button("Open Pictures...");
        File file = fileChooser.showOpenDialog(Main.ps);
        if (file != null) {
            System.out.println(file.getPath());
            chosenFile = file;
            directory.setText(file.getPath());
//            openFile(file);
        }
    }

    public void sendChosenFile(ActionEvent actionEvent){
        if(currentClientRunner != null)
            currentClientRunner.sendFile(chosenFile, userToChatWith.getPhone_id());
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        FileUploadController fileUploadController = new FileUploadController(chosenFile, currentClient, userToChatWith);
        observableList.add(fileUploadController);
    }
    private TextDownloadController uploadTextMessages(TextMessage textMessage){
        return new TextDownloadController(textMessage);
    }
    private FileDownloadController uploadFileMessages(Client requester, FileMessage fileMessage){
        return new FileDownloadController(requester, fileMessage);
    }
    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                    ChatController.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }

    @Override
    public Object[] getData() {
        return new Object[0];
    }

    public void goBack(ActionEvent actionEvent) {
        Main.getInstance().loadScene(FxmlNames.HomePage, "homepage");
    }
}
