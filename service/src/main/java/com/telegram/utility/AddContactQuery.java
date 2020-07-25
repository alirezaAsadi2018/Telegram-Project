package com.telegram.utility;

public class AddContactQuery extends Query {
    private User userToBeAdded;

    public AddContactQuery(User user, User userToBeAdded) {
        this.user = user;
        this.userToBeAdded = userToBeAdded;
    }
    public User getUserToBeAdded() {
        return userToBeAdded;
    }
}
