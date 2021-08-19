call compile.bat
call install-to-device.bat
timeout /t 5
REM adb reboot
REM echo using this to prevent reboot when missing su, because likely the module install failed
adb shell su -c reboot
timeout /t 30
