package us.xingkong.study.ui.activity.main.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import us.xingkong.study.R;
import us.xingkong.study.utils.HiddenAnimUtils;
import us.xingkong.study.utils.HttpUtils;
import us.xingkong.study.utils.Utils;

import static us.xingkong.study.ui.activity.main.MainActivity.login;
import static us.xingkong.study.utils.UserUtil.isLogin;
import static us.xingkong.study.utils.UserUtil.userMine;
import static us.xingkong.study.utils.Utils.server;

public class MySimpleAdapter extends SimpleAdapter {
    private List<? extends Map<String, ?>> data;
    private Activity activity;

    public MySimpleAdapter(Activity context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.activity = context;
        this.data = data;
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.findViewById(R.id.upIcon).setOnClickListener(view1 -> HiddenAnimUtils.newInstance(view.findViewById(R.id.hideView), (TextView) view1).toggle());
        Object imgUrl = data.get(position).get("image");
        if (imgUrl instanceof String) {
            Glide.with(view)
                    .load(imgUrl)
                    .skipMemoryCache(true)
                    .into((ImageView) view.findViewById(R.id.image));
        } else if (imgUrl != null) {
            ((ImageView) view.findViewById(R.id.image)).setImageResource(Integer.parseInt(String.valueOf(imgUrl)));
        }
        Object head = data.get(position).get("head");
        if (head instanceof String) {
            Glide.with(view)
                    .load(head)
                    .skipMemoryCache(true)
                    .into((ImageView) view.findViewById(R.id.image_user));
        } else if (head != null) {
            ((ImageView) view.findViewById(R.id.image_user)).setImageResource(Integer.parseInt(String.valueOf(head)));
        }
        Object commentString = data.get(position).get("comment");
        JSONArray comments = null;
        if (commentString instanceof String) {
            comments = JSON.parseArray((String) commentString);
        } else if (commentString instanceof JSONArray) {
            comments = (JSONArray) commentString;
        }
        ViewGroup hidnComment = view.findViewById(R.id.hideView);
        ViewGroup firstComment = view.findViewById(R.id.firstComment);
        if (comments != null) {
            firstComment.removeAllViews();
            if (comments.size() > 3) {
                view.findViewById(R.id.upIcon).setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < Math.min(3, comments.size()); i++) {
                JSONObject comment = comments.getJSONObject(i);
                View v = activity.getLayoutInflater().inflate(R.layout.comment_item, null);
                ((TextView) v.findViewById(R.id.user_name)).setText(comment.getString("sendNick"));
                ((TextView) v.findViewById(R.id.comment)).setText(comment.getString("content"));
                firstComment.addView(v);
            }
            hidnComment.removeAllViews();
            for (int i = 3; i < comments.size(); i++) {
                JSONObject comment = comments.getJSONObject(i);
                View v = activity.getLayoutInflater().inflate(R.layout.comment_item, null);
                ((TextView) v.findViewById(R.id.user_name)).setText(comment.getString("sendNick"));
                ((TextView) v.findViewById(R.id.comment)).setText(comment.getString("content"));
                hidnComment.addView(v);
            }
        }
        JSONArray finalComments = comments;
        ((EditText) view.findViewById(R.id.send_comment)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    if (isLogin) {
                        String content = textView.getText().toString();
                        textView.setText("");
                        HttpUtils.get("http://" + server + "/api/sendComment.php?gcuId=" + userMine.gcuId
                                + "&senderId=" + userMine.id
                                + "&postId=" + data.get(position).get("postId")
                                + "&at=" + userMine.at
                                + "&content=" + content, true, new HttpUtils.Callback() {
                            @Override
                            public void onSuccess(String response) {
                                JSONObject res = JSON.parseObject(response);
                                if (res != null) {
                                    if (res.getBoolean("success")) {
                                        Utils.showSnack(view, "发送成功");
                                        if (finalComments != null) {
                                            activity.runOnUiThread(() -> {
                                                JSONObject comment = new JSONObject();
                                                comment.put("gcuId", userMine.gcuId);
                                                comment.put("sendNick", userMine.nick);
                                                comment.put("content", content);
                                                if (finalComments.size() < 3) {
                                                    View v = activity.getLayoutInflater().inflate(R.layout.comment_item, null);
                                                    ((TextView) v.findViewById(R.id.user_name)).setText(comment.getString("sendNick"));
                                                    ((TextView) v.findViewById(R.id.comment)).setText(comment.getString("content"));
                                                    firstComment.addView(v);
                                                } else {
                                                    view.findViewById(R.id.upIcon).setVisibility(View.VISIBLE);
                                                    View v = activity.getLayoutInflater().inflate(R.layout.comment_item, null);
                                                    ((TextView) v.findViewById(R.id.user_name)).setText(comment.getString("sendNick"));
                                                    ((TextView) v.findViewById(R.id.comment)).setText(comment.getString("content"));
                                                    hidnComment.addView(v);
                                                }
                                                finalComments.add(comment);
                                            });
                                        }

                                    } else {
                                        Utils.showSnack(view, res.getString("msg"));
                                    }
                                } else {
                                    Utils.showSnack(view, "网络异常");
                                }
                            }

                            @Override
                            public void onFailed(int code, Exception e) {
                                Utils.showSnack(view, e.getMessage());
                            }
                        });
                    } else {
                        Utils.showSnack(view, "未登录", "前往登录", view1 -> login.run());
                    }
                }
                return false;
            }
        });
        return view;
    }
}