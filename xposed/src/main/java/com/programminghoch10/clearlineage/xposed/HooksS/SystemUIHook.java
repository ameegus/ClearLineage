package com.programminghoch10.clearlineage.xposed.HooksS;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHook {
    public static void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class<?> scrimcontrollerclass = XposedHelpers.findClass("com.android.systemui.statusbar.phone.ScrimController", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(scrimcontrollerclass, "updateScrimColor", View.class, float.class, int.class, new XC_MethodHook() {
            @TargetApi(Build.VERSION_CODES.S)
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                class ScrimField {
                    final Object object;
                    final int color;
                    final float targetAlpha;
                    ScrimField(Object object, int color, float targetAlpha) {
                        this.object = object;
                        this.color = color;
                        this.targetAlpha = targetAlpha;
                    }
                }
                List<ScrimField> scrims = List.of(
                    new ScrimField(XposedHelpers.findField(scrimcontrollerclass, "mScrimBehind").get(param.thisObject), Color.BLACK, 0.5f),
                    new ScrimField(XposedHelpers.findField(scrimcontrollerclass, "mNotificationsScrim").get(param.thisObject), Color.BLACK, 0.5f)
                );
                Optional<ScrimField> fieldOp = scrims.stream().filter(item -> item.object.equals(param.args[0])).findAny();
                if (!fieldOp.isPresent())
                    return;
                ScrimField field = fieldOp.get();
                param.args[1] = (float) param.args[1] * field.targetAlpha;
                //param.args[2] = field.color;
            }
        });
    }
}
