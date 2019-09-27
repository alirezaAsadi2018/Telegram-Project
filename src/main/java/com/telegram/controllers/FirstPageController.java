package com.telegram.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.telegram.client.ClientRunner;
import com.telegram.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.concurrent.Exchanger;


public class FirstPageController implements SharedDataExchanger {
    @FXML
    private JFXButton registerPageBtn;
    @FXML
    private JFXButton firstPageBtn;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXTextField numberId;
    @FXML
    private Label errorText;

    public void signIn(ActionEvent actionEvent) {
        try {
            if (textFieldValidation.checkNullAndNonEmpty(username.getText())
                    || textFieldValidation.checkNullAndNonEmpty(password.getText())
                    || textFieldValidation.checkNullAndNonEmpty(numberId.getText())) {
                errorText.setText("enter at least a letter!!");
                return;
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        User user = new User(null, null, username.getText(), password.getText(), false, numberId.getText());
        Client client;
        try {
            client = new Client(user, false, null, null);
        } catch (IOException e) {
            System.out.println("in signIn while creating a socket: " + e.getMessage());
            System.err.println("server is not responding");
            ClientRunner.setCachedRequests(user, new SignInQuery(user));
            Alert alert = new Alert(Alert.AlertType.WARNING, "server is not connected!!");
            alert.show();
            return;
        }
        Exchanger<Object> exchanger = new Exchanger<>();
        Object lock = new Object();
        ClientRunner clientRunner = new ClientRunner(client, exchanger, lock);
        clientRunner.signMeIn();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("signMeIn notified");
            ClientState result = client.getStatus();
            System.out.println(result);
            errorText.setText(result.toString());
            if (result.toString().equalsIgnoreCase("successfulSignIn")) {
                client.setStatus(ClientState.closedSocket);
                Main.getInstance().loadScene(FxmlNames.HomePage, "telegram", client, clientRunner, exchanger, lock);
            }
            client.setStatus(ClientState.closedSocket);
        }

    }

    @Override
    public void setData(Object... data) {
//        if(data != null && data.length == 1)
//            clientRunner = (ClientRunner)data[0];// sent from register page
    }

    @Override
    public Object[] getData() {
        return new Object[0];
    }

    public void goToRegister(ActionEvent actionEvent) {
        Main.getInstance().loadScene(FxmlNames.register, "registration");
//        disableButton(firstPageBtn);
//        enableButton(registerPageBtn);
    }
    void disableButton(JFXButton btn){
        btn.setTextFill(Color.BLACK);
        btn.setOpacity(0.3);
        btn.setUnderline(false);
    }
    void enableButton(JFXButton btn){
        btn.setTextFill(Color.valueOf("#0088CC"));
        btn.setOpacity(1);
        btn.setUnderline(true);
    }
}
