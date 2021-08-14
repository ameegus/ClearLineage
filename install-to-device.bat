adb root
adb shell su -c mount -o rw,remount /vendor
adb push android/build/outputs/apk/debug/android-debug.apk /vendor/overlay/ClearLineage-android.apk
adb push systemui/build/outputs/apk/debug/systemui-debug.apk /vendor/overlay/ClearLineage-systemui.apk
timeout /t 5
adb reboot
