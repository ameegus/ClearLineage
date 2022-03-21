package com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR;

import android.graphics.Color;
import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookRes;
import com.programminghoch10.clearlineage.xposed.HooksMap;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class SettingsHook implements HookRes {
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
