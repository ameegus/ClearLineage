package com.programminghoch10.clearlineage.xposed;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Utils {
    public static boolean isCurrentThreadCalledFromMethod(Method method) {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(stackTraceElement ->
                        stackTraceElement.getMethodName().equals(method.getName())
                                && stackTraceElement.getClassName().equals(method.getDeclaringClass().getName())
                );
    }
}
