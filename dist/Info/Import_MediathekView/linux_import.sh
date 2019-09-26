#!/bin/sh

dir=`dirname "$0"`
cd "$dir"

mtdir=~/.p2Mtplayer
mvdir=~/.mediathek3

date=$(date +%y.%m.%d-%H:%M:%S)

echo ========================
echo Import Config
echo Date:  $date
echo ========================

if [ -d "$mtdir" ];then
	echo "rename old mt config-dir to:  " $mtdir-$date
	mv $mtdir $mtdir-$date
fi

echo "create new mt new config-dir: " $mtdir
mkdir $mtdir

if [ -d "$mvdir" ];then
	
	echo "copy abos"
	cp $mvdir/downloadAbos.txt $mtdir/downloads.txt

	
	echo "copy history"
	cp $mvdir/history.txt $mtdir/
	

	echo "copy config and change abo duration"
	#cp $mvdir/mediathek.xml $mtdir/mtplayer.xml

	sed -E 's/<Mindestdauer>([0-9]*)<\/Mindestdauer><min_max>true<\/min_max>/<Mindestdauer>\1<\/Mindestdauer><Maxdauer>150<\/Maxdauer>/g;
	s/<Mindestdauer>([0-9]*)<\/Mindestdauer><min_max>false<\/min_max>/<Mindestdauer>0<\/Mindestdauer><Maxdauer>\1<\/Maxdauer>/g;
	s/<Maxdauer>0<\/Maxdauer>/<Maxdauer>150<\/Maxdauer>/g' $mvdir/mediathek.xml > $mtdir/mtplayer.xml 
	
fi


cd $OLDPWD
