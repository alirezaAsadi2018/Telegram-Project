package com.telegram.utility;

import java.io.Serializable;
import java.util.Objects;

public abstract class Query<E> implements Serializable {
    protected User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query<?> query = (Query<?>) o;
        return Objects.equals(user, query.user);
    }

    @Override
    public int hashCode() {
        return (getUser() != null ? Objects.hash(user) : 43);
    }
}
