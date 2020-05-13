package us.xingkong.study.ui.activity.main.fragment.me;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import us.xingkong.study.bean.User;
import us.xingkong.study.ui.activity.main.fragment.study.StudyFragment;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static us.xingkong.study.utils.UserUtil.userMine;

public class MeViewModel extends ViewModel {
    interface onLoginCallback {
        void onSuccess(User userMine);

        void onFiled();
    }

    private String token;

    public MeViewModel() {

    }

    /**
     * 通过接口判断时候登录成功
     */
    void checkLogin(onLoginCallback callback) {
        new Thread(() -> {
            int count = 10;
            while (--count > 0) {
                try {
                    String result = HttpUtils.get("http://" + Utils.server + "/api/checkloginstatus.php?token=" + token, true);
                    JSONObject json = JSON.parseObject(result);
                    if (json.getBoolean("success")) {
                        userMine = JSON.toJavaObject(json.getJSONObject("data"), User.class);
                        userMine.img = "http://test.kymirai.xyz:666/tmp/" + userMine.img;
                        callback.onSuccess(userMine);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取登录请求的token，并跳转智慧校园
     */
    void loginByQR(MeFragment fragment) {
        HttpUtils.get("http://" + Utils.server + "/api/getloginercode.php", true, new HttpUtils.Callback() {
            @Override
            public void onSuccess(String response) {
                JSONObject tmp = JSON.parseObject(response);
                if (tmp != null) {
                    if (tmp.getBoolean("success")) {
                        token = tmp.getJSONObject("data").getString("token");
                        if (!TextUtils.isEmpty(token)) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            ComponentName componentName = new ComponentName("com.hnlg.kdweibo.client", "com.kingdee.xuntong.lightapp.runtime.view.NewsWebViewActivity");
                            intent.setComponent(componentName);
                            intent.putExtra("webviewUrl", "http://" + Utils.server + "/login.php?token=" + token);
                            intent.putExtra("titleName", "登录");
                            fragment.startActivityForResult(intent, 666);
                        }
                    } else {
                        fragment.requireActivity().runOnUiThread(() -> {
                            Utils.showSnack(fragment.requireView(), tmp.getString("msg"));
                        });
                    }
                }
            }

            @Override
            public void onFailed(int code, Exception e) {
                e.printStackTrace();
            }
        });
    }
}