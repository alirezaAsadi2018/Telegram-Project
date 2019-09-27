package com.telegram;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest implements Runnable {
    ExecutorService executorService = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        ThreadTest threadTest1 = new ThreadTest();
        threadTest1.find();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }

    void find() {
        Thread thread = new Thread(this);
        thread.setName("hello");
        thread.setDaemon(true);
        executorService.execute(thread);
        thread.setName("hi");
        executorService.execute(thread);
    }
}
