package us.xingkong.study.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by debbytang.
 * Description:显示隐藏布局的属性动画(铺展)
 * Date:2017/3/30.
 */
public class HiddenAnimUtils {

    //private int mHeight;//伸展高度
    private View hideView;
    @Nullable
    private TextView down;//需要展开隐藏的布局，开关控件

    /**
     * 构造器(可根据自己需要修改传参)
     *
     * @param hideView 需要隐藏或显示的布局view
     * @param down     按钮开关的view
     *                 //@param height   布局展开的高度(根据实际需要传)
     */
    public static HiddenAnimUtils newInstance(View hideView, @Nullable TextView down) {
        return new HiddenAnimUtils(hideView, down);
    }

    private HiddenAnimUtils(View hideView, @Nullable TextView down) {
        this.hideView = hideView;
        this.down = down;
        //mHeight = (int) (mDensity * height + 0.5);//伸展高度
    }

    /**
     * 开关
     */
    public boolean toggle() {
        startAnimation();
        if (View.VISIBLE == hideView.getVisibility()) {
            closeAnimate(hideView);//布局隐藏
            return false;
        } else {
            openAnim(hideView);//布局铺开
            return true;
        }
    }

    public void toggle(boolean b) {
        if (b && View.GONE == hideView.getVisibility()) {
            startAnimation();
            openAnim(hideView);//布局铺开
        } else if (!b && View.VISIBLE == hideView.getVisibility()) {
            startAnimation();
            closeAnimate(hideView);//布局铺开
        }
    }

    /**
     * 开关旋转动画
     */
    private void startAnimation() {
        if (down != null) {
            if ("查看更多评论".equals(down.getText().toString())) {
                down.setText("收起评论");
            } else {
                down.setText("查看更多评论");
            }
        }

//        if (down != null) {
//            //旋转动画
//            RotateAnimation animation;
//            if (View.VISIBLE != hideView.getVisibility()) {
//                animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            } else {
//                animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            }
//            animation.setDuration(250);//设置动画持续时间
//            animation.setInterpolator(new LinearInterpolator());
//            animation.setRepeatMode(Animation.REVERSE);//设置反方向执行
//            animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
//            down.startAnimation(animation);
//        }
    }

    private void openAnim(View v) {
        v.setVisibility(View.VISIBLE);
        DisplayMetrics dm = v.getContext().getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.AT_MOST));
        ValueAnimator animator = createDropAnimator(v, 0, v.getMeasuredHeight());
        animator.start();
    }

    private void closeAnimate(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (start < end) {
                    v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.addUpdateListener(arg0 -> {
            int value = (int) arg0.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = value;
            v.setLayoutParams(layoutParams);
        });
        return animator;
    }
}