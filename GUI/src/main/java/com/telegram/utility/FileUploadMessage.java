package com.telegram.utility;

import java.io.File;

public class FileUploadMessage extends FileMessage {
    public FileUploadMessage(User from, String destinationId, File file) {
        super(from, destinationId, file);
    }
}
