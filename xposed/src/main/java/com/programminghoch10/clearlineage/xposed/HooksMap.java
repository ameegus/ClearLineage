package com.programminghoch10.clearlineage.xposed;

import android.os.Build;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HooksMap {
    public static final String PACKAGE_SYSTEMUI = "com.android.systemui";
    public static final String PACKAGE_LAUNCHER3 = "com.android.launcher3";
    public static final String PACKAGE_SETTINGS = "com.android.settings";
    public static final String TAG = "ClearLineage";
    public static List<String> supportedPackages = new LinkedList<>();
    public static List<HookEntry> list = new LinkedList<>();

    static {
        Arrays.stream(HooksMap.class.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getType().equals(String.class))
                .filter(field -> field.getName().startsWith("PACKAGE_"))
                .forEach(field -> {
                    try {
                        supportedPackages.add((String) field.get(null));
                    } catch (IllegalAccessException ignored) {
                    }
                });
    }

    static {
        // Q
        list.add(new HookEntry(HOOKTYPE.BOTH, Build.VERSION_CODES.Q, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.Q, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.Launcher3Hook.class));
        list.add(new HookEntry(HOOKTYPE.RES, Build.VERSION_CODES.Q, PACKAGE_SETTINGS, com.programminghoch10.clearlineage.xposed.Hooks.SettingsHook.class));

        // R
        list.add(new HookEntry(HOOKTYPE.BOTH, Build.VERSION_CODES.R, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.R, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.Hooks.unifiedQR.Launcher3Hook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.R, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.R.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.R, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.Hooks.R.Launcher3Hook.class));
        list.add(new HookEntry(HOOKTYPE.RES, Build.VERSION_CODES.R, PACKAGE_SETTINGS, com.programminghoch10.clearlineage.xposed.Hooks.SettingsHook.class));

        // S
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.S, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.Hooks.S.SystemUIHook.class));
        list.add(new HookEntry(HOOKTYPE.CODE, Build.VERSION_CODES.S, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.Hooks.S.Launcher3Hook.class));
        list.add(new HookEntry(HOOKTYPE.RES, Build.VERSION_CODES.S, PACKAGE_SETTINGS, com.programminghoch10.clearlineage.xposed.Hooks.SettingsHook.class));
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
