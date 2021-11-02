package com.programminghoch10.clearlineage.xposed;

import de.robv.android.xposed.callbacks.XCallback;

public interface Hook<T extends XCallback.Param> {
    void hook(T param) throws Exception;
}
