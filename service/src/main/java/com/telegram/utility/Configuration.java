package com.telegram.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    InputStream inputStream = getClass().getResourceAsStream("/config.properties");
    Properties properties = new Properties();

    public Configuration() throws IOException {
        properties.load(inputStream);
    }

    public String getString(String key) {
        Object value = properties.getOrDefault(key, "");
        if(value instanceof String){
            return (String) value;
        }
        return "";
    }

    public int getInt(String key) {
        Object value = properties.getOrDefault(key, 0);
        if(value instanceof Integer){
            return (Integer) value;
        }
        return 0;
    }
}
