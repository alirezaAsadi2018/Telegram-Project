package com.telegram.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public abstract class MessageController extends AnchorPane {
    @FXML
    protected ProgressIndicator progressIndicator;
    @FXML
    protected Label timeLabel;
    @FXML
    protected Label fileNameLabel;
    @FXML
    protected Label fileFormatLabel;
    @FXML
    protected ImageView downloadImage;

    protected MessageController() {
        load("downloadIcon");
    }

    void load(String name) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxmls/" + name + ".fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }

    }

}
