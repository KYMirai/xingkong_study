package us.xingkong.study.ui.activity.main.fragment.home.recommend;

import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import us.xingkong.study.R;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static us.xingkong.study.utils.Utils.server;

public class RecommendViewModel extends ViewModel {
    interface onGetDataCallback {
        void onSuccess(ArrayList<Map<String, Object>> data);

        void onFiled(String msg);
    }

    public RecommendViewModel() {
    }

    void getData(onGetDataCallback callback) {
        HttpUtils.get("http://" + server + "/api/getTrends.php", true, new HttpUtils.Callback() {
            @Override
            public void onSuccess(String response) {
                JSONArray res = JSON.parseArray(response);
                if (res != null) {
                    ArrayList<Map<String, Object>> list = new ArrayList<>();
                    for (int i = 0; i < res.size(); i++) {
                        JSONObject tmp = res.getJSONObject(i);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", "http://" + server + "/tmp/" + tmp.getString("imgUrl"));
                        map.put("head", "http://" + server + "/tmp/" + tmp.getString("senderImg"));
                        map.put("userName", tmp.getString("senderNick"));
                        //map.put("userImg", tmp.getString("senderImg"));
                        map.put("likes", tmp.getString("likes"));
                        map.put("postId", tmp.getString("id"));
                        map.put("content", tmp.getString("content"));
                        map.put("comment", tmp.getJSONArray("comment"));
                        list.add(map);
                    }
                    callback.onSuccess(list);
                }
            }

            @Override
            public void onFailed(int code, Exception e) {
                callback.onFiled("网络异常");
            }
        });
    }

//    ArrayList<Map<String, Object>> getData() {
//        ArrayList<Map<String, Object>> list = new ArrayList<>();
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("image", R.drawable.demo_img);
//        map.put("head", R.drawable.demo_img);
//        map.put("userName", "Seek");
//        map.put("likes", "54");
//        map.put("content", "pth660 首日板绘过程分享~");
//        map.put("comment", "[{\"name\":\"Lily\",\"comment\":\"是大佬！\"}," +
//                "{\"name\":\"Mob\",\"comment\":\"膜拜\"}," +
//                "{\"name\":\"John\",\"comment\":\"(๑•̀ㅂ•́)و✧\"}," +
//                "{\"name\":\"NN\",\"comment\":\"膜拜\"}," +
//                "{\"name\":\"MeMe\",\"comment\":\"大佬！\"}," +
//                "{\"name\":\"Kit\",\"comment\":\"！！！\"}]");
//        list.add(map);
//
//        HashMap<String, Object> map2 = new HashMap<>();
//        map2.put("image", R.drawable.demo_img_2);
//        map2.put("head", R.drawable.demo_img_2);
//        map2.put("userName", "Mob");
//        map2.put("likes", "33");
//        map2.put("content", "勾线技巧分享");
//        map2.put("comment", "[{\"name\":\"Hu\",\"comment\":\"i了i了\"}," +
//                "{\"name\":\"Ci\",\"comment\":\"膜拜大佬\"}," +
//                "{\"name\":\"Bily\",\"comment\":\"这个APP太棒了\"}," +
//                "{\"name\":\"Ki\",\"comment\":\"又一个大佬！\"}," +
//                "{\"name\":\"OOO\",\"comment\":\"大佬！\"}," +
//                "{\"name\":\"John\",\"comment\":\"！！！\"}]");
//        list.add(map2);
//
//        return list;
//    }
}