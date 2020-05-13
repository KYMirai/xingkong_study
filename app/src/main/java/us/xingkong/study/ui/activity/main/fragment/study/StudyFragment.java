package us.xingkong.study.ui.activity.main.fragment.study;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.ui.activity.main.MainActivity;
import us.xingkong.study.ui.activity.web.WebActivity;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static us.xingkong.study.ui.activity.main.MainActivity.login;
import static us.xingkong.study.utils.UserUtil.isLogin;
import static us.xingkong.study.utils.UserUtil.userMine;
import static us.xingkong.study.utils.Utils.server;

public class StudyFragment extends Fragment {

    private StudyViewModel studyViewModel;
    @BindView(R2.id.day_position)
    LinearLayout position;
    @BindView(R2.id.day_bg)
    CardView dayBg;
    @BindView(R2.id.day)
    TextView dayText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        studyViewModel = new ViewModelProvider(this).get(StudyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_study, container, false);
        ButterKnife.bind(this, root);
        ((TextView) root.findViewById(R.id.title)).setTypeface(Typeface.DEFAULT_BOLD);
        initView();
        return root;
    }

    private void initView() {
        if (userMine != null) {
            sign(userMine.day);
        } else {
            sign(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R2.id.sign)
    void signClick(View view) {
        if (isLogin) {
            HttpUtils.get("http://" + server + "/api/sign.php?gcuId=" + userMine.gcuId + "&at=" + userMine.at, true, new HttpUtils.Callback() {
                @Override
                public void onSuccess(String response) {
                    JSONObject res = JSON.parseObject(response);
                    if (res != null) {
                        if (res.getBoolean("success")) {
                            requireActivity().runOnUiThread(() -> sign(res.getJSONObject("data").getIntValue("day")));
                        }
                        Utils.showSnack(view, res.getString("msg"));
                    }
                }

                @Override
                public void onFailed(int code, Exception e) {
                    e.printStackTrace();
                    Utils.showSnack(view, "网络异常");
                }
            });
        } else {
            Utils.showSnack(requireView(), "未登录", "前往登录", view1 -> login.run());
        }
    }

    //http://sc.gcu.edu.cn/space/c/rest/app/getlightappurl?appId=1011313
    //1011313 一卡通
    //1011177 课表

    @OnClick(R2.id.kecheng)
    void openKe(View view) {
        if (isLogin && userMine != null) {
            //startActivity(new Intent(requireContext(), WebActivity.class).putExtra("url", userMine.lightAppUrl));
            HttpUtils.get("http://sc.gcu.edu.cn/space/c/rest/app/getlightappurl?appId=1011177", false, "at=" + userMine.at + ";", new HttpUtils.Callback() {
                @Override
                public void onSuccess(String response) {
                    JSONObject res = JSON.parseObject(response);
                    if (res != null) {
                        if (res.getBoolean("success")) {
                            userMine.lightAppUrl = res.getString("lightAppUrl");
                            Utils.sharedPreferences.edit().putString("lightAppUrl", userMine.lightAppUrl).apply();
                            startActivity(new Intent(requireContext(), WebActivity.class).putExtra("url", userMine.lightAppUrl));
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                if (TextUtils.isEmpty(userMine.lightAppUrl)) {
                                    Utils.showSnack(requireView(), res.getString("msg"));
                                } else {
                                    startActivity(new Intent(requireContext(), WebActivity.class).putExtra("url", userMine.lightAppUrl));
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailed(int code, Exception e) {
                    e.printStackTrace();
                    //Utils.showSnack(view, "网络异常，可能登录已失效");
                    if (TextUtils.isEmpty(userMine.lightAppUrl)) {
                        Utils.showSnack(requireView(), "网络异常，可能登录已失效");
                    } else {
                        startActivity(new Intent(requireContext(), WebActivity.class).putExtra("url", userMine.lightAppUrl));
                    }
                }
            });
        } else {
            Utils.showSnack(requireView(), "未登录", "前往登录", view1 -> login.run());
        }
    }

    private void sign(int day) {
        if ((day > 0 ? "第 " + day + " 天" : "未 开 始").equals(dayText.getText().toString())) return;
        final boolean[] tmp = {true};
        createDropAnimator(position, position.getMeasuredWidth(), Utils.dip2px(requireContext(), day < 1 ? 0 : 23 + (((day - 1) % 7) + 1) * 42)).start();
        ValueAnimator animator = ValueAnimator.ofInt(dayBg.getMeasuredWidth(), 0, Utils.dip2px(dayBg.getContext(), 100));
        animator.addUpdateListener(arg0 -> {
            int value = (int) arg0.getAnimatedValue();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) dayBg.getLayoutParams();
            if (tmp[0] && value > layoutParams.width) {
                tmp[0] = false;
                dayText.setText(day > 0 ? "第 " + day + " 天" : "未 开 始");
            }
            layoutParams.width = value;
            dayBg.setLayoutParams(layoutParams);
        });
        animator.setDuration(600);
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View v, int... values) {
        ValueAnimator animator = ValueAnimator.ofInt(values);
        animator.addUpdateListener(arg0 -> {
            int value = (int) arg0.getAnimatedValue();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
            layoutParams.width = value;
            v.setLayoutParams(layoutParams);
        });
        animator.setDuration(700);
        return animator;
    }
}