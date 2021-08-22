call gradlew.bat assembleDebug
rmdir /S /Q magiskmodule\system
mkdir magiskmodule\system\product\overlay
mkdir magiskmodule\system\app\clearlineage
copy android\build\outputs\apk\debug\android-debug.apk magiskmodule\system\product\overlay
copy systemui\build\outputs\apk\debug\systemui-debug.apk magiskmodule\system\product\overlay
copy xposed\build\outputs\apk\debug\xposed-debug.apk magiskmodule\system\app\clearlineage\clearlineage.apk
cd magiskmodule
tar -a -c -f ..\ClearLineage.zip *
cd ..
