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
import static us.xingkong.study.utils.Utils.log;

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
    public static void get(String url, boolean addSign, Callback callback) {
        get(url, addSign, "", callback);
    }

    public static void get(String url, boolean addSign, String cookie, Callback callback) {
        MyHandler handler = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            handler = new MyHandler(callback);
        }
        MyHandler finalHandler = handler;
        new Thread(() -> {
            String newUrl = addSign ? addSign(url) : url;
            log(newUrl);
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            try {
                urlConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                //设置请求方法
                urlConnection.setRequestMethod("GET");
                //设置超时时间
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                urlConnection.setRequestProperty("Accept", "*/*");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8,en;q=0.7");
                urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                urlConnection.setRequestProperty("Cookie", cookie);
                log("Cookie:" + cookie);

                //获取响应的状态码
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);
                    reader = new BufferedReader(inputStreamReader);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    log(response.toString());
                    if (finalHandler != null) {
                        finalHandler.handleMessage(finalHandler.obtainMessage(200, response.toString()));
                    } else {
                        callback.onSuccess(response.toString());
                    }
                } else {
                    Exception e = new Exception("连接失败:" + responseCode);
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
     * 计算并添加sign参数
     *
     * @param url url
     * @return url+sign
     */
    private static String addSign(String url) {
        long timestamp = 0;
        try {
            timestamp = Long.parseLong(get("http://" + Utils.server + "/api/time.php", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timestamp < 1000) {
            timestamp = System.currentTimeMillis() / 1000;
        }
        String sign = Utils.md5(timestamp + "123456");
        return url + ((url.contains("?")) ? "&timestamp=" : "?timestamp=") + timestamp + "&sign=" + sign;
    }

    /**
     * 同步get
     */
    public static String get(String url, boolean addSign) throws Exception {
        String newUrl = addSign ? addSign(url) : url;
        log(newUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(newUrl).openConnection();
        //设置请求方法
        urlConnection.setRequestMethod("GET");
        //设置超时时间
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);

        //获取响应的状态码
        if (urlConnection.getResponseCode() == 200) {
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            close(inputStream);
            close(inputStreamReader);
            close(reader);
            urlConnection.disconnect();
            log(response.toString());
            return response.toString();
        } else {
            throw new Exception("net error!");
        }
    }
}