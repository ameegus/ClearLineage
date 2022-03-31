package com.programminghoch10.clearlineage.xposed.Hooks.R;

import com.programminghoch10.clearlineage.xposed.HookCode;
import com.programminghoch10.clearlineage.xposed.Utils;

import java.lang.reflect.Method;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TrebuchetHook implements HookCode {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> depthcontrollerclass = XposedHelpers.findClass("com.android.launcher3.statehandlers.DepthController", lpparam.classLoader);
        Method setDepthMethod = XposedHelpers.findMethodExact(depthcontrollerclass, "setDepth", float.class);
        Class<?> statefulactivityclass = XposedHelpers.findClass("com.android.launcher3.statemanager.StatefulActivity", lpparam.classLoader);
        Class<?> launcherstateclass = XposedHelpers.findClass("com.android.launcher3.LauncherState", lpparam.classLoader);
        Object allappsstate = XposedHelpers.getStaticObjectField(launcherstateclass, "ALL_APPS");
        Method isinstatemethod = Arrays.stream(statefulactivityclass.getMethods()).filter(method -> method.getName().equals("isInState")).findAny().get();
        XposedBridge.hookMethod(isinstatemethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.args[0].equals(allappsstate)) return;
                if (Utils.isCurrentThreadCalledFromMethod(setDepthMethod)) {
                    param.setResult(false);
                }
            }
        });
    }
}
