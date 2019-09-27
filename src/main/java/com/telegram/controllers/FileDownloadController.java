package com.telegram.controllers;

import com.telegram.utility.Client;
import com.telegram.utility.DownloadFileMessage;
import com.telegram.utility.FileMessage;
import com.telegram.utility.User;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDownloadController extends MessageController {
    private User userToChatWith;
    private Client requester;
    private FileMessage fileMessage;
    private Desktop desktop = Desktop.getDesktop();
    private Button showPictureBtn;


    public FileDownloadController(Client requester, FileMessage fileMessage) {
        super();
        this.requester = requester;
        this.fileMessage = fileMessage;
//        textMessageLabel.setVisible(false);
        showPictureBtn = new Button("showPicture");
//        showPictureBtn.setOnAction(this::showPictures);
        progressIndicator.setOnMouseClicked(this::downloadClicked);
        downloadImage.setOnMouseClicked(this::downloadClicked);
//        btn_run.setText("download");
//        btn_run.setOnAction(this::downloadClicked);
//        btn_run.setOnAction(event -> onAction.handle(event));

    }

    //    private EventHandler<ActionEvent> onAction;

    private void showPictures(ActionEvent actionEvent) {
        try {
            desktop.open(fileMessage.getData());
        } catch (IOException ex) {
            Logger.getLogger(
                    ChatController.class.getName()).log(
                    Level.SEVERE, null, ex
            );
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

    public void downloadClicked(MouseEvent mouseEvent) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected void scheduled() {
//                label.setText("Downloading!!!");
//                label.setTextFill(Color.rgb(0, 181, 173));
            }

            @Override
            protected void succeeded() {
                System.out.println("downloaded succesfully!!");
//                label.setTextFill(Color.rgb(14, 164, 50));
//                progressIndicator.setProgress(100);
                File file = fileMessage.getData();
//                if (file != null && (file.getName().contains("jpg") || file.getName().contains("png") || file.getName().contains("jpeg") || file.getName().contains("icon"))){
//                    openFile(file);
//                    Platform.runLater(() -> VBoxFile.getChildren().add(showPictureBtn));
//                }
            }

            @Override
            protected void cancelled() {
                System.out.println("cancelled successfully!!");
                progressIndicator.progressProperty().unbind();
                progressIndicator.setProgress(0); //Set It To initialState!!
//                label.setTextFill(Color.rgb(0, 181, 173));
            }

            @Override
            protected void failed() {
                System.out.println("failed successfully!! " + getException());
                progressIndicator.progressProperty().unbind();
                progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS); //Set It To Rounding!!
//                label.setTextFill(Color.rgb(100, 0, 0));
            }

            @Override
            protected Void call() throws Exception {
                while (true) {
                    Socket socket = new Socket("localhost", 4050);
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    String from = fileMessage.getUser().getPhone_id();
                    String to = fileMessage.getDestinationId();
                    long totalFileSize = fileMessage.getData().length(); //First Of All Read File Size!!
                    String sentFileName = fileMessage.getData().getName();
                    String fileExtension = ".dat";
                    if (sentFileName.contains("."))
                        fileExtension = sentFileName.substring(sentFileName.indexOf("."));
                    String fileName = fileMessage.getData().getName() + "-length= " + totalFileSize + "-from= " + from + "-to= " + to + fileExtension;
                    output.writeObject(new DownloadFileMessage(requester, fileName));
                    File file = Paths.get("./src/downloads/" + fileName).toAbsolutePath().toFile();
                    OutputStream o;
                    if (!file.exists())
                        o = new FileOutputStream(Paths.get("./src/downloads/" + fileName).toAbsolutePath().toFile());
                    else {
                        o = new FileOutputStream(file);
                    }
                    int totalDownloaded = 0, read;
                    byte[] buffer = new byte[2048];
                    try {
                        while ((read = inputStream.read(buffer)) > 0) {
                            o.write(buffer, 0, read);
                            totalDownloaded += read;
                            updateProgress(totalDownloaded, fileMessage.getData().length());
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.err.println("exception downloading");
                    }
                    return null;
                }
            }
        };
        super.load("download");
        System.out.println("changed download to pause");
        progressIndicator.setOnMouseClicked(this::pauseDownloading);
        progressIndicator.progressProperty().bind(task.progressProperty());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(task);
        executorService.shutdown();
    }

    private void pauseDownloading(MouseEvent mouseEvent) {

    }
}
