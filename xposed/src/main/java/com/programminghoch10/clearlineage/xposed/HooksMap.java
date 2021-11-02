package com.programminghoch10.clearlineage.xposed;

import android.os.Build;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HooksMap {
    public static String PACKAGE_SYSTEMUI = "com.android.systemui";
    public static String PACKAGE_LAUNCHER3 = "com.android.launcher3";
    public static List<String> supportedPackages = new LinkedList<>();
    static {
        Arrays.stream(HooksMap.class.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getType().equals(String.class))
                .forEach(field -> {
                    try {
                        supportedPackages.add((String) field.get(null));
                    } catch (IllegalAccessException ignored) {}
                });
    }

    public static MultiKeyMap<Integer, String, Class<?>> map = new MultiKeyMap<>();
    static {
        // Q uses same hooks as R
        map.put(Build.VERSION_CODES.Q, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.HooksR.SystemUIHook.class);
        map.put(Build.VERSION_CODES.Q, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.HooksR.Launcher3Hook.class);

        // R
        map.put(Build.VERSION_CODES.R, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.HooksR.SystemUIHook.class);
        map.put(Build.VERSION_CODES.R, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.HooksR.Launcher3Hook.class);

        // S
        map.put(Build.VERSION_CODES.S, PACKAGE_SYSTEMUI, com.programminghoch10.clearlineage.xposed.HooksS.SystemUIHook.class);
        map.put(Build.VERSION_CODES.S, PACKAGE_LAUNCHER3, com.programminghoch10.clearlineage.xposed.HooksS.Launcher3Hook.class);
    }

}
