package us.xingkong.study.ui.activity.main;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Constructor;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.ui.activity.main.fragment.read.ReadFragment;
import us.xingkong.study.ui.activity.main.fragment.home.HomeFragment;
import us.xingkong.study.ui.activity.main.fragment.me.MeFragment;
import us.xingkong.study.ui.activity.main.fragment.study.StudyFragment;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity {
    private static Boolean isExit = false;
    @BindView(R2.id.nav_view)
    BottomNavigationView navView;
    @BindView(R2.id.view_pager)
    ViewPager pager;
    @BindView(R2.id.coordinator)
    CoordinatorLayout coordinator;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();

        //test();
    }

//    public void test2() {
//        try {
////            Context othercontext = createPackageContext("com.hnlg.kdweibo.client", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);//根据Context取得对应的SharedPreferences
////            SharedPreferences sp = othercontext.getSharedPreferences("EMP_SHELL_SP_KEY", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
////            Class clazz = othercontext.getClassLoader().loadClass("com.kingdee.emp.shell.a.a");
////            Constructor declaredConstructor = clazz.getDeclaredConstructor();
////            declaredConstructor.setAccessible(true);
////            Object object = declaredConstructor.newInstance();
////            String text = (String) clazz.getMethod("getOpenToken").invoke(object);
////
////            Toast.makeText(getApplicationContext(), "T:" + text, Toast.LENGTH_SHORT).show();
//            Context othercontext = createPackageContext("com.hnlg.kdweibo.client", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);//根据Context取得对应的SharedPreferences
//            //Class clazz = othercontext.getClassLoader().loadClass("com.kingdee.eas.eclite.ui.EContactApplication");
//
//            SharedPreferences sp = othercontext.getSharedPreferences("EMP_SHELL_SP_KEY", MODE_WORLD_WRITEABLE);
//            sp.edit().putString("xt_me_name", "123").commit();
//            Toast.makeText(getApplicationContext(), "T:" + sp.getString("xt_me_name", "???"), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public void test() {
        HttpUtils.get("http://test.kymirai.xyz:666/api/getloginercode.php", true, new HttpUtils.Callback() {
            @Override
            public void onSuccess(String response) {
                JSONObject tmp = JSON.parseObject(response);
                token = tmp.getJSONObject("data").getString("token");
                if (tmp.getBoolean("success")) {
                    if (!TextUtils.isEmpty(token)) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        ComponentName componentName = new ComponentName("com.hnlg.kdweibo.client", "com.kingdee.xuntong.lightapp.runtime.view.NewsWebViewActivity");
                        intent.setComponent(componentName);
                        intent.putExtra("webviewUrl", "http://test.kymirai.xyz:666/login.php?token=" + token);
                        //System.out.println(token);
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
        int[] ids = new int[]{R.id.navigation_home, R.id.navigation_study, R.id.navigation_read, R.id.navigation_me};
        pager.setAdapter(new AppPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                new Fragment[]{new HomeFragment(), new StudyFragment(), new ReadFragment(), new MeFragment()}));
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
