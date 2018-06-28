package com.yfbx.robustdemo;

import android.app.Application;

/**
 * Author:Edward
 * Date:2018/5/31
 * Description:
 */

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }


    public static App getInstance() {
        return app;
    }

}
