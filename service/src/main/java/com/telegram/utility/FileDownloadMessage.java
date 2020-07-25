package com.telegram.utility;

import java.io.File;

public class FileDownloadMessage extends FileMessage {
    public FileDownloadMessage(User from, String destinationId, File file) {
        super(from, destinationId, file);
    }
}
