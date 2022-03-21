package com.programminghoch10.clearlineage.xposed.Hooks.Sv2;

import com.programminghoch10.clearlineage.xposed.HookCode;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SettingsHook implements HookCode {
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
    }
}
