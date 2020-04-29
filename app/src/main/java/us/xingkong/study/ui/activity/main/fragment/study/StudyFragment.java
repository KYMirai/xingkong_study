package us.xingkong.study.ui.activity.main.fragment.study;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import us.xingkong.study.R;

public class StudyFragment extends Fragment {

    private StudyViewModel studyViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        studyViewModel = new ViewModelProvider(this).get(StudyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_study, container, false);
        ((TextView)root.findViewById(R.id.title)).setTypeface(Typeface.DEFAULT_BOLD);
        return root;
    }
}