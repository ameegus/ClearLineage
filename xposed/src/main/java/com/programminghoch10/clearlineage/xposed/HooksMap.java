package com.programminghoch10.clearlineage.xposed;

import android.os.Build;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class HooksMap {
    public static final String PACKAGE_SYSTEMUI = "com.android.systemui";
    public static final String PACKAGE_LAUNCHER3 = "com.android.launcher3";
    public static final String PACKAGE_SETTINGS = "com.android.settings";
    public static final String TAG = "ClearLineage";
    public static List<String> supportedPackages;
    public static List<Integer> supportedSDKs;
    public static List<HookEntry> list = new LinkedList<>();
    
    static {
        // Q
        list.add(new HookEntry(HOOKTYPE.BOTH, Build.VERSION_CODES.Q, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.RES, Build.VERSION_CODES.Q, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.Q.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.Q, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.Launcher3Hook.class));
        list.add(new HookEntry(HOOKTYPE.RES, Build.VERSION_CODES.Q, PACKAGE_SETTINGS, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.SettingsHook.class));
        
        // R
        list.add(new HookEntry(HOOKTYPE.BOTH, Build.VERSION_CODES.R, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.R, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.Launcher3Hook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.R, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.R.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.R, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.Hooks.R.Launcher3Hook.class));
        list.add(new HookEntry(HOOKTYPE.RES, Build.VERSION_CODES.R, PACKAGE_SETTINGS, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.SettingsHook.class));
        
        // Sv2
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.S_V2, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.Sv2.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.S_V2, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.Hooks.Sv2.Launcher3Hook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.S_V2, PACKAGE_SETTINGS, com.programminghoch10.clearlineage.xposed.Hooks.Sv2.SettingsHook.class));
    }
    
    static {
        supportedSDKs = list.stream().map(hookEntry -> hookEntry.sdk).distinct().collect(Collectors.toList());
        supportedPackages = list.stream().map(hookEntry -> hookEntry.packageName).distinct().collect(Collectors.toList());
    }
    
    public enum HOOKTYPE {
        CODE, // hooking java code
        RES,  // hooking resources
        BOTH, // hook code and resources
    }
    
    static class HookEntry {
        HOOKTYPE hooktype;
        int sdk;
        String packageName;
        Class<?> hookClass;
        
        HookEntry(HOOKTYPE hooktype, int sdk, String packageName, Class<?> hookClass) {
            this.hooktype = hooktype;
            this.sdk = sdk;
            this.packageName = packageName;
            this.hookClass = hookClass;
        }
    }
    
}
