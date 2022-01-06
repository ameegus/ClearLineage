#!/bin/bash

FOLDER=/sdcard
SUFOLDER=/data/adb

if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    echo "Usage: $0 [push mode: auto/user/root] [reboot yes/no] [adb device identifier]"
    echo "Defaults:"
    echo "  Push mode: auto"
    echo "  Reboot after sucessful install: no"
    echo "  The install is performed on all available adb devices."
    exit 0
fi

PUSHTYPE="$1"
REBOOT="$2"
DEVICE="$3"

if [ -z "$REBOOT" ]; then REBOOT="no"; fi
if [ -z "$PUSHTYPE" ]; then PUSHTYPE="auto"; fi
if [ "$PUSHTYPE" = "auto" ]; then PUSHTYPE="preferroot"; fi

if [ -z $DEVICE ]; then
    DEVICES="$(adb devices -l | grep transport_id | cut -d' ' -f 1)"
    DEVICESCOUNT="$(adb devices -l | grep transport_id | cut -d' ' -f 1 | wc -l)"
    if [ "$DEVICESCOUNT" = "0" ]; then 
        echo "No devices found!"
        exit 1
    fi
    if [ $DEVICESCOUNT -gt 1 ]; then
        echo "Found $DEVICESCOUNT devices"
        for device in $DEVICES; do
            echo "installing for $device"
            $0 $PUSHTYPE $REBOOT $device
        done
        exit 0
    fi
fi

ADB="adb"
if [ ! -z "$DEVICE" ]; then ADB="$ADB -s $DEVICE"; fi

rebootifwanted() {
    prev_exit_code="$1"
    if [ -z "$prev_exit_code" ]; then $prev_exit_code=$?; fi
    if [ "$REBOOT" != "yes" ]; then return; fi
    if [ "$prev_exit_code" = "0" ]; then $ADB reboot; fi
}

pushasuser() {
    echo "Pushing as user to $FOLDER"
    $ADB push ClearLineage.zip $FOLDER
    $ADB shell su -c magisk --install-module $FOLDER/ClearLineage.zip
    exit_code="$?"
    rebootifwanted "$exit_code"
    exit $exit_code
}

waitfordevice() {
    if [ -z "$DEVICE" ]; then adb wait-for-device; return; fi
    while [ "$(adb devices -l | grep $DEVICE | wc -l)" = "0" ]; do
        sleep 1
    done
}

pushasroot() {
    $ADB root
    waitfordevice
    if [ "$($ADB shell id -u)" != "0" ]; then pushasuser; fi
    echo "Pushing as root to $SUFOLDER"
    $ADB push ClearLineage.zip $SUFOLDER
    $ADB shell magisk --install-module $SUFOLDER/ClearLineage.zip
    exit_code=$?
    $ADB shell rm -v $SUFOLDER/ClearLineage.zip
    rebootifwanted "$exit_code"
    exit $exit_code
}

# manual selection passed as parameter
if [ "$PUSHTYPE" = "root" ]; then
    pushasroot
fi
if [ "$PUSHTYPE" = "user" ]; then
    pushasuser
fi

# detect automatically
if [ "$PUSHTYPE" = "preferuser" ]; then
    $ADB shell touch $FOLDER/ClearLineage.zip
    if [ $? -eq 0 ]; then
        pushasuser
    else
        pushasroot
    fi
fi
if [ "$PUSHTYPE" = "preferroot" ]; then
    # just push as root, if it fails it will automatically try pushing as user
    pushasroot
fi
