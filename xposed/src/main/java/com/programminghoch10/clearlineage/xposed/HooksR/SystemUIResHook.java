package com.programminghoch10.clearlineage.xposed.HooksR;

import android.view.View;

import com.programminghoch10.clearlineage.xposed.HookRes;
import com.programminghoch10.clearlineage.xposed.HooksMap;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class SystemUIResHook implements HookRes {
    @Override
    public void hook(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Exception {
        resparam.res.hookLayout(HooksMap.PACKAGE_SYSTEMUI, "layout", "qs_panel", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                View quick_settings_status_bar_background = liparam.view.findViewById(
                        liparam.res.getIdentifier("quick_settings_status_bar_background", "id", HooksMap.PACKAGE_SYSTEMUI)
                );
                quick_settings_status_bar_background.setBackgroundColor(0x80000000);
            }
        });
    }
}
