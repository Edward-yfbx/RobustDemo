package com.yfbx.robustdemo.test;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.meituan.robust.ChangeQuickRedirect;
import com.meituan.robust.Patch;
import com.meituan.robust.PatchedClassInfo;
import com.meituan.robust.PatchesInfo;
import com.yfbx.robustdemo.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Author:Edward
 * Date:2018/5/31
 * Description:
 */

public class PatchThread extends Thread {

    private static final String TAG = "PATCH";

    public static void applyPatch() {
        new PatchThread().start();
    }

    public PatchThread() {
    }

    @Override
    public void run() {
        try {
            Patch patch = getPatch();
            patch.setTempPath(App.getInstance().getCacheDir() + File.separator + "robust" + File.separator + "patch");
            copy(patch.getLocalPath(), patch.getTempPath());
            boolean isSuccess = patch(patch);
            Log.i(TAG, "补丁结果:" + isSuccess);
        } catch (Throwable t) {
            t.printStackTrace();
            Log.i(TAG, "补丁出错");
        }
    }

    /**
     * Patch信息
     */
    private Patch getPatch() {
        Patch patch = new Patch();
        patch.setName("123");
        patch.setLocalPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "robust" + File.separator + "patch");
        patch.setPatchesInfoImplClassFullName("com.yfbx.robustdemo.PatchesInfoImpl");
        return patch;
    }

    /**
     * 应用Patch
     */
    private boolean patch(Patch patch) throws Exception {
        DexClassLoader classLoader = new DexClassLoader(patch.getTempPath(), App.getInstance().getCacheDir().getAbsolutePath(), null, PatchThread.class.getClassLoader());

        Class patchsInfoClass = classLoader.loadClass(patch.getPatchesInfoImplClassFullName());
        PatchesInfo patchesInfo = (PatchesInfo) patchsInfoClass.newInstance();
        if (patchesInfo == null) {
            return false;
        }

        //classes need to patch
        List<PatchedClassInfo> patchedClasses = patchesInfo.getPatchedClassesInfo();
        if (null == patchedClasses || patchedClasses.isEmpty()) {
            return false;
        }

        Class patchClass;
        Class oldClass;

        for (PatchedClassInfo patchedClassInfo : patchedClasses) {
            String patchedClassName = patchedClassInfo.patchedClassName;
            String patchClassName = patchedClassInfo.patchClassName;
            if (TextUtils.isEmpty(patchedClassName) || TextUtils.isEmpty(patchClassName)) {
                continue;
            }

            oldClass = classLoader.loadClass(patchedClassName.trim());
            Field[] fields = oldClass.getDeclaredFields();
            Field changeQuickRedirectField = null;
            for (Field field : fields) {
                if (TextUtils.equals(field.getType().getCanonicalName(), ChangeQuickRedirect.class.getCanonicalName()) && TextUtils.equals(field.getDeclaringClass().getCanonicalName(), oldClass.getCanonicalName())) {
                    changeQuickRedirectField = field;
                    break;
                }
            }
            if (changeQuickRedirectField == null) {
                continue;
            }
            patchClass = classLoader.loadClass(patchClassName);
            Object patchObject = patchClass.newInstance();
            changeQuickRedirectField.setAccessible(true);
            changeQuickRedirectField.set(null, patchObject);
        }
        return true;
    }

    /**
     * 将Patch文件复制到私有目录
     */
    private void copy(String srcPath, String dstPath) throws IOException {
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
}
