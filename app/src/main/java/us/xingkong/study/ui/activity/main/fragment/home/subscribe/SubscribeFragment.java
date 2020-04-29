package us.xingkong.study.ui.activity.main.fragment.home.subscribe;

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

public class SubscribeFragment extends Fragment {
    private SubscribeViewModel subscribeViewModel;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        subscribeViewModel = new ViewModelProvider(this).get(SubscribeViewModel.class);
        root = inflater.inflate(R.layout.fragment_subscribe, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    private void initView() {
    }
}
