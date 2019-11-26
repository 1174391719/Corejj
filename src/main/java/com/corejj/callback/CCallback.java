package com.corejj.callback;

public interface CCallback {
    void onSuccess(String response);

    void onFailure(String error);
}
