#!/bin/bash

case $API in
	29)
		;;
	30)
		;;
	31)
		;;
	*)
		abort "Your Android version is not supported!"
		;;
esac

for FOLDER in $(ls "$MODPATH/files/sdk$API"); do
	cp -r "$MODPATH/files/sdk$API/$FOLDER" "$MODPATH"
done
for FOLDER in $(ls "$MODPATH/files/all"); do
	cp -r "$MODPATH/files/all/$FOLDER" "$MODPATH"
done
rm -r "$MODPATH/files"

ui_print
ui_print "Welcome to the ClearLineage Revolution!"
ui_print
