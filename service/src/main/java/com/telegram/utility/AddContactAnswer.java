package com.telegram.utility;

public class AddContactAnswer extends Message<String> {

    public AddContactAnswer(String data) {
        super(data);
    }

    @Override
    public String getData() {
        return data;
    }
}
