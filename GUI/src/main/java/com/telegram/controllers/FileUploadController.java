package com.telegram.controllers;

import com.telegram.utility.Client;
import com.telegram.utility.DownloadFinishedMessage;
import com.telegram.utility.FileMessage;
import com.telegram.utility.User;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// client side download controller
public class FileUploadController extends MessageController {
    private User userToChatWith;
    private Client from;
    private File chosenFile = null; // uses absolute path directory must be changed if folder names are changed or manipulated

    public FileUploadController(File chosenFile, Client from, User userToChatWith) {
        super();
        this.chosenFile = chosenFile;
        this.userToChatWith = userToChatWith;
        this.from = from;
//        textMessageLabel.setVisible(false);
//        btn_run.setText("upload");
        System.out.println(progressIndicator.getProgress());
        progressIndicator.setOnMouseClicked(this::uploadClicked);
        downloadImage.setOnMouseClicked(this::uploadClicked);
//        Platform.runLater(()-> textMessageLabel.setText(" from: " + from.getPhone_id()));
//        btn_run.setOnAction(event -> onAction.handle(event));

    }

//    private EventHandler<ActionEvent> onAction;

    public void uploadClicked(MouseEvent mouseEvent) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected void scheduled() {
//                label.setText("uploading!!!");
//                label.setTextFill(Color.rgb(0, 181, 173));
            }

            @Override
            protected void succeeded() {
                System.out.println("uploaded succesfully!!");
//                label.setTextFill(Color.rgb(14, 164, 50));
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
                Socket socket = new Socket("localhost", 4050);
                from.setVideoSocket(socket);
                ObjectOutputStream out = new ObjectOutputStream(from.getVideoSocket().getOutputStream());
                out.writeObject(new FileMessage(from, userToChatWith.getPhone_id(), chosenFile));
                ObjectOutputStream outputStream = new ObjectOutputStream(from.getVideoSocket().getOutputStream());
                FileInputStream i = new FileInputStream(chosenFile);
                byte[] buffer = new byte[2048];
                int read = 0;
                while ((read = i.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, read);
                }
                System.out.println("sending finished");
                outputStream.writeObject(new DownloadFinishedMessage());
                return null;
            }
        };
        super.load("download");
        System.out.println("changed download to pause");
        progressIndicator.setOnMouseClicked(this::pauseUploading);
        progressIndicator.progressProperty().bind(task.progressProperty());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(task);
        executorService.shutdown();
    }

    private void pauseUploading(MouseEvent mouseEvent) {

    }

}
