package com.yfbx.robustdemo.patch;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchManipulate;
import com.meituan.robust.RobustApkHashUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:Edward
 * Date:2018/5/30
 * Description:
 */

public class PatchManipulateImp extends PatchManipulate {

    /***
     * 获取补丁列表
     */
    @Override
    protected List<Patch> fetchPatchList(Context context) {
        //将app自己的robustApkHash上报给服务端，服务端根据robustApkHash来区分每一次apk build来给app下发补丁
        String robustApkHash = RobustApkHashUtils.readRobustApkHash(context);
        Log.w("robust", "robustApkHash :" + robustApkHash);

        //联网获取补丁列表
        List<Patch> list = new ArrayList<>();

        Patch patch = new Patch();
        patch.setName("123");
        patch.setLocalPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "robust" + File.separator + "patch");
        //包名是和xml配置项patchPackname保持一致，而且类名必须是：PatchesInfoImpl
        patch.setPatchesInfoImplClassFullName("com.yfbx.robustdemo.PatchesInfoImpl");

        list.add(patch);
        return list;
    }

    /**
     * 验证补丁文件md5是否一致
     * 如果不存在，则动态下载
     */
    @Override

    protected boolean verifyPatch(Context context, Patch patch) {
        //do your verification, put the real patch to patch
        //放到app的私有目录
        patch.setTempPath(context.getCacheDir() + File.separator + "robust" + File.separator + "patch");
        //in the sample we just copy the file
        try {
            copy(patch.getLocalPath(), patch.getTempPath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("copy source patch to local patch error, no patch execute in path " + patch.getTempPath());
        }

        return true;
    }

    public void copy(String srcPath, String dstPath) throws IOException {
        File src = new File(srcPath);
        if (!src.exists()) {
            throw new RuntimeException("source patch does not exist ");
        }
        File dst = new File(dstPath);
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    /**
     * 确保补丁文件存在
     * 如果不存在，则动态下载
     */
    @Override
    protected boolean ensurePatchExist(Patch patch) {
        return true;
    }

}
