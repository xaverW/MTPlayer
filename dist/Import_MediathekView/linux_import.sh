#!/bin/sh

dir=`dirname "$0"`
cd "$dir"

mtdir=~/.mtplayer
mvdir=~/.mediathek3

date=$(date +%y.%m.%d-%H:%M:%S)

echo ========================
echo Import Config
echo Date:    $date
echo ========================

if [ -d "$mtdir" ];then
	echo "rename mt config-dir"
	echo $mtdir-$date
	mv $mtdir $mtdir-$date
fi

echo "create mt config-dir"
mkdir $mtdir

if [ -d "$mvdir" ];then
	echo "copy config"
	cp $mvdir/downloadAbos.txt $mtdir/downloads.txt

	cp $mvdir/history.txt $mtdir/
	cp $mvdir/mediathek.xml $mtdir/mtplayer.xml
fi


cd $OLDPWD
