package com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookCode;
import com.programminghoch10.clearlineage.xposed.Utils;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TrebuchetHook implements HookCode {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> shelfscrimviewclass = XposedHelpers.findClass("com.android.quickstep.views.ShelfScrimView", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(shelfscrimviewclass, Context.class, AttributeSet.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Field mEndAlpha = XposedHelpers.findField(shelfscrimviewclass, "mEndAlpha");
                Field mEndScrim = XposedHelpers.findField(shelfscrimviewclass, "mEndScrim");
                boolean night = Utils.isNight((Context) param.args[0]);
                mEndScrim.setInt(param.thisObject, night ? Color.BLACK : Color.WHITE);
                mEndAlpha.setInt(param.thisObject, (int) (255 * (night ? 0.5f : 0.7f)));
            }
        });
        
        XposedHelpers.findAndHookMethod("com.android.launcher3.uioverrides.states.OverviewState", lpparam.classLoader,
                "getOverviewScrimAlpha", "com.android.launcher3.Launcher", XC_MethodReplacement.returnConstant(0.5f));
        Class<?> scrimclass = XposedHelpers.findClass("com.android.launcher3.graphics.Scrim", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(scrimclass, "onExtractedColorsChanged",
                "com.android.launcher3.uioverrides.WallpaperColorInfo", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Field mScrimColor = XposedHelpers.findField(scrimclass, "mScrimColor");
                        Context context = ((View) XposedHelpers.getObjectField(param.thisObject, "mRoot")).getContext();
                        mScrimColor.setInt(param.thisObject, Utils.isNight(context) ? Color.BLACK : Color.GRAY);
                        return null;
                    }
                });
        
        Class<?> scrimviewclass = XposedHelpers.findClass("com.android.launcher3.views.ScrimView", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(scrimviewclass, "onExtractedColorsChanged",
                "com.android.launcher3.uioverrides.WallpaperColorInfo", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Field mEndFlatColorAlpha = XposedHelpers.findField(scrimviewclass, "mEndFlatColorAlpha");
                        Field mEndScrim = XposedHelpers.findField(scrimviewclass, "mEndScrim");
                        Context context = ((View) param.thisObject).getContext();
                        boolean night = Utils.isNight(context);
                        mEndScrim.setInt(param.thisObject, night ? Color.BLACK : Color.WHITE);
                        mEndFlatColorAlpha.setInt(param.thisObject, (int) (255 * (night ? 0.5f : 0.7f)));
                    }
                });
    }
}
