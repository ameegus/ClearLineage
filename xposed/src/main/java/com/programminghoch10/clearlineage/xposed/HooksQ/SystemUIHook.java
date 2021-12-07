package com.programminghoch10.clearlineage.xposed.HooksQ;

import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookCode;
import com.programminghoch10.clearlineage.xposed.HookRes;
import com.programminghoch10.clearlineage.xposed.HooksMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHook implements HookCode, HookRes {
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        final Class<?> scrimdrawableclass = XposedHelpers.findClass("com.android.internal.colorextraction.drawable.ScrimDrawable", lpparam.classLoader);
        XC_MethodHook alphahook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    int startvalue = (int) param.args[0];
                    float startvaluef = startvalue;
                    float interpolated = startvaluef / 256 * 200;
                    int endvalue = (int) interpolated;
                    param.args[0] = endvalue;
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
            }
        };
        final XC_MethodHook.Unhook[] unhookhandle = new XC_MethodHook.Unhook[1];

        Class<?> scrimcontrollerclass = XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimController", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(scrimcontrollerclass, "updateScrimColor", View.class, float.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!XposedHelpers.findField(scrimcontrollerclass, "mScrimBehind").get(param.thisObject).equals(param.args[0]))
                    return;
                param.args[1] = (float) param.args[1] * 0.5f;
                param.args[2] = Color.BLACK;
            }
        });
    }

    @Override
    public void hook(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Exception {
        resparam.res.hookLayout(HooksMap.PACKAGE_SYSTEMUI, "layout", "qs_panel", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                View quick_settings_status_bar_background = liparam.view.findViewById(
                        liparam.res.getIdentifier("quick_settings_status_bar_background", "id", HooksMap.PACKAGE_SYSTEMUI)
                );
                quick_settings_status_bar_background.setBackgroundColor(0x80000000);
            }
        });
    }
}
