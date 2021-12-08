package com.programminghoch10.clearlineage.xposed.HooksR;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookCode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
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

        Class<?> depthcontrollerclass = XposedHelpers.findClass("com.android.launcher3.statehandlers.DepthController", lpparam.classLoader);
        Class<?> statefulactivityclass = XposedHelpers.findClass("com.android.launcher3.statemanager.StatefulActivity", lpparam.classLoader);
        Class<?> launcherstateclass = XposedHelpers.findClass("com.android.launcher3.LauncherState", lpparam.classLoader);
        Object allappsstate = XposedHelpers.getStaticObjectField(launcherstateclass, "ALL_APPS");
        Method isinstatemethod = Arrays.stream(statefulactivityclass.getMethods()).filter(method -> method.getName().equals("isInState")).findAny().get();
        XposedBridge.hookMethod(isinstatemethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.args[0].equals(allappsstate)) return;
                boolean depthcontrollerfound = Arrays.stream(Thread.currentThread().getStackTrace())
                        .anyMatch(stackTraceElement -> stackTraceElement.getMethodName().equals("setDepth")
                                && stackTraceElement.getClassName().equals(depthcontrollerclass.getName()));
                if (depthcontrollerfound) {
                    param.setResult(false);
                }
            }
        });
    }
}
