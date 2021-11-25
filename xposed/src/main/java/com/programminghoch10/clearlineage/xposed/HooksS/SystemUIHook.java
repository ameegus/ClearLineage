package com.programminghoch10.clearlineage.xposed.HooksS;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookCode;

import java.util.List;
import java.util.Optional;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHook implements HookCode {
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
    }
}
