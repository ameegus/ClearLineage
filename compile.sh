#!/bin/bash
rm -v ClearLineage.zip
./gradlew assemble || exit 1
rm -r magiskmodule/system
rm -r magiskmodule/files
for SDK in 30 31; do
    mkdir -p magiskmodule/files/sdk$SDK/system/product/overlay
    for VERSION in android systemui; do
        cp -v $VERSION-sdk$SDK/build/outputs/apk/release/$VERSION-sdk$SDK-release.apk magiskmodule/files/sdk$SDK/system/product/overlay/ClearLineage-$VERSION-sdk$SDK.apk
    done
done
mkdir -p magiskmodule/system/app/clearlineage
cp -v xposed/build/outputs/apk/release/xposed-release.apk magiskmodule/system/app/clearlineage/ClearLineage-xposed.apk
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
