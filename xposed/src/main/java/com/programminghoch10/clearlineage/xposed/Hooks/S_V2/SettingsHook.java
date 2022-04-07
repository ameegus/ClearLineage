package com.programminghoch10.clearlineage.xposed.Hooks.S_V2;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.programminghoch10.clearlineage.xposed.HookCode;
import com.programminghoch10.clearlineage.xposed.Utils;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SettingsHook implements HookCode {
    // this transforms all colors to given color, and flattens every alpha > 0 to 255
    private static ColorMatrixColorFilter getFlatteningColorFilter(int color) {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                0, 0, 0, 0, Color.red(color), // r
                0, 0, 0, 0, Color.green(color), // g
                0, 0, 0, 0, Color.blue(color), // b
                0, 0, 0, 255, 0, // a
        }));
    }
    
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> highlightabletoplevelpreferenceadapterclass = XposedHelpers.findClass("com.android.settings.widget.HighlightableTopLevelPreferenceAdapter", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(
                highlightabletoplevelpreferenceadapterclass,
                "com.android.settings.homepage.SettingsHomepageActivity",
                "androidx.preference.PreferenceGroup",
                "androidx.recyclerview.widget.RecyclerView",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String[] fields = {
                                "mTitleColor",
                                "mSummaryColor",
                                "mIconColor",
                        };
                        for (String field : fields) {
                            XposedHelpers.findField(highlightabletoplevelpreferenceadapterclass, field + "Highlight").set(param.thisObject,
                                    XposedHelpers.findField(highlightabletoplevelpreferenceadapterclass, field + "Normal").get(param.thisObject)
                            );
                        }
                    }
                });
        
        Class<?> settingshomepageactivityclass = XposedHelpers.findClass("com.android.settings.homepage.SettingsHomepageActivity", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(settingshomepageactivityclass, "updateHomepageBackground", XC_MethodReplacement.DO_NOTHING);
        
        Class<?> toplevelsettingsclass = XposedHelpers.findClass("com.android.settings.homepage.TopLevelSettings", lpparam.classLoader);
        Method onCreatePreferencesMethod = XposedHelpers.findMethodExact(toplevelsettingsclass, "onCreatePreferences", Bundle.class, String.class);
        XposedHelpers.findAndHookMethod(Drawable.class, "setTint", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!Utils.isCurrentThreadCalledFromMethod(onCreatePreferencesMethod)) return;
                Drawable drawable = (Drawable) param.thisObject;
                int color = (int) param.args[0];
                ColorFilter colorFilter = getFlatteningColorFilter(color);
                drawable.setColorFilter(colorFilter);
            }
        });
    }
}
