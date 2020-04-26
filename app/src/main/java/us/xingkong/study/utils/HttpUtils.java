package us.xingkong.study.utils;

import android.os.Handler;
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

        void onFaild(int code, Exception e);
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
                callback.onFaild(msg.what, (Exception) msg.obj);
            }
        }
    }

    /**
     * 异步get
     */
    public static void get(String url, Callback callback) {
        MyHandler handler = new MyHandler(callback);
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
                    handler.handleMessage(handler.obtainMessage(1, response.toString()));
                } else {
                    Exception e = new Exception("连接失败");
                    e.printStackTrace();
                    handler.handleMessage(handler.obtainMessage(responseCode, e));
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.handleMessage(handler.obtainMessage(-1, e));
            } finally {
                close(inputStream);
                close(inputStreamReader);
                close(reader);
                if (urlConnection != null) urlConnection.disconnect();
            }
        });
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
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(inputStreamReader);
            close(reader);
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }
}