package com.programminghoch10.clearlineage.xposed.Hooks.R;

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

public class SystemUIHook implements HookCode {
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
}
