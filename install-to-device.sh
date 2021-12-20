#!/bin/bash

FOLDER=/sdcard
SUFOLDER=/data/adb

pushasuser() {
    echo "Pushing as user to $FOLDER"
    adb push ClearLineage.zip $FOLDER
    adb shell su -c magisk --install-module $FOLDER/ClearLineage.zip
    exit $?
}

pushasroot() {
    adb root || exit 1
    echo "Pushing as root to $SUFOLDER"
    adb push ClearLineage.zip $SUFOLDER
    adb shell magisk --install-module $SUFOLDER/ClearLineage.zip
    exit_code=$?
    adb shell rm -v $SUFOLDER/ClearLineage.zip
    exit $exit_code
}

# manual selection passed as parameter
if [[ "$1" == "root" ]]; then
    pushasroot
fi
if [[ "$1" == "user" ]]; then
    pushasroot
fi

# detect automatically
adb shell touch $FOLDER/ClearLineage.zip
if [ $? -eq 0 ]; then
    pushasuser
else
    pushasroot
fi
