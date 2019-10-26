#!/bin/sh

# =============
# thx Thorolf
# =============


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
	
	# first get some settings from settings.xml
	USERAGENT=$(sed -En 's/.*<user_agent>(.*)<\/user_agent>/<system-useragent>\1<\\\/system-useragent>/gp' $mvdir/settings.xml)
	GEOMARK=$(sed -En 's/.*<report>(.*)<\/report>/<system-mark-geo>\1<\\\/system-mark-geo>/gp' $mvdir/settings.xml)
	GEOLOC=$(sed -En 's/.*<location>([a-zA-Z]*)<\/location>/<system-geo-home-place>\1<\\\/system-geo-home-place>/gp' $mvdir/settings.xml)
	MAXSPEED=$(sed -En 's/.*<limit>(.*)<\/limit>/<download-max-bandwith-kbyte>\1<\\\/download-max-bandwith-kbyte>/gp' $mvdir/settings.xml)
	
	# copy settings from mediathek.xml, change variable names as necessary and insert the settings from above
	sed -E \
		"s/<Abo-Mindestdauer-Minuten>(.*)<\/Abo-Mindestdauer-Minuten>/<abo-minute-min-size>\1<\/abo-minute-min-size>/g;
		s/<Abos-sofort-suchen>(.*)<\/Abos-sofort-suchen>/<abo-search-now>\1<\/abo-search-now>/g;
		s/<Blacklist-auch-Abo>(.*)<\/Blacklist-auch-Abo>/<blacklist-show-abo>\1<\/blacklist-show-abo>/g;
		s/<Blacklist-ausgeschaltet>(.*)<\/Blacklist-ausgeschaltet>/<Blacklist-ein>\1<\/Blacklist-ein>/g;
		s/<Blacklist-Filmlaenge>(.*)<\/Blacklist-Filmlaenge>/<blacklist-min-film-duration>\1<\/blacklist-min-film-duration>/g;
		s/<Blacklist-Geo-nicht-anzeigen>(.*)<\/Blacklist-Geo-nicht-anzeigen>/<blacklist-show-no-geo>\1<\/blacklist-show-no-geo>/g;
		s/<Blacklist-ist-Whitelist>(.*)<\/Blacklist-ist-Whitelist>/<blacklist-is-whitelist>\1<\/blacklist-is-whitelist>/g;
		s/<Blacklist-Zukunft-nicht-anzeigen>(.*)<\/Blacklist-Zukunft-nicht-anzeigen>/<blacklist-show-no-future>\1<\/blacklist-show-no-future>/g;
		s/<Download-sofort-starten>(.*)<\/Download-sofort-starten>/<download-start-now>\1<\/download-start-now>/g;
		s/<Ersetzungstabelle-verwenden>(.*)<\/Ersetzungstabelle-verwenden>/<system-use-replacetable>\1<\/system-use-replacetable>/g;
		s/<Ersetzungstabelle><von>(.*)<\/von><nach>(.*)<\/nach><\/Ersetzungstabelle>/<Ersetzungstabelle><von>\1<\/von><to>\2<\/to><\/Ersetzungstabelle>/g;
		s/<max1DownloadProServer>(.*)<\/max1DownloadProServer>/<download-max-one-per-server>\1<\/download-max-one-per-server>/g;
		s/<Maxdauer>0<\/Maxdauer>/<Maxdauer>150<\/Maxdauer>/g;
		s/<maxDownload>(.*)<\/maxDownload>/<download-max-downloads>\1<\/download-max-downloads>/g;
		s/<Media_DB_ohne-Suffix>(.*)<\/Media_DB_ohne-Suffix>/<media-db-with-out-suffix>\1<\/media-db-with-out-suffix>/g;
		s/<Media_DB_Suffix>(.*)<\/Media_DB_Suffix>/<media-db-suffix>\1<\/media-db-suffix>/g;
		s/<Mindestdauer>(.*)<\/Mindestdauer><min_max>false<\/min_max>/<Mindestdauer>0<\/Mindestdauer><Maxdauer>\1<\/Maxdauer>/g;
		s/<Mindestdauer>(.*)<\/Mindestdauer><min_max>true<\/min_max>/<Mindestdauer>\1<\/Mindestdauer><Maxdauer>150<\/Maxdauer>/g;
		s/<nur-ascii>(.*)<\/nur-ascii>/<system-only-ascii>\1<\/system-only-ascii>/g;
		s/<pfad-vlc>(.*)<\/pfad-vlc>/<path-vlc>\1<\/path-vlc>/g;
		s/<Programm-Url-oeffnen>(.*)<\/Programm-Url-oeffnen>/<system-prog-open-url>\1<\/system-prog-open-url>/g;
		s/<system-anz-tage-filmilste>(.*)<\/system-anz-tage-filmilste>/<system-load-filmlist-max-days>\1<\/system-load-filmlist-max-days>/g;
		s/<Version-Programmset>(.*)<\/Version-Programmset>/<system-update-progset-version>\1<\/system-update-progset-version>/g;

		s/<\/system>/\t$USERAGENT\n\t$GEOMARK\n\t$GEOLOC\n\t$MAXSPEED\n<\/system>/g" \
		$mvdir/mediathek.xml > $mtdir/mtplayer.xml
		
	
fi


cd $OLDPWD

echo "done"

