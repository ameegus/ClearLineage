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
        if (HooksMap.map.containsKey(hooktype, Build.VERSION.SDK_INT, packageName)) {
            XposedBridge.log("Hooking " + hooktype + "/v" + Build.VERSION.SDK_INT + "/" + packageName);
            Class<?> hook = HooksMap.map.get(hooktype, Build.VERSION.SDK_INT, packageName);
            Method hookMethod = hook.getMethod("hook", param.getClass());
            hookMethod.invoke(hook.newInstance(), param);
        } else {
            XposedBridge.log("Can't find hook " + hooktype + "/v" + Build.VERSION.SDK_INT + "/" + packageName);
        }
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
        XposedBridge.log("Zygote System Init");
    }
}
