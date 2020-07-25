package com.telegram.utility;

import java.util.Objects;

public class Message<E> extends Query<E> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message<?> message = (Message<?>) o;
        if(getUser() == null)
            return Objects.equals(getData(), message.getData());
        else
            return Objects.equals(getData(), message.getData()) && getUser().equals(message.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(data) + super.hashCode();
    }

    @Override
    public String toString() {
        return "Message{" + "data=" + data + '}';
    }
}
