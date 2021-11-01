package com.programminghoch10.clearlineage.xposed;

import android.annotation.SuppressLint;
import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageHook implements IXposedHookLoadPackage {
	@SuppressLint("ObsoleteSdkInt")
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
			XposedBridge.log("Android Version " + Build.VERSION.SDK_INT + " not supported!");
			return;
		}
		if (HooksMap.map.containsKey(Build.VERSION.SDK_INT, lpparam.packageName)) {
			XposedBridge.log("Hooking v" + Build.VERSION.SDK_INT + "/" + lpparam.packageName);
			HooksMap.map.get(Build.VERSION.SDK_INT, lpparam.packageName).getMethod("handleLoadPackage", lpparam.getClass()).invoke(null, lpparam);
		} else {
			// suppress warning if package has hooks for other android versions
			if (HooksMap.supportedPackages.contains(lpparam.packageName)) return;
			XposedBridge.log("Can't find hook v" + Build.VERSION.SDK_INT + "/" + lpparam.packageName);
		}
	}
}
