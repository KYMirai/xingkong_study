package us.xingkong.study.ui.activity.main.fragment.home.recommend;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import butterknife.ButterKnife;
import us.xingkong.study.R;

public class RecommendFragment extends Fragment {
    private RecommendViewModel recommendViewModel;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recommendViewModel = new ViewModelProvider(this).get(RecommendViewModel.class);
        root = inflater.inflate(R.layout.fragment_recommend, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    private void initView() {
    }
}
