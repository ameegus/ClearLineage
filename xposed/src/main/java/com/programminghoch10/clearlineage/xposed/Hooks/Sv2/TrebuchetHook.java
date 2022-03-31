package com.programminghoch10.clearlineage.xposed.Hooks.Sv2;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.programminghoch10.clearlineage.xposed.HookCode;
import com.programminghoch10.clearlineage.xposed.Utils;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TrebuchetHook implements HookCode {
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> scrimviewclass = XposedHelpers.findClass("com.android.launcher3.views.ScrimView", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(scrimviewclass, "setBackgroundColor", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int prevcolor = (int) param.args[0];
                // if the color is an transparent white or black, it probably comes from our overlays, so we don't change it
                if (prevcolor == Color.TRANSPARENT
                        || Utils.setAlpha(prevcolor, 0xFF) == Color.WHITE
                        || Utils.setAlpha(prevcolor, 0xFF) == Color.BLACK)
                    return;
                int prevalpha = Color.alpha(prevcolor);
                // could use provided color, but that is an ugly grey and transparent black or white looks better
                Field mContext = XposedHelpers.findField(scrimviewclass, "mContext");
                Context context = (Context) mContext.get(param.thisObject);
                int adaptivecolor = Utils.isNight(context) ? Color.BLACK : Color.WHITE;
                int newalpha = (int) (prevalpha * 0.5f);
                param.args[0] = Utils.setAlpha(adaptivecolor, newalpha);
            }
        });
        Class<?> allappscontainerview = XposedHelpers.findClass("com.android.launcher3.allapps.AllAppsContainerView", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(allappscontainerview, Context.class, AttributeSet.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Field mNavBarScrimPaintField = XposedHelpers.findField(allappscontainerview, "mNavBarScrimPaint");
                Paint p = new Paint();
                p.setColor(Color.TRANSPARENT);
                mNavBarScrimPaintField.set(param.thisObject, p);
            }
        });
        XposedHelpers.findAndHookMethod(allappscontainerview, "updateHeaderScroll", int.class, XC_MethodReplacement.DO_NOTHING);
        
        Class<?> depthcontrollerclass = XposedHelpers.findClass("com.android.launcher3.statehandlers.DepthController", lpparam.classLoader);
        final boolean[] systemAppLaunch = {false};
        XposedHelpers.findAndHookMethod(depthcontrollerclass, "setIsInLaunchTransition", boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                systemAppLaunch[0] = (boolean) param.args[0];
                return null;
            }
        });
        Class<?> launcherstateclass = XposedHelpers.findClass("com.android.launcher3.LauncherState", lpparam.classLoader);
        Object overviewstate = XposedHelpers.getStaticObjectField(launcherstateclass, "OVERVIEW");
        Object normalstate = XposedHelpers.getStaticObjectField(launcherstateclass, "NORMAL");
        Class<?> statemanagerclass = XposedHelpers.findClass("com.android.launcher3.statemanager.StateManager", lpparam.classLoader);
        Field mLauncherField = XposedHelpers.findField(depthcontrollerclass, "mLauncher");
        XposedHelpers.findAndHookMethod(depthcontrollerclass, "dispatchTransactionSurface", float.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Object mLauncher = mLauncherField.get(param.thisObject);
                Object stateManager = XposedHelpers.findMethodExact(mLauncherField.getType(), "getStateManager").invoke(mLauncher);
                Object state = XposedHelpers.findMethodExact(statemanagerclass, "getState").invoke(stateManager);
                Object stablestate = XposedHelpers.findMethodExact(statemanagerclass, "getCurrentStableState").invoke(stateManager);
                boolean overviewstateinvolved = state.equals(overviewstate) || stablestate.equals(overviewstate);
                if (overviewstateinvolved || systemAppLaunch[0])
                    XposedHelpers.setBooleanField(param.thisObject, "mBlurDisabledForAppLaunch", true);
                if (!overviewstateinvolved && stablestate.equals(normalstate) && !systemAppLaunch[0])
                    XposedHelpers.setBooleanField(param.thisObject, "mBlurDisabledForAppLaunch", false);
            }
        });
        
        final float TASKBAR_TARGET_ALPHA = 0.5f;
        Class<?> taskbardraglayerclass = XposedHelpers.findClass("com.android.launcher3.taskbar.TaskbarDragLayer", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(taskbardraglayerclass, "setTaskbarBackgroundAlpha", float.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = (float) param.args[0] * TASKBAR_TARGET_ALPHA;
            }
        });
    }
}
