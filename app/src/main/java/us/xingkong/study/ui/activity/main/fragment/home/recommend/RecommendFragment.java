package us.xingkong.study.ui.activity.main.fragment.home.recommend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.xingkong.study.R;
import us.xingkong.study.R2;
import us.xingkong.study.ui.activity.main.fragment.home.MySimpleAdapter;
import us.xingkong.study.utils.Utils;

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
        model.getData(new RecommendViewModel.onGetDataCallback() {
            @Override
            public void onSuccess(ArrayList<Map<String, Object>> data) {
                requireActivity().runOnUiThread(() -> {
                    listView.setAdapter(new MySimpleAdapter(requireActivity(), data, R.layout.msg_item
                            , new String[]{"userName", "content", "likes"}
                            , new int[]{R.id.user_name, R.id.content, R.id.likes}));
                });
            }

            @Override
            public void onFiled(String msg) {
                Utils.showSnack(requireView(), msg);
            }
        });
    }
}
