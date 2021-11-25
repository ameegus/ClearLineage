#!/bin/bash

case $API in
	29)
		;;
	30)
		;;
	31)
		ui_print "Android 12 support is experimental!"
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
for FOLDER in $(ls "$MODPATH/files/all"); do
	cp -rv "$MODPATH/files/all/$FOLDER" "$MODPATH"
done
rm -r "$MODPATH/files"
