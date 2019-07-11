package com.corejj.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CNetworkUtils {
    public static void uploadFile(String url, String fileKey, String fileName, File file, Map<String, String> header, Map<String, String> bodyParams, Callback callback) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(120000, TimeUnit.SECONDS) //连接超时
                    .readTimeout(120000, TimeUnit.SECONDS) //读取超时
                    .writeTimeout(120000, TimeUnit.SECONDS) //写超时
                    .build();
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);

            MultipartBody.Builder builder = new MultipartBody.Builder();

            builder.addFormDataPart(fileKey, fileName, fileBody);

            for (String key : bodyParams.keySet()) {
                builder.addFormDataPart(key, bodyParams.get(key));

            }
            RequestBody requestBody = builder.build();

            Request.Builder requestBuilder = new Request.Builder();
            for (String key : header.keySet()) {
                requestBuilder.addHeader(key, header.get(key));
            }
            Request request = requestBuilder
                    .url(url)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadFile(String url, File file, Map<String, String> header, Map<String, String> bodyParams, Callback callback) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(120000, TimeUnit.SECONDS) //连接超时
                .readTimeout(120000, TimeUnit.SECONDS) //读取超时
                .writeTimeout(120000, TimeUnit.SECONDS) //写超时
                .build();
        MediaType mediaType = MediaType.parse("image/png");//设置类型，类型为八位字节流
        RequestBody requestBody = null;
        if (file != null) {
            requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体
        }

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : bodyParams.keySet()) {
            builder.addFormDataPart(key, bodyParams.get(key));
        }
        if (file != null) {
            builder.addFormDataPart("multipartFile", "jpg", requestBody);//文件名,请求体里的文件
        }

        MultipartBody multipartBody = builder.build();


        Request.Builder requestBuilder = new Request.Builder();
        for (String key : header.keySet()) {
            requestBuilder.header(key, header.get(key));
        }
        requestBuilder
                .url(url)
                .post(multipartBody);

        Request request = requestBuilder.build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }

    public static void uploadFile22(String url, File
            file, Map<String, String> header, Map<String, String> bodyParams, Callback callback) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(120000, TimeUnit.SECONDS) //连接超时
                .readTimeout(120000, TimeUnit.SECONDS) //读取超时
                .writeTimeout(120000, TimeUnit.SECONDS) //写超时
                .build();
        MediaType mediaType = MediaType.parse("image/png");//设置类型，类型为八位字节流
        //  RequestBody requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : bodyParams.keySet()) {
            builder.addFormDataPart(key, bodyParams.get(key));
        }
        // builder.addFormDataPart("multipartFile", "jpg", requestBody);//文件名,请求体里的文件

        MultipartBody multipartBody = builder.build();


        Request.Builder requestBuilder = new Request.Builder();
        for (String key : header.keySet()) {
            requestBuilder.header(key, header.get(key));
        }
        requestBuilder.url(url).post(multipartBody);

        Request request = requestBuilder.build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }

    public static void uploadFile1(String url, File file, String token) {
        long size = file.length();//文件长度
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(120000, TimeUnit.SECONDS) //连接超时
                .readTimeout(120000, TimeUnit.SECONDS) //读取超时
                .writeTimeout(120000, TimeUnit.SECONDS) //写超时
                .build();
        MediaType mediaType = MediaType.parse("image/png");//设置类型，类型为八位字节流
        RequestBody requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "ddd")
                .addFormDataPart("phone", "13877876576")
                .addFormDataPart("multipartFile", "jpg", requestBody)//文件名,请求体里的文件
                .build();

        Request request = new Request.Builder()
                .header("token", token)
                .url(url)
                .post(multipartBody)
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ZLog.e("onFailure. " + call.toString() + " e:" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();

                ZLog.e("onResponse. response:" + string);
            }
        });
    }
}
