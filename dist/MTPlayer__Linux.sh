#!/bin/sh
#
# Wenn der Arbeitsspeicher knapp ist, kann das helfen:
# java -Xms128M -Xmx1G -jar ./MTPlayer.jar "$@"


#dir=`dirname "$0"`
dir=$(dirname $(readlink -f "$0"))
cd "$dir"

if [ -n "$JAVA_HOME" ]; then
  $JAVA_HOME/bin/java -jar ./MTPlayer.jar "$@"
else
  java -jar ./MTPlayer.jar "$@"
fi
cd $OLDPWD
