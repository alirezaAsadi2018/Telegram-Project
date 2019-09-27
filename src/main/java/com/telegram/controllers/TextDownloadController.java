package com.telegram.controllers;

import com.telegram.utility.TextMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextDownloadController extends MessageController {
    private TextMessage textMessage;

    public TextDownloadController(TextMessage textMessage) {
        super();
        this.textMessage = textMessage;
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        fileFormatLabel.setVisible(false);
        fileFormatLabel.setManaged(false);
        downloadImage.setVisible(false);
        downloadImage.setManaged(false);
        fileNameLabel.setAlignment(Pos.CENTER);
//        btn_run.setVisible(false);
//        label.setVisible(false);
//        VBoxFile.setManaged(false);
//        textMessageLabel.setVisible(true);
//        textMessageLabel.setAlignment(Pos.CENTER);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        System.out.println(textMessage.getUser());
        Platform.runLater(() -> {
            fileNameLabel.setText(textMessage.getText() + " from: " + textMessage.getUser().getPhone_id());
            timeLabel.setText(LocalDateTime.now().format(format));
        });
    }

    private void downloadClicked(ActionEvent actionEvent) {
    }
}
