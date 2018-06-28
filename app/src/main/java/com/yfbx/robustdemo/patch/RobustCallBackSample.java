package com.yfbx.robustdemo.patch;

import android.util.Log;
import android.widget.Toast;

import com.meituan.robust.Patch;
import com.meituan.robust.RobustCallBack;
import com.yfbx.robustdemo.App;

import java.util.List;

/**
 * Author:Edward
 * Date:2018/5/30
 * Description:
 */

public class RobustCallBackSample implements RobustCallBack {

    private static final String TAG = "RobustCallBack";

    @Override
    public void onPatchListFetched(boolean result, boolean isNet, List<Patch> patches) {
        Log.d(TAG, "onPatchListFetched result: " + result);
        Log.d(TAG, "onPatchListFetched isNet: " + isNet);
        for (Patch patch : patches) {
            Log.d(TAG, "onPatchListFetched patch: " + patch.getName());
        }
    }

    @Override
    public void onPatchFetched(boolean result, boolean isNet, Patch patch) {
        Log.d(TAG, "onPatchFetched result: " + result);
        Log.d(TAG, "onPatchFetched isNet: " + isNet);
        Log.d(TAG, "onPatchFetched patch: " + patch.getName());
    }

    @Override
    public void onPatchApplied(boolean result, Patch patch) {
        Log.d(TAG, "onPatchApplied result: " + result);
        Log.d(TAG, "onPatchApplied patch: " + patch.getName());
        Toast.makeText(App.getInstance(), "补丁应用成功", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void logNotify(String log, String where) {
        Log.d(TAG, "logNotify log: " + log);
        Log.d(TAG, "logNotify where: " + where);
    }

    @Override
    public void exceptionNotify(Throwable throwable, String where) {
        Log.e(TAG, "exceptionNotify where: " + where, throwable);
    }
}
