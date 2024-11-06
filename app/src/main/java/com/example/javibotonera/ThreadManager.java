package com.example.javibotonera;

public class ThreadManager {
    private static ThreadManager instance;
    private ConnectedThread connectedThread;

    private ThreadManager() {
    }

    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            instance = new ThreadManager();
        }
        return instance;
    }

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void setConnectedThread(ConnectedThread thread) {
        this.connectedThread = thread;
    }
}
