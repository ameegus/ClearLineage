package com.programminghoch10.clearlineage.xposed.HooksR;

import android.graphics.Color;
import android.os.Build;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHook {
	public static void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		
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
		
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
			final Class<?> gradientcolorsclass = XposedHelpers.findClass("com.android.internal.colorextraction.ColorExtractor.GradientColors", lpparam.classLoader);
			final Class<?> actionsdialogclass = XposedHelpers.findClass("com.android.systemui.globalactions.GlobalActionsDialog$ActionsDialog", lpparam.classLoader);
			XposedHelpers.findAndHookMethod(actionsdialogclass, "updateColors", gradientcolorsclass, boolean.class, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					
					Field backgrounddrawablefield = XposedHelpers.findField(actionsdialogclass, "mBackgroundDrawable");
					Object thisobj = param.thisObject;
					Object mbackgrounddrawbale = backgrounddrawablefield.get(thisobj);
					Object backgrounddrawable = scrimdrawableclass.cast(mbackgrounddrawbale);
					Method setcolormethod = XposedHelpers.findMethodExact(scrimdrawableclass, "setColor", int.class, boolean.class);
					boolean animate = (boolean) param.args[1];
					setcolormethod.invoke(backgrounddrawable, Color.BLACK, animate);
					
					unhookhandle[0] = XposedHelpers.findAndHookMethod(scrimdrawableclass, "setAlpha", int.class, alphahook);
					
				}
			});
			XposedHelpers.findAndHookMethod(actionsdialogclass, "completeDismiss", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					unhookhandle[0].unhook();
				}
			});
		}
		
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
}
