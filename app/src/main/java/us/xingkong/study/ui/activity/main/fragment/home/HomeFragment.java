package us.xingkong.study.ui.activity.main.fragment.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextClassifier;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.ui.activity.main.fragment.home.recommend.RecommendFragment;
import us.xingkong.study.ui.activity.main.fragment.home.subscribe.SubscribeFragment;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    @BindView(R2.id.smartTabLayout)
    SmartTabLayout smartTabLayout;
    @BindView(R2.id.viewPager)
    ViewPager viewPager;
    private View root;
    private float fontScale;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);
        fontScale = getResources().getDisplayMetrics().scaledDensity;
        initView();
        return root;
    }

    private void initView() {
        Fragment[] fragments = new Fragment[]{new RecommendFragment(), new SubscribeFragment(),};
        String[] titles = new String[]{"推荐", "关注"};
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public int getCount() {
                return fragments.length;
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        smartTabLayout.setCustomTabView((container, position, adapter) -> {
            try {
                Method method = SmartTabLayout.class.getDeclaredMethod("createDefaultTabView", CharSequence.class);
                method.setAccessible(true);
                TextView textView = (TextView) method.invoke(smartTabLayout, adapter.getPageTitle(position));
                if (textView != null) {
                    updateTextView(textView, position);
                    return textView;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSmartTab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        smartTabLayout.setViewPager(viewPager);
        ((LinearLayout) smartTabLayout.getTabAt(0).getParent()).setGravity(Gravity.CENTER);
    }

    /**
     * 设置tab栏样式
     */
    private void setSmartTab() {
        for (int i = 0; i < viewPager.getChildCount(); ++i) {
            View view = smartTabLayout.getTabAt(i);
            updateTextView((TextView) view, i);
        }
    }

    /**
     * 更新tab栏样式
     */
    private void updateTextView(TextView textView, int position) {
        if (position == viewPager.getCurrentItem()) {
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            createAnimator(textView, 20).start();
        } else {
            textView.setTextColor(Color.GRAY);
            textView.setTypeface(Typeface.DEFAULT);
            createAnimator(textView, 17).start();
        }
    }

    /**
     * TextView文字大小渐进动画
     */
    private ValueAnimator createAnimator(final TextView v, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(v.getTextSize() / fontScale, end);
        //animator.setDuration(150);
        animator.addUpdateListener(arg0 -> {
            v.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) arg0.getAnimatedValue());
        });
        return animator;
    }
}
