package com.maxi.corejj.infrastucture.network;

import android.text.TextUtils;

import com.maxi.corejj.callback.NetworkCallback;
import com.maxi.corejj.infrastucture.utils.L;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CHttp {
    private CHttpBuilder mBuilder;

    private CHttp(CHttpBuilder b) {
        mBuilder = b;
    }

    public static CHttpBuilder with(String url) {
        return new CHttpBuilder(url);
    }

    public void enqueue() {
        //Request Builder
        Request.Builder builder = new Request.Builder();
        //url
        builder.url(mBuilder.url);
        //header
        if (mBuilder.header != null && mBuilder.header.size() > 0) {
            for (String key : mBuilder.header.keySet()) {
                builder.header(key, mBuilder.header.get(key));
            }
        }
        //body
        if (mBuilder.requestModel == RequestModel.POST) {
            builder.post(getRequestBody());
        }

        //Request
        final Request request = builder.build();
        //Client
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mBuilder.callback != null) {
                    mBuilder.callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (mBuilder.callback != null) {
                    mBuilder.callback.onResponse(call, response);
                }
            }
        });
    }

    /**
     * *********************************************************************************************
     */
    private RequestBody getRequestBody() {
        /**
         * 首先判断mJsonStr是否为空，由于mJsonStr与mParamsMap不可能同时存在，所以先判断mJsonStr
         */
        if (!TextUtils.isEmpty(mBuilder.json)) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
            return RequestBody.create(JSON, mBuilder.json);//json数据，
        }

        /**
         * post,put,delete都需要body，但也都有body等于空的情况，此时也应该有body对象，但body中的内容为空
         */
        FormBody.Builder formBody = new FormBody.Builder();
        if (mBuilder.bodyParams != null) {
            for (String key : mBuilder.bodyParams.keySet()) {
                formBody.add(key, mBuilder.bodyParams.get(key));
            }
        }
        return formBody.build();
    }

    public static final class CHttpBuilder {
        String url;
        RequestModel requestModel;
        String json;
        Map<String, String> bodyParams;
        Map<String, String> header;
        NetworkCallback callback;

        private JSONObject jsonObject;

        private CHttpBuilder(String u) {
            url = u;
        }

        public CHttpBuilder requestModel(RequestModel model) {
            requestModel = model;
            return this;
        }

        public CHttpBuilder json(String j) {
            json = j;
            return this;
        }

        public CHttpBuilder json(String key, String value) {
            if (jsonObject == null) {
                jsonObject = new JSONObject();
            }
            try {
                jsonObject.put(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public CHttpBuilder header(String key, String value) {
            if (header == null) {
                header = new HashMap<>();
            }
            header.put(key, value);
            return this;
        }

        public CHttpBuilder bodyParams(Map b) {
            if (bodyParams == null) {
                bodyParams = b;
            } else {
                bodyParams.putAll(b);
            }
            return this;
        }

        public CHttpBuilder bodyParams(String key, String value) {
            if (bodyParams == null) {
                bodyParams = new HashMap<>();
            }
            bodyParams.put(key, value);
            return this;
        }

        public CHttpBuilder callback(NetworkCallback c) {
            callback = c;
            return this;
        }

        public void enqueue() {

            try {
                json = jsonObject.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            L.d("url:" + url + " json:" + json);
            new CHttp(this).enqueue();
        }
    }

    public enum RequestModel {
        GET,
        POST
    }
}
