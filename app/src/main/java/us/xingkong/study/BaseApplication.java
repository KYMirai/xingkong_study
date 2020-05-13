package us.xingkong.study;

import android.app.Application;

import us.xingkong.study.utils.Utils;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
    }
}
