package com.programminghoch10.clearlineage.xposed.Hooks.S_V2;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookCode;
import com.programminghoch10.clearlineage.xposed.HooksMap;
import com.programminghoch10.clearlineage.xposed.Utils;

import java.lang.reflect.Method;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHook implements HookCode {
    
    private static final float QS_TILE_UNAVAILABLE_ALPHA = 0.5f;
    
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> scrimcontrollerclass = XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimController", lpparam.classLoader);
        Class<?> scrimviewclass = XposedHelpers.findClass("com.android.systemui.scrim.ScrimView", lpparam.classLoader);
        Method getScrimNameMethod = XposedHelpers.findMethodExact(scrimcontrollerclass, "getScrimName", scrimviewclass);
        XposedHelpers.findAndHookMethod(scrimcontrollerclass, "updateScrimColor", View.class, float.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String mState = XposedHelpers.getObjectField(param.thisObject, "mState").toString();
                if (mState.equals("KEYGUARD")) {
                    param.args[1] = 0.0f;
                    return;
                }
                if (mState.contains("BOUNCER")) {
                    param.args[1] = (float) param.args[1] * 0.5f;
                    return;
                }
                Context context = ((View) param.args[0]).getContext();
                boolean usesSplitShade = context.getResources().getBoolean(
                        context.getResources().getIdentifier("config_use_split_notification_shade", "bool", HooksMap.PACKAGE_SYSTEMUI)
                );
                switch (getScrimNameMethod.invoke(param.thisObject, param.args[0]).toString()) {
                    case "front_scrim":
                    case "behind_scrim":
                        param.args[1] = (float) param.args[1] * 0.5f;
                        break;
                    case "notifications_scrim":
                        param.args[1] = (float) param.args[1] * (usesSplitShade ? 0.0f : 0.5f);
                        break;
                }
            }
        });
        Class<?> scrimstateclass = XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimState", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(scrimstateclass, "updateScrimColor", scrimviewclass, float.class, int.class, XC_MethodReplacement.DO_NOTHING);
        
        Class<?> qstileviewimpl = XposedHelpers.findClass("com.android.systemui.qs.tileimpl.QSTileViewImpl", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(qstileviewimpl, Context.class, "com.android.systemui.plugins.qs.QSIconView", boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final String[] unavailableFields = new String[]{"colorLabelUnavailable", "colorSecondaryLabelUnavailable"};
                for (String field : unavailableFields) {
                    int prevcolor = XposedHelpers.getIntField(param.thisObject, field);
                    int newcolor = Utils.setAlpha(prevcolor, QS_TILE_UNAVAILABLE_ALPHA);
                    XposedHelpers.setIntField(param.thisObject, field, newcolor);
                }
                XposedHelpers.setIntField(param.thisObject, "colorUnavailable", Utils.setAlpha(Color.BLACK, QS_TILE_UNAVAILABLE_ALPHA * 0.5f));
                XposedHelpers.setIntField(param.thisObject, "colorInactive", Utils.setAlpha(Color.BLACK, 0x80));
            }
        });
        
        Class<?> numpadanimatorclass = XposedHelpers.findClass("com.android.keyguard.NumPadAnimator", lpparam.classLoader);
        XposedBridge.hookAllConstructors(numpadanimatorclass, XC_MethodReplacement.DO_NOTHING);
        Arrays.stream(numpadanimatorclass.getDeclaredMethods())
                .forEach(method -> XposedBridge.hookMethod(method, XC_MethodReplacement.DO_NOTHING));
        Class<?> numpadbuttonclass = XposedHelpers.findClass("com.android.keyguard.NumPadButton", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(numpadbuttonclass, "reloadColors", XC_MethodReplacement.DO_NOTHING);
        Class<?> numpadkeyclass = XposedHelpers.findClass("com.android.keyguard.NumPadKey", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(numpadkeyclass, "reloadColors", XC_MethodReplacement.DO_NOTHING);
        
        Class<?> animatableclockviewclass = XposedHelpers.findClass("com.android.keyguard.AnimatableClockView", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(animatableclockviewclass, "setColors", int.class, int.class, new XC_MethodHook() {
            @TargetApi(Build.VERSION_CODES.S)
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context context = ((View) param.thisObject).getContext();
                // dozing color
                //param.args[0] = context.getResources().getColor(android.R.color.system_accent3_200, context.getTheme());
                // clock accent color
                param.args[1] = context.getResources().getColor(android.R.color.system_accent1_200, context.getTheme());
            }
        });
        
        Class<?> globalactionsdialogliteclass = XposedHelpers.findClass("com.android.systemui.globalactions.GlobalActionsDialogLite$ActionsDialogLite", lpparam.classLoader);
        Method showRestartOptionsMethod = XposedHelpers.findMethodExactIfExists(globalactionsdialogliteclass, "showRestartOptionsMenu");
        if (showRestartOptionsMethod != null) {
            XposedBridge.hookMethod(showRestartOptionsMethod, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    View mContainer = (View) XposedHelpers.findField(globalactionsdialogliteclass, "mContainer").get(param.thisObject);
                    Dialog mRestartOptionsDialog = (Dialog) XposedHelpers.findField(globalactionsdialogliteclass, "mRestartOptionsDialog").get(param.thisObject);
                    mContainer.setVisibility(View.INVISIBLE);
                    mRestartOptionsDialog.setOnDismissListener(dialog -> mContainer.setVisibility(View.VISIBLE));
                }
            });
        }
    }
}
