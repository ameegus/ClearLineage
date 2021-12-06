package com.programminghoch10.clearlineage.xposed.HooksR;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookCode;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Launcher3Hook implements HookCode {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> shelfscrimviewclass = XposedHelpers.findClass("com.android.quickstep.views.ShelfScrimView", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(shelfscrimviewclass, Context.class, AttributeSet.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Field mEndAlpha = XposedHelpers.findField(shelfscrimviewclass, "mEndAlpha");
                Field mEndScrim = XposedHelpers.findField(shelfscrimviewclass, "mEndScrim");
                boolean night = (((Context) param.args[0]).getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
                mEndScrim.setInt(param.thisObject, night ? Color.BLACK : Color.WHITE);
                mEndAlpha.setInt(param.thisObject, (int) (255 * (night ? 0.5f : 0.7f)));
            }
        });

        XposedHelpers.findAndHookMethod("com.android.launcher3.uioverrides.states.OverviewState", lpparam.classLoader,
                "getOverviewScrimAlpha", "com.android.launcher3.Launcher", XC_MethodReplacement.returnConstant(0.1f));

        Class<?> scrimviewclass = XposedHelpers.findClass("com.android.launcher3.views.ScrimView", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(scrimviewclass, "onExtractedColorsChanged",
                "com.android.launcher3.uioverrides.WallpaperColorInfo", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Field mEndFlatColorAlpha = XposedHelpers.findField(scrimviewclass, "mEndFlatColorAlpha");
                        Field mEndScrim = XposedHelpers.findField(scrimviewclass, "mEndScrim");
                        Context context = ((View) param.thisObject).getContext();
                        boolean night = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
                        mEndScrim.setInt(param.thisObject, night ? Color.BLACK : Color.WHITE);
                        mEndFlatColorAlpha.setInt(param.thisObject, (int) (255 * (night ? 0.5f : 0.7f)));
                    }
                });
    }
}
