package us.xingkong.study.ui.activity.main.fragment.read;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import us.xingkong.study.R;

public class ReadFragment extends Fragment {

    private ReadViewModel readViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        readViewModel = new ViewModelProvider(this).get(ReadViewModel.class);
        View root = inflater.inflate(R.layout.fragment_read, container, false);
        return root;
    }
}