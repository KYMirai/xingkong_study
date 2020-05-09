package us.xingkong.study.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.alibaba.fastjson.util.IOUtils.close;

public class HttpUtils {
    public interface Callback {
        void onSuccess(String response);

        void onFailed(int code, Exception e);
    }

    private static class MyHandler extends Handler {
        private HttpUtils.Callback callback;

        MyHandler(HttpUtils.Callback callback) {
            super();
            this.callback = callback;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                callback.onSuccess((String) msg.obj);
            } else {
                callback.onFailed(msg.what, (Exception) msg.obj);
            }
        }
    }

    /**
     * 异步get
     */
    public static void get(String url, Callback callback) {
        MyHandler handler = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            handler = new MyHandler(callback);
        }
        MyHandler finalHandler = handler;
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            try {
                urlConnection = (HttpURLConnection) new URL(url).openConnection();
                //设置请求方法
                urlConnection.setRequestMethod("GET");
                //设置超时时间
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                //获取响应的状态码
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);
                    reader = new BufferedReader(inputStreamReader);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    if (finalHandler != null) {
                        finalHandler.handleMessage(finalHandler.obtainMessage(200, response.toString()));
                    } else {
                        callback.onSuccess(response.toString());
                    }
                } else {
                    Exception e = new Exception("连接失败");
                    //e.printStackTrace();
                    if (finalHandler != null) {
                        finalHandler.handleMessage(finalHandler.obtainMessage(responseCode, e));
                    } else {
                        callback.onFailed(responseCode, e);
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                if (finalHandler != null) {
                    finalHandler.handleMessage(finalHandler.obtainMessage(-1, e));
                } else {
                    callback.onFailed(-1, e);
                }
            } finally {
                close(inputStream);
                close(inputStreamReader);
                close(reader);
                if (urlConnection != null) urlConnection.disconnect();
            }
        }).start();
    }

    /**
     * 同步get
     */
    public static String get(String url) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            //设置请求方法
            urlConnection.setRequestMethod("GET");
            //设置超时时间
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            //获取响应的状态码
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                return response.toString();
            } else {
                return "null";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(inputStreamReader);
            close(reader);
            if (urlConnection != null) urlConnection.disconnect();
        }
        return "null";
    }
}