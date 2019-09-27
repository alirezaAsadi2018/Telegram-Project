package com.telegram.utility;

public class TextMessage extends Message<String> {
    private String destinationId;

    public TextMessage(User from, String text, String destinationId) {
        user = from;
        data = text;
        this.destinationId = destinationId;
    }

    public TextMessage(String data, User from) {
        super(data);
        this.user = from;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getText() {
        return data;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public void setText(String text) {
        data = text;
    }
}
