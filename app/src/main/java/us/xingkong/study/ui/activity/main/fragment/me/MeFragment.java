package us.xingkong.study.ui.activity.main.fragment.me;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.ui.activity.main.MainActivity;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static android.content.Context.ACTIVITY_SERVICE;

public class MeFragment extends Fragment {

    private MeViewModel meViewModel;
    private String token;

    @BindView(R2.id.login)
    Button loginButton;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        meViewModel = new ViewModelProvider(this).get(MeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, root);
        initView();
        return root;
    }

    private void initView() {

    }

    @OnClick(R2.id.login)
    void login() {
        HttpUtils.get("http://" + Utils.server + "/api/getloginercode.php", true, new HttpUtils.Callback() {
            @Override
            public void onSuccess(String response) {
                JSONObject tmp = JSON.parseObject(response);
                token = tmp.getJSONObject("data").getString("token");
                if (tmp.getBoolean("success")) {
                    if (!TextUtils.isEmpty(token)) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        ComponentName componentName = new ComponentName("com.hnlg.kdweibo.client", "com.kingdee.xuntong.lightapp.runtime.view.NewsWebViewActivity");
                        intent.setComponent(componentName);
                        intent.putExtra("webviewUrl", "http://" + Utils.server + "/login.php?token=" + token);
                        intent.putExtra("titleName", "登录");
                        startActivityForResult(intent, 666);
                    }
                }
            }

            @Override
            public void onFailed(int code, Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666) {
            Timer timer = new Timer();
            final int[] count = {10};
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if ((--count[0]) < 1) timer.cancel();
                    try {
                        String result = HttpUtils.get("http://" + Utils.server + "/api/checkloginstatus.php?token=" + token, true);
                        JSONObject json = JSON.parseObject(result);
                        if (json.getBoolean("success")) {
                            setTopApp();
                            getActivity().runOnUiThread(() -> {
                                loginButton.setText(json.getJSONObject("data").getString("name"));
                            });
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 2 * 1000);
        }
    }

    private void setTopApp() {
        try {
            ActivityManager activtyManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            activtyManager.moveTaskToFront(getActivity().getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
