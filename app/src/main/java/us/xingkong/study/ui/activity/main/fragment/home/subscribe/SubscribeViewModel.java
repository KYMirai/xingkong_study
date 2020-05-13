package us.xingkong.study.ui.activity.main.fragment.home.subscribe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import us.xingkong.study.R;

public class SubscribeViewModel extends ViewModel {


    public SubscribeViewModel() {
    }

    ArrayList<Map<String, Object>> getData() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("image", R.drawable.demo_img);
        map.put("likes", "40");
        map.put("head", R.drawable.demo_img);
        map.put("userName", "Seek");
        map.put("content", "pth660 首日板绘过程分享~");
        list.add(map);

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("image", R.drawable.demo_img_2);
        map2.put("head", R.drawable.demo_img_2);
        map2.put("likes", "12");
        map2.put("userName", "Mob");
        map2.put("content", "勾线技巧分享");
        list.add(map2);

        return list;
    }
}