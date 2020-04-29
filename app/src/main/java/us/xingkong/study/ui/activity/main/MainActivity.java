package us.xingkong.study.ui.activity.main;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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
import us.xingkong.study.utils.Utils;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity {
    private static Boolean isExit = false;
    @BindView(R2.id.nav_view)
    BottomNavigationView navView;
    @BindView(R2.id.view_pager)
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            Utils.showSnack(navView, "再按一次退出");
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
