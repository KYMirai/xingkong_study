package us.xingkong.study.ui.activity.main.fragment.home.recommend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecommendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecommendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    LiveData<String> getText() {
        return mText;
    }
}