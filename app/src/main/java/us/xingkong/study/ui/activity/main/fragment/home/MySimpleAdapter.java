package us.xingkong.study.ui.activity.main.fragment.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import us.xingkong.study.R;
import us.xingkong.study.utils.HiddenAnimUtils;

public class MySimpleAdapter extends SimpleAdapter {
    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.findViewById(R.id.upIcon).setOnClickListener(view1 -> HiddenAnimUtils.newInstance(view.findViewById(R.id.hideView), (TextView) view1).toggle());
        return view;
    }
}