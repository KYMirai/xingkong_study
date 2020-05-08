package us.xingkong.study.ui.activity.main.fragment.study;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.utils.Utils;

public class StudyFragment extends Fragment {

    private StudyViewModel studyViewModel;
    @BindView(R2.id.day_position)
    LinearLayout position;
    @BindView(R2.id.day_bg)
    CardView dayBg;
    @BindView(R2.id.day)
    TextView dayText;
    private int day = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        studyViewModel = new ViewModelProvider(this).get(StudyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_study, container, false);
        ButterKnife.bind(this, root);
        ((TextView) root.findViewById(R.id.title)).setTypeface(Typeface.DEFAULT_BOLD);
        return root;
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R2.id.sign)
    void sign(View view) {
        final boolean[] tmp = {true};
        day++;
        createDropAnimator(position, position.getMeasuredWidth(), Utils.dip2px(view.getContext(), 23 + (((day - 1) % 7) + 1) * 42)).start();
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