package com.telegram.data;

import com.telegram.utility.User;
import java.util.concurrent.RecursiveTask;

public class DataRunner extends RecursiveTask<User> {// search the map for a client
    private User user;
//    private String[] mapArray;
    public DataRunner(User user) {
        this.user  = user;
//        mapArray = new String[];
    }

    @Override
    protected User compute() {
        if(ClientsInfo.clientsData.size() == 0)
            return null;
        for (User alreadySignedInClient : ClientsInfo.clientsData.values()) {
            if (user.equals(alreadySignedInClient))
                return alreadySignedInClient;
        }
        return null;
    }
}
