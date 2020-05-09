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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.bean.User;
import us.xingkong.study.ui.activity.main.MainActivity;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static android.content.Context.ACTIVITY_SERVICE;

public class MeFragment extends Fragment {
    private MeViewModel meViewModel;

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
        meViewModel.loginByQR(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666) {
            meViewModel.checkLogin(new MeViewModel.onLoginCallback() {
                @Override
                public void onSuccess(User userMine) {
                    setTopApp();
                    requireActivity().runOnUiThread(() -> loginButton.setText(userMine.nick));
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
            ActivityManager activtyManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
            if (activtyManager != null) {
                activtyManager.moveTaskToFront(requireActivity().getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
