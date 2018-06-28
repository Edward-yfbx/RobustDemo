package com.yfbx.robustdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.meituan.robust.patch.annotaion.Modify;
import com.yfbx.robustdemo.R;

import java.lang.ref.WeakReference;

/**
 * Author:Edward
 * Date:2018/5/31
 * Description:
 */

public class RobustActivity extends Activity {

    private TextView infoTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoTxt = findViewById(R.id.info_txt);
        new MyHandler(this).sendEmptyMessage(0);
    }

    @Modify
    public void setInfo() {
        infoTxt.setText("补丁应用成功");
    }

    static class MyHandler extends Handler {

        private WeakReference<RobustActivity> refs;

        MyHandler(RobustActivity activity) {
            refs = new WeakReference<RobustActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            refs.get().setInfo();
            sendEmptyMessageDelayed(0, 3000);
        }
    }

}
