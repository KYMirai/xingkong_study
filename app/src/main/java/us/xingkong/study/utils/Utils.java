package us.xingkong.study.utils;

import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class Utils {
    /**
     * 屏幕下方显示一条可拖动的悬浮消息
     *
     * @param view 随便一个可用的view即可
     * @param text 显示的消息
     */
    public static void showSnack(View view, String text) {
        Snackbar.make(view, text, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    /**
     * 数组 定位
     *
     * @param arr   数组
     * @param value 值
     * @return value所在位置
     */
    public static int getIndex(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return i;
            }
        }
        return -1;//如果未找到返回-1
    }
}
