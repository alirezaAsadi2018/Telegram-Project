package com.telegram.utility;

public class GetUsersListAnswer extends Message<Object> {

    public GetUsersListAnswer(Object data) {
        super(data);
    }

    @Override
    public Object getData() {
        return data;
    }
}
