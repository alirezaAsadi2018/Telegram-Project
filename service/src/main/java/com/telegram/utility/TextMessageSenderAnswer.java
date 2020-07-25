package com.telegram.utility;

public class TextMessageSenderAnswer extends Message<String> {
    public TextMessageSenderAnswer(String data){
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }
}
