package com.programminghoch10.clearlineage.xposed.HooksR;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;

import com.programminghoch10.clearlineage.xposed.HookCode;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Launcher3Hook implements HookCode {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> scrimviewclass = XposedHelpers.findClass("com.android.launcher3.views.ScrimView", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(scrimviewclass, Context.class, AttributeSet.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Field mEndScrim = XposedHelpers.findField(scrimviewclass, "mEndScrim");
                //int prevcolor = mEndScrim.getInt(param.thisObject);
				/*int prevcolor = ((Context)param.args[0]).getTheme().obtainStyledAttributes(new int[] {
						android.R.attr.colorBackground
				}).getColor(0, 0);*/
                boolean night = (((Context) param.args[0]).getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
                int prevcolor = night ? Color.BLACK : Color.WHITE;
                int newcolor = (prevcolor & 0x00FFFFFF) | (0xa0 << 24);
                mEndScrim.setInt(param.thisObject, newcolor);
            }
        });
    }
}
