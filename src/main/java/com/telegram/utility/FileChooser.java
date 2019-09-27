package com.telegram.utility;

import com.telegram.controllers.Main;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class FileChooser {
    public static void main(String[] args) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File("c:/dev/javafx");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(Main.ps);
    }
}
