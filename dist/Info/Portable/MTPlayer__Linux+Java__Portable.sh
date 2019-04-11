#!/bin/sh
#
# Wenn der Arbeitsspeicher knapp ist, kann das helfen:
# ./Java/bin/java -Xms128M -Xmx1G -jar ./MTPlayer.jar "$@"


dir=$(dirname $(readlink -f "$0"))
cd "$dir"

mtplayer="./MTPlayer.jar"
einstellungen="./Einstellungen"

if [ -f ../MTPlayer.jar ]; then
	cd ..
fi

echo
echo ============================================
echo Programmverzeichnis: $PWD
echo Programmdatei: $mtplayer
echo Einstellungen: $einstellungen
echo ============================================
echo

./Java/bin/java -jar $mtplayer $einstellungen "$@"

