package com.yfbx.robustdemo.test;

import android.os.Environment;
import android.text.TextUtils;

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

public class PatchUtil {

    private static final String PATCH_DIR = App.getInstance().getCacheDir().getAbsolutePath();
    private static final String PATCH_PATH = PATCH_DIR + File.separator + "robust" + File.separator + "patch";

    /**
     * 检查补丁版本
     */
    public static void checkPatch() {
        boolean hasNewPatch = true;
        // TODO: 2018/5/31 联网检查补丁版本

        //有新版本则下载
        if (hasNewPatch) {
            downloadPatch();
        } else {
            //没有新版本则应用已存在的补丁
            try {
                applyPatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载补丁到私有目录
     */
    private static void downloadPatch() {

        String srcPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "robust" + File.separator + "patch";
        String dstPath = App.getInstance().getCacheDir() + File.separator + "robust" + File.separator + "patch";

        File src = new File(srcPath);
        if (!src.exists()) {
            throw new RuntimeException("source patch does not exist ");
        }
        File dst = new File(dstPath);
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }

        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //下载完成后应用补丁
        try {
            applyPatch();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Patch信息
     */
    private static Patch getPatch() {
        Patch patch = new Patch();
        patch.setName("123");
        patch.setLocalPath(PATCH_PATH);
        patch.setTempPath(PATCH_PATH);
        patch.setPatchesInfoImplClassFullName("com.yfbx.robustdemo.PatchesInfoImpl");
        return patch;
    }

    /**
     * 应用Patch
     */
    private static boolean applyPatch() throws Exception {
        Patch patch = getPatch();
        DexClassLoader classLoader = new DexClassLoader(PATCH_PATH, PATCH_DIR, null, PatchThread.class.getClassLoader());

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

}
