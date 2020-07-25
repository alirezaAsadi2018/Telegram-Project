package com.telegram.utility;

public class DownloadFileMessage extends Message<String> {
    public DownloadFileMessage(User user, String fileName) {
        this.user =user;
        data = fileName;
    }
}
