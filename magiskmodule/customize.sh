#!/bin/bash

case $API in
	29)
		ui_print "Android 10 detected!"
        	ui_print "Only installing XPosed module"
	        rm -r "$MODPATH"/system/product/overlay
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
