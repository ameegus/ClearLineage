#!/bin/bash
rm -v ClearLineage.zip
./gradlew assemble || exit 1
rm -r magiskmodule/system
rm -r magiskmodule/files
for SDK in 30 31; do
    mkdir -v -p magiskmodule/files/sdk$SDK/system/product/overlay
    for VERSION in android systemui; do
        cp -v $VERSION-sdk$SDK/build/outputs/apk/release/$VERSION-sdk$SDK-release.apk magiskmodule/files/sdk$SDK/system/product/overlay/ClearLineage-$VERSION-sdk$SDK.apk
    done
done
mkdir -v -p magiskmodule/system/app/clearlineage
cp -v xposed/build/outputs/apk/release/xposed-release.apk magiskmodule/system/app/clearlineage/ClearLineage-xposed.apk
cd magiskmodule
zip -v -r -0 ../ClearLineage.zip *
cd ..
