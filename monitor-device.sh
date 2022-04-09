#!/bin/bash

case "$1" in
    "logcat")
        while true; do
            echo "Waiting for device..."
            adb wait-for-device
            clear
            adb logcat -s ClearLineage LSPosed-Bridge
        done
        ;;
    "overlay")
        OVERLAY="$2"
        watch "adb shell cmd overlay dump com.programminghoch10.clearlineage.$OVERLAY"
        ;;
    *)
        echo "command not recognized!"
        ;;
esac
