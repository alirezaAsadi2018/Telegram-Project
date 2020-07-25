package com.telegram.utility;

public class GetContactsAnswer extends Message<Object> {
    public GetContactsAnswer(Object data) {
        super(data);
    }

    @Override
    public Object getData() {
        return data;
    }
}
