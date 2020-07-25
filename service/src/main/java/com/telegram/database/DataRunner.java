package com.telegram.database;

import com.telegram.utility.User;
import java.util.concurrent.RecursiveTask;

public class DataRunner extends RecursiveTask<User> {// search the map for a client
    private User user;
    public DataRunner(User user) {
        this.user  = user;
    }

    @Override
    protected User compute() {
        if(ClientsInfo.clients.size() == 0)
            return null;
        for (User alreadySignedInClient : ClientsInfo.clients.values()) {
            if (user.equals(alreadySignedInClient))
                return alreadySignedInClient;
        }
        return null;
    }
}
