package com.telegram.database.dao;

import com.telegram.utility.Configuration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDao {
    protected Configuration configuration;
    protected String filesPath;

    public FileDao() {
        try {
            configuration = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected Object readFileById(String clientId) {
        String filePath = Path.of(filesPath, clientId).toString();
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(filePath))) {
            return input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void writeFileById(String clientId, Serializable toBeWritten) {
        Path filePath = Path.of(filesPath, clientId);
        try {
            if (!fileExists(clientId)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            // This block also catches 'FileAlreadyExistsException'
            e.printStackTrace();
        }
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filePath.toString()))) {
            output.writeObject(toBeWritten);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void deleteFileById(String clientId) {
        Path filePath = Path.of(filesPath, clientId);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean fileExists(String clientId) {
        if (clientId == null || clientId.equals("") || clientId.contains("/"))
            return false;
        Path filePath = Path.of(filesPath, clientId);
        return Files.exists(filePath);
    }
}
