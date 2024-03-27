package com.maxi.corejj.infrastucture.network;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
在Android应用访问后台时，需要进行签名以确保通信的安全性。一种常见的做法是使用SSL证书对通信进行加密。以下是一般的步骤：
生成SSL证书：
1、生成一个自签名的SSL证书，可以使用keytool命令或者使用第三方工具生成。将生成的证书保存为.keystore文件。
2、在应用中使用SSL证书：
    将SSL证书文件放置在应用的assets目录下。
    在应用代码中加载SSL证书并配置到网络请求中，示例代码如下：
    // 加载SSL证书
    InputStream keyStoreInputStream = context.getAssets().open("your_certificate.keystore");
    KeyStore keyStore = KeyStore.getInstance("BKS");
    keyStore.load(keyStoreInputStream, "your_password".toCharArray());

    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keyStore);

    // 创建SSLContext
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

    // 配置到网络请求
    OkHttpClient client = new OkHttpClient.Builder()
    .sslSocketFactory(sslContext.getSocketFactory())
    .build();

3、发送HTTPS请求：
    使用配置好SSL证书的网络请求库发送HTTPS请求到后台服务器。

通过以上步骤，应用就可以使用SSL证书对访问后台进行签名并确保通信安全。请注意，生成SSL证书和配置SSL证书的具体细节可能会因为
网络请求库的不同而有所不同。*/
public class OkhttpPresenter {
    /**
     * 通过ssl证书来保证安全
     */
    public void requestWithSSL(Context context) {
        try {
            // 加载SSL证书
            InputStream keyStoreInputStream = context.getAssets().open("your_certificate.keystore");
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(keyStoreInputStream, "your_password".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // 创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            // 配置到网络请求
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory())
                    .build();
        } catch (Exception e) {
        }
    }

    /**
     * 通过签名来保证安全
     */
    public void requestWithSign(Map<String, String> params) {
        // 生成签名数据
        String signature = generateSignature(params);

        // 创建OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // 构建请求
        Request request = new Request.Builder()
                .url("https://example.com/api/endpoint")
                .addHeader("Signature", signature)
                .build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 处理响应
                String responseData = response.body().string();
                System.out.println(responseData);
            }
        });
    }

    //**********************************************************************************************
    private String generateSignature(Map<String, String> requestParams) {
        // 将请求参数按照参数名进行字典序排序
        List<String> sortedKeys = new ArrayList<>(requestParams.keySet());
        Collections.sort(sortedKeys);

        // 拼接排序后的参数字符串
        StringBuilder sb = new StringBuilder();
        for (String key : sortedKeys) {
            sb.append(key).append("=").append(requestParams.get(key)).append("&");
        }

        // 添加密钥
        String secretKey = "your_secret_key";
        sb.append(secretKey);

        // 使用HMAC-SHA256算法计算签名
        String signature = calculateHmacSha256(sb.toString(), secretKey);

        return signature;
    }

    private String calculateHmacSha256(String data, String key) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
