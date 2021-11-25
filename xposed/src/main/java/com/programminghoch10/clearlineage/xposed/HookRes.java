package com.programminghoch10.clearlineage.xposed;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;

public interface HookRes {
    void hook(XC_InitPackageResources.InitPackageResourcesParam param) throws Exception;
}
