package com.programminghoch10.clearlineage.xposed;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface HookCode {
    void hook(XC_LoadPackage.LoadPackageParam param) throws Exception;
}
