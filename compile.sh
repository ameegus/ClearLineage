#!/bin/bash
rm -v ClearLineage.zip
./gradlew assembleDebug || exit 1
rm -r magiskmodule/system
mkdir -v -p magiskmodule/system/product/overlay
mkdir -v -p magiskmodule/system/app/clearlineage
cp -v android/build/outputs/apk/debug/android-debug.apk magiskmodule/system/product/overlay/ClearLineage-android.apk
cp -v systemui/build/outputs/apk/debug/systemui-debug.apk magiskmodule/system/product/overlay/ClearLineage-systemui.apk
cp -v xposed/build/outputs/apk/debug/xposed-debug.apk magiskmodule/system/app/clearlineage/ClearLineage-xposed.apk
cd magiskmodule
zip -v -r -0 ../ClearLineage.zip *
cd ..
