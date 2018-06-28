package com.yfbx.robustdemo;

import android.widget.Toast;

import com.meituan.robust.patch.annotaion.Modify;

/**
 * Author:Edward
 * Date:2018/5/31
 * Description:
 */
@Modify
public class Model {

    public static String getResult(int type) {

        Toast.makeText(App.getInstance(), "" + type, Toast.LENGTH_SHORT).show();

        switch (type) {
            case 1:
                return "THE NUMBER IS" + type;
            case 2:
                return "THE NUMBER IS" + type;
            case 3:
                return "THE NUMBER IS" + type;
            case 4:
                return "THE NUMBER IS" + type;
            case 5:
                return "THE NUMBER IS" + type;
        }
        return "EMPTY";
    }
}
