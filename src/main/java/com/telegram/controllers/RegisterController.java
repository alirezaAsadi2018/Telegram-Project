package com.telegram.controllers;

import com.jfoenix.controls.JFXButton;
import com.telegram.client.ClientRunner;
import com.telegram.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.concurrent.Exchanger;


public class RegisterController implements SharedDataExchanger {
    @FXML
    private JFXButton registerPageBtn;
    @FXML
    private JFXButton firstPageBtn;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField firstname;
    @FXML
    private TextField lastname;
    @FXML
    private TextField numberId;
    @FXML
    private Label errorText;


    public void register(ActionEvent actionEvent) {
        if (textFieldValidation.checkNullAndNonEmpty(username.getText())
                || textFieldValidation.checkNullAndNonEmpty(password.getText())
                || textFieldValidation.checkNullAndNonEmpty(firstname.getText())
                || textFieldValidation.checkNullAndNonEmpty(lastname.getText())
                || textFieldValidation.checkNullAndNonEmpty(numberId.getText())) {
            errorText.setText("enter at least a letter!");
            return;
        }
        User user = new User(firstname.getText(), lastname.getText(), username.getText(), password.getText(), false, numberId.getText());
        Client client;
        try {
            client = new Client(user, false, null, null);
        } catch (IOException e) {
            System.out.println("in registration while creating a socket: " + e.getMessage());
            System.err.println("server is not responding");
            ClientRunner.setCachedRequests(user, new RegisterQuery(user));
            Alert alert = new Alert(Alert.AlertType.WARNING, "server is not connected!!");
            alert.show();
            return;
        }
        Object lock = new Object();
        ClientRunner clientRunner = new ClientRunner(client, new Exchanger<>(), lock);
        clientRunner.registerMe();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("registerMe notified");
        ClientState result = client.getStatus();
        errorText.setText(result.toString());
        if (result.toString().equalsIgnoreCase("successfulRegistration")) {
            client.setStatus(ClientState.closedSocket);
            Main.getInstance().loadScene(FxmlNames.firstPage, "telegram");
        }
        client.setStatus(ClientState.closedSocket);
    }

    @Override
    public Object[] getData() {
        return new Object[0];
    }

    @Override
    public void setData(Object... data) {

    }

    public void goToFirstPage(ActionEvent actionEvent) {
        Main.getInstance().loadScene(FxmlNames.firstPage, "hello");
    }

}
