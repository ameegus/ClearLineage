package com.programminghoch10.clearlineage.xposed;

import android.content.Context;
import android.content.res.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Utils {
    public static boolean isCurrentThreadCalledFromMethod(Method method) {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(stackTraceElement ->
                        stackTraceElement.getMethodName().equals(method.getName())
                                && stackTraceElement.getClassName().equals(method.getDeclaringClass().getName())
                );
    }
    
    public static int setAlpha(int color, int alpha) {
        return color & ~(0xff << 24) | (alpha << 24);
    }
    
    public static int setAlpha(int color, float alpha) {
        return setAlpha(color, (int) (alpha * 0xff));
    }
    
    public static boolean isNight(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
