package com.telegram.utility;

public class SignInAnswer extends Message<String> {
    public SignInAnswer(String data){
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }
}
