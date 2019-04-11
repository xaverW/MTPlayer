#!/bin/sh
#
# Wenn der Arbeitsspeicher knapp ist, kann das helfen:
# java -Xms128M -Xmx1G -jar ./MTPlayer.jar "$@"


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

if [ -n "$JAVA_HOME" ]; then
  $JAVA_HOME/bin/java -jar $mtplayer $einstellungen "$@"
else
  java -jar $mtplayer $einstellungen "$@"
fi
