package com.telegram.controllers;

import com.telegram.client.ClientRunner;
import com.telegram.utility.Client;
import com.telegram.utility.FxmlNames;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.concurrent.Exchanger;

public class HomePageController implements SharedDataExchanger {
    private Client currentClient;
    private ClientRunner currentClientRunner;
    private Exchanger<Object> exchanger;
    private Object lock;
    @FXML
    private Label clientInfoLabel;


    public void goToMyPage(ActionEvent actionEvent) {
        Main.getInstance().loadScene(FxmlNames.myPage, "chat here!", currentClient, currentClientRunner, exchanger);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setData(Object... data) {
        if(data != null && data.length == 4) {
            currentClient = (Client) data[0];
            currentClientRunner = (ClientRunner)data[1];
            exchanger = (Exchanger<Object>)data[2];
            this.lock = data[3];
        }
        clientInfoLabel.setText(clientInfoLabel.getText().concat(currentClient.getPhone_id()));
    }

    @Override
    public Object[] getData() {
        return new Object[0];
    }

    public void goToAddContacts(ActionEvent actionEvent) {
        Main.getInstance().loadScene(FxmlNames.addContacts, "addContacts", currentClient, currentClientRunner, exchanger, lock);
    }

    public void showContacts(ActionEvent actionEvent) {
        Main.getInstance().loadScene(FxmlNames.showContacts, "contacts", currentClient, currentClientRunner, exchanger, lock);
    }
}
