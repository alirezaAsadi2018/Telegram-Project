package com.telegram.utility;

public class RegisterAnswer extends Message<String> {
    public RegisterAnswer(String data){
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }
}
