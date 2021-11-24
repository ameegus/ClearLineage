#!/bin/bash
if [ -f "ClearLineage.zip" ]; then rm -v ClearLineage.zip; fi
./gradlew assemble || exit 1
rm -r magiskmodule/files
for SDK in 29 30 31; do
    mkdir -p magiskmodule/files/sdk$SDK/system/product/overlay
    for TARGET in android systemui; do
        #if [ ! -d $TARGET/sdk$SDK ]; then continue; fi;
        cp -v $TARGET/sdk$SDK/build/outputs/apk/release/sdk$SDK-release.apk magiskmodule/files/sdk$SDK/system/product/overlay/ClearLineage-$TARGET-sdk$SDK.apk
    done
done
mkdir -p magiskmodule/files/all/system/app/clearlineage
cp -v xposed/build/outputs/apk/release/xposed-release.apk magiskmodule/files/all/system/app/clearlineage/ClearLineage-xposed.apk
mkdir -p magiskmodule/files/all/system/product/overlay
for ACCENT in $(ls accent); do
    cp -v accent/$ACCENT/build/outputs/apk/release/$ACCENT-release.apk magiskmodule/files/all/system/product/overlay/ClearLineage-Accent-$ACCENT.apk
done
mkdir -p magiskmodule/files/all/system/product/overlay/LineageBlackTheme
cp -v emptyblacktheme/build/outputs/apk/release/emptyblacktheme-release.apk magiskmodule/files/all/system/product/overlay/LineageBlackTheme/LineageBlackTheme.apk
VERSION=$(git log -1 --pretty=%h)
#VERSION=$(date +%Y%m%d-%H%M%S)
VERSIONCODE=$(git rev-list --count HEAD)
ID=$(cat module.prop | grep "id" | cut -d "=" -f2)
NAME=$(cat module.prop | grep "name" | cut -d "=" -f2)
AUTHOR=$(cat module.prop | grep "author" | cut -d "=" -f2)
DESC=$(cat module.prop | grep "description" | cut -d "=" -f2)
echo "id=$ID
name=$NAME
version=$VERSION
versionCode=$VERSIONCODE
author=$AUTHOR
description=$DESC" > magiskmodule/module.prop
cd magiskmodule
#sed -i "s/version=.*/version=$VERSION/g" module.prop
#sed -i "s/versionCode=.*/versionCode=$VERSIONCODE/g" module.prop
zip -q -r -0 ../ClearLineage.zip *
cd ..
