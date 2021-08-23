
if [ $API -gr 30 ] || [ $API -lt 29]; then
	abort "Your Android version is not supported!"
fi

if [ $API -eq 29 ]; then
	ui_print "Android 10 detected, removing overlays..."
	rm -r "$MODPATH"/system/product/overlay
fi
