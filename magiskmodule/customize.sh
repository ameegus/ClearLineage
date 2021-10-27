#!/bin/bash

case $API in
	29)
		ui_print "Android 10 detected!"
        	ui_print "Only installing XPosed module"
	        rm -r "$MODPATH"/system/product/overlay
		;;
	30)
		;;
	*)
		abort "Your Android version is not supported!"
		;;
esac
