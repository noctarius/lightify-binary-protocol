package com.noctarius.lightify;

public interface StatusListener {

    void onConnect();

    void onConnectionFailed();

    void onConnectionEstablished();

    void onConnectionLost();

}
