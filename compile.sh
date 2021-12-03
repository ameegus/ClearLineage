#!/bin/bash
if [ -f "ClearLineage.zip" ]; then rm -v ClearLineage.zip; fi
./gradlew assemble || exit 1
rm -r magiskmodule/files
for SDK in 29 30 31; do
    mkdir -p magiskmodule/files/sdk$SDK/system/product/overlay
    for TARGET in android systemui trebuchet settings; do
        if [ ! -d $TARGET/sdk$SDK ]; then continue; fi;
        cp -v $TARGET/sdk$SDK/build/outputs/apk/release/sdk$SDK-release.apk magiskmodule/files/sdk$SDK/system/product/overlay/ClearLineage-$TARGET-sdk$SDK.apk
    done
done
mkdir -p magiskmodule/files/all/system/app/clearlineage
cp -v xposed/build/outputs/apk/release/xposed-release.apk magiskmodule/files/all/system/app/clearlineage/ClearLineage-xposed.apk
for OVERLAY in accent shape; do
    for SDK in 29 30; do
        mkdir -p magiskmodule/files/sdk$SDK/system/product/overlay
        for ITEM in $(ls $OVERLAY); do
            cp -v $OVERLAY/$ITEM/build/outputs/apk/release/$ITEM-release.apk magiskmodule/files/sdk$SDK/system/product/overlay/ClearLineage-$OVERLAY-$ITEM.apk
        done
    done
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
zip -q -r -0 ../ClearLineage.zip *
cd ..
