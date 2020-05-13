package us.xingkong.study.ui.activity.main.fragment.me;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.bean.User;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static us.xingkong.study.utils.UserUtil.isLogin;
import static us.xingkong.study.utils.UserUtil.userMine;
import static us.xingkong.study.utils.Utils.log;
import static us.xingkong.study.utils.Utils.server;
import static us.xingkong.study.utils.Utils.sharedPreferences;

public class MeFragment extends Fragment {
    private MeViewModel meViewModel;

    @BindView(R2.id.name)
    TextView loginButton;
    @BindView(R2.id.subscribe)
    TextView subscribe;
    @BindView(R2.id.fans)
    TextView fans;
    @BindView(R2.id.likes)
    TextView likes;

    @BindView(R2.id.profile_image)
    ImageView imageView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        meViewModel = new ViewModelProvider(this).get(MeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_me, container, false);
        ((TextView) root.findViewById(R.id.title)).setTypeface(Typeface.DEFAULT_BOLD);
        ButterKnife.bind(this, root);
        initView();
        return root;
    }

    private void initView() {
        if (isLogin && userMine != null) {
            loginButton.setText(userMine.nick);
            subscribe.setText(String.valueOf(userMine.subscribe));
            fans.setText(String.valueOf(userMine.fans));
            likes.setText(String.valueOf(userMine.likes));
            Glide.with(imageView)
                    .load(userMine.img)
                    .skipMemoryCache(true)
                    .into(imageView);
        }
    }

    private void rename() {
        final EditText inputServer = new EditText(requireContext());
        inputServer.setText(userMine.nick);
        inputServer.setHint("昵称不可超过12个字");
        inputServer.setMaxEms(12);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("更改昵称").setView(inputServer)//.setIcon(android.R.drawable.ic_dialog_info)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", (dialog, which) -> {
            String nick = inputServer.getText().toString();
            if (!TextUtils.isEmpty(nick)) {
                HttpUtils.get("http://" + server + "/api/rename.php?gcuId=" + userMine.gcuId + "&at=" + userMine.at + "&nick=" + nick, true, new HttpUtils.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject json = JSON.parseObject(response);
                        if (json.getBoolean("success")) {
                            userMine.nick = json.getJSONObject("data").getString("nick");
                            requireActivity().runOnUiThread(() -> loginButton.setText(userMine.nick));
                        }
                        Utils.showSnack(requireView(), json.getString("msg"));
                    }

                    @Override
                    public void onFailed(int code, Exception e) {
                        e.printStackTrace();
                        Utils.showSnack(requireView(), "网络异常");
                    }
                });
            }
        });
        builder.show();
    }

    @OnClick(R2.id.name)
    public void login() {
        if (isLogin) {
            rename();
        } else {
            meViewModel.loginByQR(this);
        }
    }

    @OnClick(R2.id.exit)
    void exitLogin() {
        if (isLogin) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        userMine = null;
                        isLogin = false;
                        sharedPreferences.edit().clear().apply();
                        loginButton.setText("未登录");
                        fans.setText("0");
                        subscribe.setText("0");
                        likes.setText("0");
                    })
                    .show();
        }
    }

    @OnClick(R2.id.profile_image)
    void changeImg() {
        if (isLogin) {
            Utils.showSnack(requireView(), "待开发");
        } else {
            Utils.showSnack(requireView(), "未登录", "前往登录", view1 -> login());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666) {
            meViewModel.checkLogin(new MeViewModel.onLoginCallback() {
                @Override
                public void onSuccess(User userMine) {
                    setTopApp();
                    isLogin = true;
                    if (sharedPreferences != null) {
                        sharedPreferences.edit()
                                .putString("gcuId", userMine.gcuId)
                                .putString("at", userMine.at)
                                .putString("lightAppUrl", userMine.lightAppUrl)
                                .putString("img", userMine.img)
                                .apply();
                    }
                    requireActivity().runOnUiThread(() -> {
                        loginButton.setText(userMine.nick);
                        subscribe.setText(String.valueOf(userMine.subscribe));
                        fans.setText(String.valueOf(userMine.fans));
                        likes.setText(String.valueOf(userMine.likes));
                        Glide.with(requireView())
                                .load(userMine.img)
                                .skipMemoryCache(true)
                                .into(imageView);
                    });
                }

                @Override
                public void onFiled() {

                }
            });
        }
    }

    /**
     * 切换回前台
     */
    private void setTopApp() {
        try {
            ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                activityManager.moveTaskToFront(requireActivity().getTaskId(), ActivityManager.MOVE_TASK_NO_USER_ACTION);
            } else {
                log("activityManager is null!");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
