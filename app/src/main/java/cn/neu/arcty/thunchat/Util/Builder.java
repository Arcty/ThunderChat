package cn.neu.arcty.thunchat.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by arcty on 16-12-9.
 * 显示Toast的工具类
 */

public class Builder {
    public static void BuildToast(String word, Context context) {
        Toast.makeText(context, word, Toast.LENGTH_SHORT).show();
    }
}
