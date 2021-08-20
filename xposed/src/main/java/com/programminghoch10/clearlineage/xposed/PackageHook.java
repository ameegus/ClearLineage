package com.programminghoch10.clearlineage.xposed;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageHook implements IXposedHookLoadPackage {
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
			XposedBridge.log("Android Version not supported!");
			return;
		}
		switch (lpparam.packageName) {
			case "com.android.systemui":
				SystemUIHook.handleLoadPackage(lpparam);
				break;
			case "com.android.launcher3":
				Launcher3Hook.handleLoadPackage(lpparam);
				break;
			default:
				return;
		}
	}
}
