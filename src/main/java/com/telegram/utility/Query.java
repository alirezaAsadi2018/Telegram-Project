package com.telegram.utility;

import java.io.Serializable;

public abstract class Query<E> implements Serializable{
    protected User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
