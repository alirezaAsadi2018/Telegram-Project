package com.telegram.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileChooserController implements SharedDataExchanger {

    private Desktop desktop = Desktop.getDesktop();

    public void chooseFile(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        final Button openMultipleButton = new Button("Open Pictures...");
        File file = fileChooser.showOpenDialog(Main.ps);
        if (file != null) {
        System.out.println(file.getPath());
            openFile(file);
        }
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
    public void setData(Object... data) {

    }

    @Override
    public Object[] getData() {
        return new Object[0];
    }
}
