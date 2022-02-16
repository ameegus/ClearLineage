#!/bin/bash
rm -v ClearLineage*.zip
./gradlew :xposed:clean assemble || exit 1
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
    for SDK in 29 30 31; do
        [ "$OVERLAY" = "accent" ] && [ "$SDK" = "31" ] && continue # due to material you, accent colors are unnecessary on 31
        mkdir -p magiskmodule/files/sdk$SDK/system/product/overlay
        for ITEM in $(ls $OVERLAY); do
            cp -v $OVERLAY/$ITEM/build/outputs/apk/release/$ITEM-release.apk magiskmodule/files/sdk$SDK/system/product/overlay/ClearLineage-$OVERLAY-$ITEM.apk
        done
    done
done
mkdir -p magiskmodule/files/all/system/product/overlay/LineageBlackTheme
cp -v emptyblacktheme/build/outputs/apk/release/emptyblacktheme-release.apk magiskmodule/files/all/system/product/overlay/LineageBlackTheme/LineageBlackTheme.apk
if [[ `git status --porcelain` ]]; then CHANGES="+"; else CHANGES="-"; fi
VERSIONCODE=$(git rev-list --count HEAD)
VERSION=v$VERSIONCODE$CHANGES\($(git log -1 --pretty=%h)\)
#VERSION=$(date +%Y%m%d-%H%M%S)
ID=$(cat module.prop | grep "id" | cut -d "=" -f2)
NAME=$(cat module.prop | grep "name" | cut -d "=" -f2)
AUTHOR=$(cat module.prop | grep "author" | cut -d "=" -f2)
DESC=$(cat module.prop | grep "description" | cut -d "=" -f2)
UPDATEJSON=$(cat module.prop | grep "updateJson" | cut -d "=" -f2)
echo "id=$ID
name=$NAME
version=$VERSION
versionCode=$VERSIONCODE
author=$AUTHOR
description=$DESC
updateJson=$UPDATEJSON" > magiskmodule/module.prop
cat xposed/src/main/res/values/scope.xml | grep item  | cut -d'>' -f2 | cut -d'<' -f1 > magiskmodule/scope.txt
cd magiskmodule
zip -q -r -0 ../ClearLineage-$VERSION.zip *
cd ..
cp -v ClearLineage-$VERSION.zip ClearLineage.zip
