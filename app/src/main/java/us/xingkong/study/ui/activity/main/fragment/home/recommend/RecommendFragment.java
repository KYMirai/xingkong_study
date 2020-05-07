package us.xingkong.study.ui.activity.main.fragment.home.recommend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.xingkong.study.R;
import us.xingkong.study.R2;

public class RecommendFragment extends Fragment {
    private RecommendViewModel model;
    private View root;
    @BindView(R2.id.list)
    ListView listView;
    @BindView(R2.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        model = new ViewModelProvider(this).get(RecommendViewModel.class);
        root = inflater.inflate(R.layout.fragment_recommend, container, false);
        ButterKnife.bind(this, root);
        initView();
        return root;
    }

    private void initView() {
        initList();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            initList();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void initList() {
        listView.setAdapter(new SimpleAdapter(root.getContext(), model.getData(), R.layout.msg_item, new String[]{}, new int[]{}));
    }
}
