package com.serjardovic.testapp2.interfaces;

public interface NetworkListener<T> {

    void onSuccess(T data);
    void onError(String error);

}
