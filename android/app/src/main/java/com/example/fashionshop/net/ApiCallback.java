package com.example.fashionshop.net;

/**
 * 网络回调，onSuccess / onError 都在主线程执行，可以直接更新 UI。
 */
public interface ApiCallback<T> {

    void onSuccess(T data);

    void onError(String message);
}
