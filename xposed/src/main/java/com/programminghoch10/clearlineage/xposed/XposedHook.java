package com.programminghoch10.clearlineage.xposed;

import android.annotation.SuppressLint;
import android.os.Build;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XCallback;

public class XposedHook implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {
    public static String MODULE_PATH = null;

    @SuppressLint("ObsoleteSdkInt")
    private static boolean isAndroidVersionSupported() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            XposedBridge.log("Android Version " + Build.VERSION.SDK_INT + " not supported!");
            return false;
        }
        return true;
    }

    private static void hook(HooksMap.HOOKTYPE hooktype, String packageName, XCallback.Param param) throws Exception {
        if (!isAndroidVersionSupported()) return;
        if (!HooksMap.supportedPackages.contains(packageName)) return;
        HooksMap.list.stream()
                .filter(hookEntry -> hookEntry.sdk == Build.VERSION.SDK_INT)
                .filter(hookEntry -> hookEntry.packageName.equals(packageName))
                .filter(hookEntry -> hookEntry.hooktype.equals(hooktype) || hookEntry.hooktype.equals(HooksMap.HOOKTYPE.BOTH))
                .forEach(hookEntry -> {
                    XposedBridge.log("Hooking " + hooktype + "/v" + hookEntry.sdk + "/" + packageName);
                    Class<?> hook = hookEntry.hookClass;
                    try {
                        Method hookMethod = hook.getMethod("hook", param.getClass());
                        hookMethod.invoke(hook.newInstance(), param);
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                });
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        hook(HooksMap.HOOKTYPE.CODE, lpparam.packageName, lpparam);
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Exception {
        hook(HooksMap.HOOKTYPE.RES, resparam.packageName, resparam);
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        if (!isAndroidVersionSupported()) return;
        if (!startupParam.startsSystemServer) return;
        MODULE_PATH = startupParam.modulePath;
    }
}
