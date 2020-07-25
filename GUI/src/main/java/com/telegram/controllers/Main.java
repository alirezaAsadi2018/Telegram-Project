package com.telegram.controllers;

import com.telegram.utility.FxmlNames;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * created by Alireza Asadi on 2018/6/28
 */
public class Main extends Application implements Initializable {// uses absolute path directory must be changed if folder names are changed or manipulated

    private static final Object mutex = new Object();
    public static Stage ps;
    private static volatile Main instance;

    public static Main getInstance() { // ASingleton (thread-safe and singleton)
        Main result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Main();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        launch(Main.class, args);
    }

    public void loadScene(FxmlNames name, String title, double width, double height, Object... sharedData) {
        String fxmlResourcePath = "/fxmls/" + name.toString() + ".fxml";
        URL fxmlUrl = Main.this.getClass().getResource(fxmlResourcePath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        try {
            Parent root = fxmlLoader.load();
            SharedDataExchanger dataExchanger = fxmlLoader.getController();
            dataExchanger.setData(sharedData);
            ps.setTitle(title);
            ps.setScene(new Scene(root, width, height));
            ps.show();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    public void loadScene(FxmlNames name, String title, Object... sharedData) {
        loadScene(name, title, -1, -1, sharedData);
    }

    @Override
    public void start(Stage primaryStage) {
        ps = primaryStage;
        loadScene(FxmlNames.firstPage, "Hello", (Object) null);
    }

    @Override
    public void init() throws Exception {
        if (instance == null)
            instance = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
//    @FXML
//    void initialize(){
//
//    }
//    @FXML
//    ResourceBundle stringBundle;
//    @FXML
//    URL location;
}
