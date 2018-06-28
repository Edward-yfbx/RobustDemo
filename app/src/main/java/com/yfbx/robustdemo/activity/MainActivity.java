package com.yfbx.robustdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meituan.robust.PatchExecutor;
import com.meituan.robust.RobustApkHashUtils;
import com.meituan.robust.patch.annotaion.Modify;
import com.yfbx.robustdemo.Model;
import com.yfbx.robustdemo.R;
import com.yfbx.robustdemo.test.PatchThread;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView infoTxt;
    private EditText typeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoTxt = findViewById(R.id.info_txt);
        typeTxt = findViewById(R.id.type);
        findViewById(R.id.patch_btn).setOnClickListener(this);
        findViewById(R.id.test).setOnClickListener(this);

    }

    private int getType() {
        return TextUtils.isEmpty(typeTxt.getText()) ? 0 : Integer.valueOf(typeTxt.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test:
                showResult();
                break;
            case R.id.patch_btn:
                checkPatch();
//                new PatchExecutor(this, new PatchManipulateImp(), new RobustCallBackSample()).start();
                break;
        }
    }

    private void showResult() {
        infoTxt.setText(Model.getResult(getType()));
    }

    @Modify
    private void checkPatch() {
        String appHash = RobustApkHashUtils.readRobustApkHash(this);
        Log.i("App Hash值", appHash);
        PatchThread.applyPatch();
    }
}
