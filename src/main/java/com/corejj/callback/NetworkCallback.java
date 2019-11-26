package com.corejj.callback;

import okhttp3.Call;
import okhttp3.Response;

public interface NetworkCallback {
    void onFailure(Call call, Exception e);

    void onResponse(Call call, Response response);

    void onSafeFailure(Exception e);

    void onSafeResponse(String response);
}
