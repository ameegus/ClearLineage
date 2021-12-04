package com.programminghoch10.clearlineage.xposed.HooksS;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookCode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHook implements HookCode {

    private static final float QS_TILE_UNAVAILABLE_ALPHA = 0.5f;

    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> scrimcontrollerclass = XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimController", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(scrimcontrollerclass, "updateScrimColor", View.class, float.class, int.class, new XC_MethodHook() {
            @TargetApi(Build.VERSION_CODES.S)
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                class ScrimField {
                    final Object object;
                    final float targetAlpha;

                    ScrimField(Object object, float targetAlpha) {
                        this.object = object;
                        this.targetAlpha = targetAlpha;
                    }
                }
                List<ScrimField> scrims = List.of(
                        new ScrimField(XposedHelpers.findField(scrimcontrollerclass, "mScrimBehind").get(param.thisObject), 0.5f),
                        new ScrimField(XposedHelpers.findField(scrimcontrollerclass, "mNotificationsScrim").get(param.thisObject), 0.5f)
                );
                Optional<ScrimField> fieldOp = scrims.stream().filter(item -> item.object.equals(param.args[0])).findAny();
                if (!fieldOp.isPresent())
                    return;
                ScrimField field = fieldOp.get();
                param.args[1] = (float) param.args[1] * field.targetAlpha;
                //param.args[2] = Color.BLACK;
            }
        });

        Class<?> qstileviewimpl = XposedHelpers.findClass("com.android.systemui.qs.tileimpl.QSTileViewImpl", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(qstileviewimpl, Context.class, "com.android.systemui.plugins.qs.QSIconView", boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final String[] unavailableFields = new String[]{"colorLabelUnavailable", "colorSecondaryLabelUnavailable"};
                for (String field : unavailableFields) {
                    int prevcolor = XposedHelpers.getIntField(param.thisObject, field);
                    int newcolor = prevcolor & 0xFFFFFF | ((int) (QS_TILE_UNAVAILABLE_ALPHA * 0xFF) << 4 * 6);
                    XposedHelpers.setIntField(param.thisObject, field, newcolor);
                }
                XposedHelpers.setIntField(param.thisObject, "colorUnavailable", (int) (QS_TILE_UNAVAILABLE_ALPHA * 0.5f * 0xFF) << 4 * 6);
                XposedHelpers.setIntField(param.thisObject, "colorInactive", 0x80000000);
            }
        });

        Class<?> numpadanimatorclass = XposedHelpers.findClass("com.android.keyguard.NumPadAnimator", lpparam.classLoader);
        XposedBridge.hookAllConstructors(numpadanimatorclass, XC_MethodReplacement.DO_NOTHING);
        Arrays.stream(numpadanimatorclass.getDeclaredMethods())
                .forEach(method -> XposedBridge.hookMethod(method, XC_MethodReplacement.DO_NOTHING));
        Class<?> numpadbuttonclass = XposedHelpers.findClass("com.android.keyguard.NumPadButton", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(numpadbuttonclass, "reloadColors", XC_MethodReplacement.DO_NOTHING);
        Class<?> numpadkeyclass = XposedHelpers.findClass("com.android.keyboard.NumPadKey", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(numpadkeyclass, "reloadColors", XC_MethodReplacement.DO_NOTHING);
    }
}
