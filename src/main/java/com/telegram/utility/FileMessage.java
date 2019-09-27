package com.telegram.utility;

import java.io.File;

public class FileMessage extends Message<File> {
    private String destinationId;
    public FileMessage(){
        super();
    }
    public FileMessage(User from, String destinationId, File file) {
        user = from;
        this.destinationId = destinationId;
        this.data = file;
    }
    FileMessage(User requester, File file){
        user = requester;
        data = file;
    }
    public String getDestinationId() {
        return destinationId;
    }
}
