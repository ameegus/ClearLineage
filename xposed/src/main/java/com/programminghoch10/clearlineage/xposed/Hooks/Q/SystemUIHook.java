package com.programminghoch10.clearlineage.xposed.Hooks.Q;

import android.graphics.Color;

import com.programminghoch10.clearlineage.xposed.HookRes;
import com.programminghoch10.clearlineage.xposed.HooksMap;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class SystemUIHook implements HookRes {
    @Override
    public void hook(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Exception {
        resparam.res.hookLayout(HooksMap.PACKAGE_SYSTEMUI, "layout", "quick_settings_footer", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                liparam.view.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }
}
