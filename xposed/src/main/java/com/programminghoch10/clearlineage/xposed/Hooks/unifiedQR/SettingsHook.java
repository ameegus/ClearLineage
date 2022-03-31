package com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.programminghoch10.clearlineage.xposed.HookCode;
import com.programminghoch10.clearlineage.xposed.HookRes;
import com.programminghoch10.clearlineage.xposed.HooksMap;
import com.programminghoch10.clearlineage.xposed.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SettingsHook implements HookCode, HookRes {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> switchbarclass = XposedHelpers.findClass("com.android.settings.widget.SwitchBar", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(switchbarclass, Context.class, AttributeSet.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final String fieldName = "mBackgroundActivatedColor";
                int prevcolor = (int) XposedHelpers.getIntField(param.thisObject, fieldName);
                int newcolor = Utils.setAlpha(prevcolor, 0xa0);
                XposedHelpers.setIntField(param.thisObject, fieldName, newcolor);
                
                Context context = (Context) param.args[0];
                TextView textView = (TextView) XposedHelpers.getObjectField(param.thisObject, "mTextView");
                int textColor = context.obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary}).getColor(0, textView.getCurrentTextColor());
                textView.setTextColor(textColor);
            }
        });
    }
    
    @Override
    public void hook(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Exception {
        resparam.res.hookLayout(HooksMap.PACKAGE_SETTINGS, "layout", "settings_homepage_container", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                View view = liparam.view.findViewById(
                        liparam.res.getIdentifier("main_content", "id", HooksMap.PACKAGE_SETTINGS)
                );
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }
}
