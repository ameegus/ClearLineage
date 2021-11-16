#!/bin/bash

case $API in
	29)
		ui_print "Android 10 detected!"
        ui_print "Only limited functionality is available!"
		;;
	30)
		;;
	31)
		ui_print "Android 12 support is experimental!"
		# remove accent colors, they can't be used due to Material You
		rm -r "$MODPATH/system/product/overlay"
		;;
	*)
		abort "Your Android version is not supported!"
		;;
esac

ui_print
ui_print "Welcome to the ClearLineage Revolution"
ui_print

ui_print "- Copying files..."
for FOLDER in $(ls "$MODPATH/files/sdk$API"); do
	cp -rv "$MODPATH/files/sdk$API/$FOLDER" "$MODPATH"
done
rm -r "$MODPATH/files"
