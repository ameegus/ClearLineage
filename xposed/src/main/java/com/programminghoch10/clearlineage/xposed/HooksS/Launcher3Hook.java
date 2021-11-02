package com.programminghoch10.clearlineage.xposed.HooksS;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Launcher3Hook {
	public static void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		Class<?> scrimviewclass = XposedHelpers.findClass("com.android.launcher3.views.ScrimView", lpparam.classLoader);
		XposedHelpers.findAndHookMethod(scrimviewclass, "setBackgroundColor", int.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				int prevcolor = (int) param.args[0];
				if (prevcolor == Color.TRANSPARENT) return;
				int prevalpha = Color.alpha(prevcolor);
				// could use provided color, but that is an ugly grey and transparent black or white looks better
				Field mContext = XposedHelpers.findField(scrimviewclass, "mContext");
				Context context = (Context) mContext.get(param.thisObject);
				boolean night = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
				int adaptivecolor = night ? Color.BLACK : Color.WHITE;
				int newalpha = (int) (prevalpha * 0.5f);
				int newcolor = (adaptivecolor & 0x00FFFFFF) | (newalpha << 24);
				param.args[0] = newcolor;
			}
		});
		Class<?> allappscontainerview = XposedHelpers.findClass("com.android.launcher3.allapps.AllAppsContainerView", lpparam.classLoader);
		XposedHelpers.findAndHookMethod(allappscontainerview, "updateHeaderScroll", int.class, XC_MethodReplacement.DO_NOTHING);
	}
}
