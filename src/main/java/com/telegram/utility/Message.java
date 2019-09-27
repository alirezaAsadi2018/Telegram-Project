package com.telegram.utility;

public class Message<E> extends Query{
    protected E data;

    public Message(E data) {
        this.data = data;
    }

    public Message() {
    }

    public E getData() {
        return data;
    }
    public void setData(E data) {
        this.data = data;
    }
}
