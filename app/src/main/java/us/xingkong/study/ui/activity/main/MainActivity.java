package us.xingkong.study.ui.activity.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.bean.User;
import us.xingkong.study.ui.activity.main.fragment.home.HomeFragment;
import us.xingkong.study.ui.activity.main.fragment.me.MeFragment;
import us.xingkong.study.ui.activity.main.fragment.study.StudyFragment;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static us.xingkong.study.utils.UserUtil.isLogin;
import static us.xingkong.study.utils.UserUtil.userMine;
import static us.xingkong.study.utils.Utils.sharedPreferences;

public class MainActivity extends AppCompatActivity {
    private static Boolean isExit = false;
    @BindView(R2.id.nav_view)
    BottomNavigationView navView;
    @BindView(R2.id.view_pager)
    ViewPager pager;
    @BindView(R2.id.coordinator)
    CoordinatorLayout coordinator;
    private String token;
    public static Runnable login;
    private MeFragment meFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色白色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.WHITE);
            // 设置状态栏字体黑色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        int[] ids = new int[]{R.id.navigation_home, R.id.navigation_study, /*R.id.navigation_read,*/ R.id.navigation_me};
        meFragment = new MeFragment();
        pager.setAdapter(new AppPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                new Fragment[]{new HomeFragment(), new StudyFragment(), /*new ReadFragment(),*/ meFragment}));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navView.setSelectedItemId(ids[position]);
                setTitle(navView.getMenu().getItem(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        navView.setOnNavigationItemSelectedListener(item -> {
            pager.setCurrentItem(Utils.getIndex(ids, item.getItemId()));
            setTitle(item.getTitle());
            return true;
        });
        if (sharedPreferences != null) {
            String gcuId = sharedPreferences.getString("gcuId", "");
            String at = sharedPreferences.getString("at", "");
            if (!TextUtils.isEmpty(at) && !TextUtils.isEmpty(gcuId)) {
                HttpUtils.get("http://" + Utils.server + "/api/loginByToken.php?at=" + at + "&gcuId=" + gcuId, true, new HttpUtils.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject res = JSON.parseObject(response);
                        if (res != null && res.getBoolean("success")) {
                            userMine = JSON.toJavaObject(res.getJSONObject("data"), User.class);
                            userMine.lightAppUrl = sharedPreferences.getString("lightAppUrl", "");
                            userMine.img = "http://test.kymirai.xyz:666/tmp/" + userMine.img;
                            isLogin = true;
                        }
                    }

                    @Override
                    public void onFailed(int code, Exception e) {

                    }
                });
            }
        }
        login = () -> {
            pager.setCurrentItem(3);
            meFragment.login();
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            exitBy2Click();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 双击退出函数
     */
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            Utils.showSnack(coordinator, "再按一次退出");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
            System.exit(0);
        }
    }
}
