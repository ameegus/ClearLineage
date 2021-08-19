call gradlew.bat assembleDebug
copy android\build\outputs\apk\debug\android-debug.apk magiskmodule\system\product\overlay
copy systemui\build\outputs\apk\debug\systemui-debug.apk magiskmodule\system\product\overlay
cd magiskmodule
tar -a -c -f ..\ClearLineage.zip *
cd ..
