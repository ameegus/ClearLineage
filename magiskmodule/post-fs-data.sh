#!/bin/bash

MODPATH=${0%/*}
LOGFILE="$MODPATH/clearlineage-log.txt"

# prepare logs
echo "Init post-fs-data $(date)" > $LOGFILE
echo "MODPATH=$MODPATH" >> $LOGFILE

# constants
CLEARLINEAGE_XPOSED_PACKAGE="com.programminghoch10.clearlineage.xposed"
CLEARLINEAGE_XPOSED_PACKAGE_PATH="/system/app/clearlineage/ClearLineage-xposed.apk"

# disable module check / function
DISABLEFILE="$MODPATH/disable"
if [ -f $DISABLEFILE ]; then
    echo "Module disabled." >> $LOGFILE
    exit 0
fi
disable_and_exit() {
    echo "Disabling module..." >> $LOGFILE
    touch $DISABLEFILE
    exit 0
}

# sqlite setup
SQLITE=$(which sqlite3)
echo "SQLITE=$SQLITE" >> $LOGFILE
if [ ! -f "$SQLITE" ]; then
    echo "fatal: sqlite not found!" >> $LOGFILE
    disable_and_exit
fi
echo "$($SQLITE -version)" >> $LOGFILE

# locate LSPosed module config database
LSPOSED_MODULE_CONFIG_DATABASE="/data/adb/lspd/config/modules_config.db"
if [ ! -f $LSPOSED_MODULE_CONFIG_DATABASE ]; then 
    echo "fatal: can't locate LSPosed module config database!" >> $LOGFILE
    disable_and_exit
fi

# query module id
query_module_id() {
    echo "querying module id" >> $LOGFILE
    XPOSED_MODULE_ID=$($SQLITE $LSPOSED_MODULE_CONFIG_DATABASE "select mid from modules where module_pkg_name='$CLEARLINEAGE_XPOSED_PACKAGE'" 2>>$LOGFILE)
    echo "XPOSED_MODULE_ID=$XPOSED_MODULE_ID" >> $LOGFILE
}
query_module_id
if [ -z "$XPOSED_MODULE_ID" ]; then 
    # module id not found, try to create it
    $SQLITE $LSPOSED_MODULE_CONFIG_DATABASE "insert into modules (module_pkg_name, apk_path, enabled) values ('$CLEARLINEAGE_XPOSED_PACKAGE', '$CLEARLINEAGE_XPOSED_PACKAGE_PATH', 1)" >> $LOGFILE 2>&1
    query_module_id
    if [ -z "$XPOSED_MODULE_ID" ]; then 
        echo "fatal: can't query module id after creating it" >> $LOGFILE
        disable_and_exit
    fi
fi

# enable module
$SQLITE $LSPOSED_MODULE_CONFIG_DATABASE "update modules set enabled = '1' where mid='$XPOSED_MODULE_ID'" >> $LOGFILE 2>&1

# set scope
$SQLITE $LSPOSED_MODULE_CONFIG_DATABASE "delete from scope where mid='$XPOSED_MODULE_ID'" >> $LOGFILE 2>&1
for scope_pkg in $(cat $MODPATH/scope.txt); do
    $SQLITE $LSPOSED_MODULE_CONFIG_DATABASE "insert into scope (mid, app_pkg_name, user_id) values ('$XPOSED_MODULE_ID', '$scope_pkg', 0)" >> $LOGFILE 2>&1
done

echo "done setting up lsposed config" >> $LOGFILE
