FOLDER=/sdcard
SUFOLDER=/data/adb
adb shell touch $FOLDER/ClearLineage.zip
if [ $? -eq 0 ]; then
    adb push ClearLineage.zip $FOLDER
    adb shell su -c magisk --install-module $FOLDER/ClearLineage.zip
else
    adb root || exit 1
    adb push ClearLineage.zip $SUFOLDER
    adb shell magisk --install-module $SUFOLDER/ClearLineage.zip
    adb shell rm -v $SUFOLDER/ClearLineage.zip
fi
