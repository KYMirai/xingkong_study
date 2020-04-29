package us.xingkong.study.ui.activity.main.fragment.home.subscribe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubscribeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SubscribeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    LiveData<String> getText() {
        return mText;
    }
}