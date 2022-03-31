package com.programminghoch10.clearlineage.xposed.Hooks;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.programminghoch10.clearlineage.xposed.HookCode;
import com.programminghoch10.clearlineage.xposed.HookRes;
import com.programminghoch10.clearlineage.xposed.HooksMap;
import com.programminghoch10.clearlineage.xposed.Utils;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class UpdaterHook implements HookCode, HookRes {
    private static final int blurRadius = 20; //in dp
    
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> updatesactivityclass = XposedHelpers.findClass("org.lineageos.updater.UpdatesActivity", lpparam.classLoader);
        Class<?> collapsingtoolbarlayoutclass = XposedHelpers.findClass("com.google.android.material.appbar.CollapsingToolbarLayout", lpparam.classLoader);
        Method setContentScrimMethod = XposedHelpers.findMethodExact(collapsingtoolbarlayoutclass, "setContentScrim", Drawable.class);
        Method setStatusBarScrimMethod = XposedHelpers.findMethodExact(collapsingtoolbarlayoutclass, "setStatusBarScrim", Drawable.class);
        XposedHelpers.findAndHookMethod(updatesactivityclass, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                //activity.setTheme(android.R.style.Theme_DeviceDefault_Settings);
                Window window = activity.getWindow();
                int backgroundColorDayNight = Utils.isNight(activity) ? Utils.setAlpha(Color.BLACK, 0.5f) : Utils.setAlpha(Color.WHITE, 0.7f);
                window.setBackgroundDrawable(new ColorDrawable(backgroundColorDayNight));
                int flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                        | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
                window.addFlags(flags);
                float density = activity.getResources().getDisplayMetrics().density;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    window.getAttributes().setBlurBehindRadius((int) (blurRadius * density));
                View toolbar = activity.findViewById(
                        activity.getResources().getIdentifier("collapsing_toolbar", "id", HooksMap.PACKAGE_UPDATER)
                );
                ColorDrawable colorDrawable = new ColorDrawable(backgroundColorDayNight);
                setContentScrimMethod.invoke(toolbar, colorDrawable);
                setStatusBarScrimMethod.invoke(toolbar, new ColorDrawable(Color.TRANSPARENT));
            }
        });
    }
    
    @Override
    public void hook(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Exception {
        resparam.res.hookLayout(HooksMap.PACKAGE_UPDATER, "layout", "activity_updates", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                View view = liparam.view.findViewById(
                        liparam.res.getIdentifier("app_bar", "id", HooksMap.PACKAGE_UPDATER)
                );
                view.setBackground(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        resparam.res.hookLayout(HooksMap.PACKAGE_UPDATER, "layout", "update_item_view", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                liparam.view.setBackground(new ColorDrawable(Color.TRANSPARENT));
            }
        });
    }
}
