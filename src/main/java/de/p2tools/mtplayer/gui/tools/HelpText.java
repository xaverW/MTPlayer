/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.p2tools.mtplayer.gui.tools;

import de.p2tools.p2lib.P2LibConst;

public class HelpText {

    public static final String MEDIA_CLEANING_CONFIG_DIALOG =
            "Zum Suchen in der Mediensammlung oder den geladenen Abos kann " +
                    "der Suchtext aufbereitet werden." +
                    "\n\n" +
                    "Zuerst kann ausgewählt werden, WIE der Suchtext gebaut wird. Die Auswahl ist: Der Suchtext ist " +
                    "der *Titel* das *Thema* oder *Thema und Titel* des Films." +
                    "\n\n" +
                    "Dann wird angegeben, WO der Suchtext vorkommen muss, also wo der Suchtext gesucht wird." +
                    "\n" +
                    "Hier steht in der Mediensammlung der *Dateiname* und/oder der *Pfad* zur Auswahl. " +
                    "\n" +
                    "Bei den Abos kann der Suchtext im *Titel* und/oder *Thema* des Abo-Films " +
                    "gesucht werden." +
                    "\n\n" +
                    "* Exakt den Begriff suchen *\n" +
                    "Dann wird exakt der Suchtext zum Suchen verwendet, er wird dazu in \" eingeschlossen." +
                    "\n\n" +
                    "* Putzen *\n" +
                    "Hier wird der Suchtext aufbereitet, ansonsten wird er direkt zum Suchen verwendet. Aufbereiten meint, dass " +
                    "Zeichen wie \"[\" oder das Datum, ... entfernt werden." +
                    "\n\n" +
                    "* Cleaning Liste anwenden *\n" +
                    "Die Zeichen/Wörter die in der Cleaning Liste stehen, werden aus dem Suchtext entfernt. Damit " +
                    "können viele \"Füllwörter\" aus dem Suchtext gelöscht werden um dadurch das Suchergebnis zu verbessern." +
                    "\n\n" +
                    "* Verknüpfen mit UND *\n" +
                    "Mit \"UND\" müssen die einzelnen Wörter im Suchtext ALLE im gesuchten Film " +
                    "vorkommen, ansonsten (ODER) muss nur eines davon vorkommen." +
                    "\n\n" +
                    "Die Cleaning-Liste enthält Zeichen und Wörter die entfernt werden sollen. \"Immer\" bedeutet, " +
                    "dass das Zeichen immer aus dem Suchtext gelöscht wird. Ist \"Immer\" nicht eingeschaltet, " +
                    "wird das Wort nur entfernt, wenn es im Suchtext \"frei\" steht. " +
                    "\n\n" +
                    "Ein Beispiel-Suchtext: \"ZDF und der ZDF-Comedy Sommer.\"" +
                    "\n" +
                    "In der Cleaning-Liste sind diese beiden Einträge: \".\" (Immer ist eingeschaltet) " +
                    "und \"ZDF\" (Immer ist ausgeschaltet). " +
                    "\n" +
                    "Jeder \".\" wird entfernt (\"Immer\"). Das erste \"ZDF\" wird hier entfernt, " +
                    "da es frei steht, das zweite nicht (\"Immer\" ist ausgeschaltet)." +

                    "\n\n" +
                    "Beim Entfernen von Teilen des Suchtextes wird der entfernte Text durch ein Leerzeichen ersetzt. So " +
                    "kann dann der restliche Teil des Suchtextes gut mit ',' oder ':' zusammengesetzt werden (Suche mit " +
                    "UND oder ODER)." +
                    "\n";

    public static final String MEDIA_COLLECTION =
            "Hier kann eine Mediensammlung angelegt werden. Das ist eine Liste der " +
                    "bereits vorhandenen Filme.\n" +
                    "\n" +
                    "Vor dem Download eines Films kann darin der Filmtitel gesucht werden, " +
                    "um doppelte Downloads zu vermeiden. Bei Filmen in den Ansichten \"Filme\" " +
                    "und \"Downloads\" geht dies mit dem Kontextmenü \"Titel in der Mediensammlung " +
                    "suchen\". Ein Dialog listet dann gefundene Filmtitel aus der Mediensammlung auf." +
                    "\n" +
                    "Im Tab Filme/Download werden die Medien auch unter der Tabelle mit angezeigt." +
                    "\n" +
                    "\n" +
                    "Hier im Konfigurations-Dialog werden die Ordner angegeben, deren Filmdateien " +
                    "in die Mediensammlung aufgenommen werden.\n" +
                    "\n" +
                    "\"Interne Medien\": Die angegebenen Pfade werden beim Programmstart durchsucht, " +
                    "sie müssen deshalb verfügbar sein, während das Programm läuft. Die Mediensammlung " +
                    "ist daher für interne Medien immer aktuell.\n" +
                    "\n" +
                    "\"Externe Medien\": Ihr Inhalt an Mediendateien wird beim Hinzufügen zur Liste " +
                    "hinzugefügt und steht dann dauerhaft im Programm und bei der Suche in der " +
                    "Mediensammlung zur Verfügung. Es ist daher nicht nötig, dass externe Medien " +
                    "beim Programmstart vorhanden sind.\n" +
                    "\n" +

                    "\"Keine Dateien mit diesem Suffix\" oder \"nur Dateien mit diesem Suffix\": Bestimmt " +
                    "durch die angegebenen Suffixe welche Dateien in die Mediensammlung aufgenommen werden. " +
                    "Mehrere Suffixe können durch Komma getrennt angegeben werden, z.B. 'txt,xml'. Ist die " +
                    "Zeile leer, werden alle Dateien aufgenommen.\n" +
                    "\n" +
                    "\"Keine versteckten Dateien suchen:\" Unabhängig vom Suffix werden versteckte " +
                    "Dateien nicht in die Mediensammlung aufgenommen.\n" +
                    "\n" +
                    "\"Nur Dateien mit Mindestgröße suchen:\" Kleinere Dateien werden nicht in die " +
                    "Mediensammlung aufgenommen.\n" +
                    "\n" +
                    "Mit dem \"Export\"-Button kann die komplette Mediensammlung in eine " +
                    "Textdatei geschrieben werden." +
                    "\n";

    public static final String EXTERN_MEDIA_COLLECTION =
            "Hier können externe Medienordner in der Mediensammlung verwaltet werden: " +
                    "Ordner hinzufügen, die Mediensammlung mit vorhandenen Ordnern " +
                    "manuell aktualisieren, oder vorhandene Ordner aus der " +
                    "Mediensammlung entfernen." + P2LibConst.LINE_SEPARATORx2 +
                    "Wichtig ist hier noch, dass nach dem Hinzufügen oder Ändern " +
                    "einer externen Mediensammlung, diese wieder eingelesen wird. " +
                    "Sonst arbeitet das Programm noch mit den alten Einträgen." + P2LibConst.LINE_SEPARATORx2 +
                    "Externe Medienordner werden nur aktualisiert, " +
                    "wenn es mit dem Button \"gedrehte Pfeile\" " +
                    "angestoßen wird. Dafür müssen diese Ordner (z.B. USB-Festplatte) " +
                    "nicht dauerhaft am Rechner angeschlossen sein." +
                    "\n";

    public static final String INTERN_MEDIA_COLLECTION =
            "Hier können interne Medienordner in der Mediensammlung verwaltet werden: Ordner " +
                    "hinzufügen oder vorhandene Ordner aus der Mediensammlung entfernen. " +
                    "Eine Aktualisierung der Mediensammlung mit ihrem Inhalt erfolgt bei " +
                    "jedem Programmstart. Die Pfade müssen also beim Programmstart erreichbar sein." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Soll die Mediensammlung sofort aktualisiert werden, " +
                    "kann das mit dem Button \"gedrehte Pfeile\" gestartet werden." +
                    "\n";

    public static final String DOWNLOAD_GUI_MEDIA =
            "Hier werden Filme aus der Mediensammlung und den " +
                    "erledigten Abos angezeigt. " +
                    "Mit dem Suchtext kann in der Mediensammlung / erledigten Abos gesucht werden. " +
                    "Ein Doppelklick auf ein Wort im Suchtext stellt es frei." +
                    "\n\n" +
                    "Im Einstellungsdialog kann ausgewählt werden, wie der Suchtext beim Klick auf einen " +
                    "Download gebaut wird und wo (in der Mediensammlung / erledigten Abos) gesucht " +
                    "werden soll." +
                    "\n\n" +
                    "Dort wird auch festgelegt, ob und wie der Suchtext \"geputzt\" " +
                    "wird. Beim Putzen wird der Suchtext auf die wichtigsten Wörter " +
                    "reduziert um das Suchergebnis zu verbessern." +
                    "\n";

    public static final String FILTER_FIELD =
            "Mit den Textfeldern kann nach einem wörtlichen Suchtext (Suchtext muss enthalten sein/" +
                    "oder darf nicht enthalten sein) " +
                    "oder nach " +
                    "regulären Ausdrücken (RegExp) gesucht werden.\n" +
                    "\n" +
                    "Groß- und Kleinschreibung wird bei beiden Arten der Suche nicht unterschieden.\n" +
                    "\n" +

                    "-- Wörtlicher Suchtext muss enthalten sein --\n" +
                    "Ein wörtlicher Suchtext findet alle Dateien bei denen der Suchtext an beliebiger " +
                    "Stelle im durchsuchten Bereich enthalten ist." +
                    "\n" +
                    "\n" +
                    "Wörtlicher Suchtext kann in \" angegeben werden. Dann wird alles gefunden, was im durchsuchten " +
                    "Bereich den Inhalt zwischen den \" enthält. Hier sind auch \",\" und \":\" erlaubt. Der gesamte Suchtext " +
                    "muss aber in \" eingeschlossen werden, z.B. \"das, das und das wird gesucht\". Dann muss der durchsuchte " +
                    "Bereich genau das \"das, das und das wird gesucht\" enthalten (ohne die \")." +
                    "\n" +
                    "Eine Suche mit RegEx, z.B. \"#:.*pass,+.*\" kann damit verkürzt werden: \"pass,\". " +
                    "Das ist kürzer und läuft im Programm auch schneller." +
                    "\n\n" +

                    "Um mehrere Begriffe zu suchen müssen diese durch Komma oder Doppelpunkt " +
                    "getrennt werden. Das Komma verknüpft die Begriffe mit ODER (=> mindestens einer der Begriffe " +
                    "muss vorkommen), der Doppelpunkt mit UND (=> alle Begriffe müssen vorkommen).\n" +
                    "\n" +
                    "\n" +

                    "-- Wörtlicher Suchtext darf nicht enthalten sein --\n" +
                    "Der Suchtext muss mit '!:' (ohne die ' ') beginnen.\n" +
                    "Ein wörtlicher Suchtext schließt alle Dateien aus, bei denen der Suchtext an beliebiger " +
                    "Stelle im durchsuchten Bereich enthalten ist.\n" +
                    "\n" +
                    "Um mehrere Begriffe auszuschließen, müssen diese durch Komma oder Doppelpunkt " +
                    "getrennt werden. Das Komma verknüpft die Begriffe mit ODER (=> mindestens einer der Begriffe " +
                    "muss enthalten sein). Der Doppelpunkt verknüpft die Begriffe mit UND " +
                    "(=> alle Begriffe müssen enthalten sein, damit der Beitrag ein Treffer ist).\n" +
                    "\n" +
                    "\n" +

                    "Suchtext und Suchbegriffe dürfen Leerzeichen enthalten, aber kein Komma und keinen Doppelpunkt " +
                    "(wenn der gesamte Suchtext nicht in \" eingeschlossen ist.)" +
                    "\n" +
                    "\n" +
                    "Beispiele:\n" +
                    "'Tagesschau' findet u.a. 'Tagesschau, 12:00 Uhr', 'ARD Tagesschau Livestream', 'Bei Logo und der Tagesschau'.\n" +
                    "'Sport,Fussball' (Komma-getrennt) findet Filme bei denen 'Sport' oder 'Fussball' " +
                    "oder beides vorkommt, u.a. 'Wintersport im Mumintal' und 'Wie wird man Fussballprofi?'.\n" +
                    "'Sport:Fussball' (Doppelpunkt-getrennt) findet nur Dateien bei denen " +
                    "beides ('Sport' und 'Fussball') vorkommt, z.B. 'Wintersport, Fussball und Formel 1'.\n" +
                    "\n" +
                    "'!:Auto,Motorrad', es werden nur Beiträge gefunden, die weder \"Auto\" noch \"Motorrad\" " +
                    "enthalten.\n" +
                    "'!:Auto:Motorrad', es werden Beiträge gefunden, die \"Auto\" *und* \"Motorrad\" " +
                    "*nicht* enthalten.\n" +

                    "\n" +
                    "-- Reguläre Ausdrücke --\n" +
                    "Ein Suchtext aus regulären Ausdrücken (RegExp) muss mit '#:' (ohne die ' ') beginnen.\n" +
                    "\n" +
                    "Beliebige Zeichen mit in die Suche aufnehmen (als '.+', '.*' oder '.?'') macht einen Unterschied!\n" +
                    "\n" +
                    "Beispiele:\n" +
                    "'#:Burger' oder '#:^burger$' -- beide finden 'Burger' und 'burger' und nichts sonst.\n" +
                    "'#:Burger.+' findet nicht 'Burger', aber alles was nach 'Burger' noch Zeichen " +
                    "enthält (z.B. 'Burger für die Straubing Tigers').\n" +
                    "'#:.+burger' findet alles was auf 'burger' oder 'Burger' " +
                    "endet (z.B. 'Der Hamburger' und '1. Burger').\n" +
                    "\n" +
                    "Mehr zu regulären Ausdrücken:\n" +
                    "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck" +
                    "\n";

    public static final String SEARCH_MEDIA_DIALOG = "" +
            "In der Mediensammlung und den erledigten Abos / der History kann nach Filmen gesucht " +
            "werden. Dazu wird in der Mediensammlung nach dem DATEINAMEN - und/oder - PFAD gesucht. " +
            "In der Liste der erledigten Abos / History wird nach " +
            "dem TITEL - und/oder - dem THEMA des Abo-Films / History-Films gesucht." +
            "\n" +
            "\n" +
            "Der Button (gedrehte Pfeile) stellt den ursprünglichen Suchtext wieder her." +
            "\n" +
            "\n" +
            "-- Besonderheiten --\n" +
            "Beim Durchsuchen der Mediensammlungen muss der Suchtext den eigenen Vorgaben für " +
            "Dateinamen entsprechen. Eine Suche nach 'mein Film' würde den Film 'Das ist mein Film.avi' " +
            "finden, aber nicht 'Das_ist_mein_Film.avi'.\n" +
            "\n" +
            "Bei der Suche mit mehreren UND-Verknüpften Suchbegriffen müssen alle im selben Datenfeld " +
            "vorkommen. Ein Film mit 'Sport' im THEMA " +
            "und 'Fussball' im TITEL wird von 'Sport:Fussball' nicht gefunden." +
            "\n" +
            "Sind die Suchbegriffe dagegen ODER-Verknüpft, z.B.: 'Sport,Fussball' dann wird es gefunden. " +
            "Dann muss ein Suchbegriff entweder im THEMA/PFAD ODER " +
            "im TITEL/DATEINAMEN vorkommen." +
            "\n" +
            "\n" +
            "Ein Doppelklick auf ein Wort im Suchtext stellt dieses frei. So kann man den Suchtext " +
            "schnell auf einen entscheidenden Begriff reduzieren." +
            "\n" +
            "\n" +
            "--- Suchregeln ---" +
            "\n" +
            FILTER_FIELD +
            "\n";

    public static final String PROG_PATHS =
            "Hier können Standardprogramme zum Ansehen und Aufzeichnen der Filme eingetragen werden.\n" +
                    "\n" +
                    "Es müssen mindestens diese Programme installiert sein:\n" +
                    "VLC -- zum Ansehen der Filme\n" +
                    "ffmpeg -- zum Aufzeichnen von Playlisten (URL endet mit 'm3u8')\n" +
                    "\n" +
                    "Wenn die Pfade nicht automatisch erkannt wurden kann man sie auch per Hand auswählen.\n" +
                    "\n" +
                    "Installation unter Linux:\n" +
                    "Am einfachsten über die Paketverwaltung.\n" +
                    "\n" +
                    "Installation unter Windows:\n" +
                    "MTPlayer enthält bereits ffmpeg, es kann aber auch separat installiert werden.\n" +
                    "VLC muss aus dem Internet geladen werden.\n" +
                    "\n" +
                    "Downloadquellen:\n" +
                    "http://www.videolan.org\n" +
                    "http://ffmpeg.org" +
                    "\n";

    public static final String GUI_FILM_FILTER =
            "Die Menüs, Textfelder und Schieberegler erlauben ein detailliertes " +
                    "Durchsuchen und Filtern der vorhandenen Filme.\n" +
                    "\n" +
                    "\n" +
                    "Im Bereich darunter können ganze Such-/Filtereinstellungen als Profile " +
                    "angelegt und verwaltet werden.\n" +
                    "\n" +
                    "\n" +
                    "Die Blacklist (anlegen unter Einstellungen > Blacklist) kann für die " +
                    "Anzeige aktiviert bzw. deaktiviert werden.\n" +
                    "  - Blacklist aus: Alle Filme werden angezeigt.\n" +
                    "  - Blacklist ein: Von der Blacklist erfasste Filme werden nicht angezeigt.\n" +
                    "  - Blacklist invers: Nur von der Blacklist erfasste Filme werden angezeigt.\n" +
                    "\n" +
                    "\n" +
                    "Der Einstellungs-Button (das Zahnrad) öffnet einen Dialog in dem die einzelnen " +
                    "Filter ein- oder ausgeschaltet werden können.\n" +
                    "\n" +
                    "-- Menüs \"Sender\" und \"Thema\" --\n" +
                    "Die Einträge der Filtermenüs \"Sender\" und \"Thema\" werden automatisch aus der " +
                    "Filmliste erstellt.\n" +
                    "\n" +
                    "Im Einstellungs-Dialog kann für \"Thema\" ein Textfeld für freies Suchen " +
                    "zugeschaltet werden.\n" +
                    "\n" +
                    "-- Suchen und Filtern --\n" +
                    "Mit den Textfeldern ([Thema] wenn eingeschaltet, [Thema oder Titel], [Titel], [Irgendwo] " +
                    "und [URL]) kann sehr detailliert gesucht werden. \"Zeitraum\", \"Filmlänge\", \"Sendezeit\" " +
                    "(ein- oder ausschließen), \"anzeigen\" und \"ausschließen\" erlauben noch " +
                    "weitergehende Filterung.\n" +
                    "\n" +
                    FILTER_FIELD + "\n" +
                    "\n" +
                    "-- Besonderheiten --\n" +
                    "[Thema oder Titel] durchsucht THEMA und TITEL der Filmliste. [Irgendwo] sucht " +
                    "außerdem noch in BESCHREIBUNG und DATUM.\n" +
                    "Bei einer Suche nach mehreren Suchbegriffen müssen hier alle Suchbegriffe im selben " +
                    "Datenfeld vorkommen. Ein Film mit 'Sport' in THEMA und 'Fussball' in TITEL wird " +
                    "von 'Sport:Fussball' nicht erfasst.\n" +
                    "\n" +
                    "[URL] sucht in der URL des Films sowie in der URL der Webseite des Films.\n" +
                    "\n" +
                    "-- Filterprofile --\n" +
                    "Die Buttons \"<\" und \">\") blättern durch die letzten Such/Filtereinstellungen.\n" +
                    "\n" +
                    "Darunter kann man die aktuellen Einstellungen als Profil speichern bzw. aus dem " +
                    "gespeicherten Zustand wieder laden. " +
                    "Solange ein geladenes Profil nicht verändert wurde ist sein Name unterstrichen." +
                    "\n";

    public static final String GUI_DOWNLOAD_FILTER =
            "Die Menüs und Schieberegler erlauben ein detailliertes " +
                    "Durchsuchen und Filtern der vorhandenen Downloads.\n" +
                    "\n" +
                    "-- Menüs \"Quelle\", \"Downloadart\", \"Sender\", \"Abo\" und \"Status\" --\n" +
                    "Die Einträge der Filtermenüs werden automatisch aus der " +
                    "Filmliste und Downloadliste erstellt.\n" +
                    "\n" +
                    "-- Suchen und Filtern --\n" +
                    "[Quelle] filtert nach Herkunft, also Downloads von Hand oder durch ein Abo angelegt.\n" +
                    "[Downloadart] sucht nach Downloads die direkt geladen werden können oder Downloads " +
                    "die durch ein externes Programm geladen werden müssen.\n" +
                    "\n" +
                    "[Sender], [Abo], [Status] sucht Downloads mit dem Sender, " +
                    "aus dem Abo, oder mit dem Status (nicht/gestartet, fertig).\n" +
                    "\n" +
                    "Mit den Schiebereglern [gleichzeitige Downloads] und [max. Bandbreite] " +
                    "kann die Anzahl der gleichzeitigen Downloads die geladen werden, festgelegt werden. Die vorgegebene " +
                    "maximale Bandbreite gilt pro Download.\n" +
                    "\n";

    public static final String BLACKLIST_WHITELIST =
            "Die Funktion \"Blacklist\" blendet alle Filme aus, die den Angaben in mindestens einer Zeile " +
                    "in der Tabelle entsprechen." +
                    "\n\n" +
                    "Bei der \"Whitelist\" ist es umgekehrt, es werden nur Filme angezeigt, die den " +
                    "Angaben in mindestens einer Zeile entsprechen." +
                    "\n\n" +
                    "Beim Umschalten zwischen Blacklist und Whitelist wird also die Auswahl der angezeigten Filme invertiert.\n" +
                    "\n" +
                    FILTER_FIELD + "\n" +
                    "\n" +

                    "-- Besonderheiten --\n" +
                    "Wenn bei \"Thema\" der Schalter \"Exakt\" eingeschaltet ist, darf der Suchtext " +
                    "nicht \"an beliebiger Stelle darin enthalten\" sein, sondern muss das gesamte " +
                    "Feld darstellen.\n" +
                    "Beispiele:\n" +
                    "\"Exakt\" eingeschaltet: 'Dokumentation' erfasst nur 'Dokumentation' oder " +
                    "'dokumentation', nichts sonst.\n" +
                    "\"Exakt\" ausgeschaltet: 'Dokumentation' erfasst u.a. 'Dokumentationen und Reportagen', " +
                    "'Reportage & Dokumentation', 'Geschichtsdokumentationen'.\n" +
                    "\n" +
                    "[Thema-Titel] durchsucht in der Filmliste THEMA und TITEL.\n" +
                    "Bei einer Suche nach mehreren Suchbegriffen müssen hier alle Suchbegriffe im selben Bereich " +
                    "vorkommen. Ein Film mit 'Sport' in THEMA und 'Fussball' in TITEL wird von " +
                    "'Sport:Fussball' nicht erfasst." +
                    "\n";

    public static final String ABO_SEARCH =
            "Ein Abo findet einen Download, wenn die Filtereinstellungen zum Film passen. Im \"Zielpfad\" " +
                    "wird der Download dann gespeichert. Ist eine \"Startzeit\" vorgegeben, startet der " +
                    "Download zu der vorgegebenen Zeit. Sind mehre Downloadsets (Download-Einstellungen) " +
                    "im Programm angelegt, kann man auch auswählen, mit welcher die Downloads laufen sollen.\n" +
                    "\n" +
                    FILTER_FIELD + "\n" +
                    "\n" +

                    "-- Besonderheiten --\n" +
                    "Wenn bei \"Thema\" der Schalter \"Exakt\" eingeschaltet ist, darf der Suchtext " +
                    "nicht \"an beliebiger Stelle darin enthalten\" sein, sondern muss das gesamte " +
                    "Feld darstellen.\n" +
                    "Beispiele:\n" +
                    "\"Exakt\" eingeschaltet: 'Dokumentation' erfasst nur 'Dokumentation' oder " +
                    "'dokumentation', nichts sonst.\n" +
                    "\"Exakt\" ausgeschaltet: 'Dokumentation' erfasst u.a. 'Dokumentationen und Reportagen', " +
                    "'Reportage & Dokumentation', 'Geschichtsdokumentationen'.\n" +
                    "\n" +
                    "[Thema-Titel] durchsucht in der Filmliste THEMA und TITEL.\n" +
                    "Bei einer Suche nach mehreren Suchbegriffen müssen hier alle Suchbegriffe im selben Bereich " +
                    "vorkommen. Ein Film mit 'Sport' in THEMA und 'Fussball' in TITEL wird von " +
                    "'Sport:Fussball' nicht erfasst." +
                    "\n";

    public static final String CONFIG_GEO =
            "Nicht alle Filme lassen sich aus allen Ländern abrufen (Geoblocking). Man kann hier " +
                    "seinen Standort angeben, wenn geblockte Filme in der Liste markiert werden sollen " +
                    "oder um sie per Blacklist auszuschließen.\n" +
                    "\n" +
                    "Manche Sender überprüfen die abfragende URL, so dass man trotz zum Senderland " +
                    "passender Einstellung nicht auf geblockte Downloads zugreifen kann.\n" +
                    "\n" +
                    "Ein Downloadversuch geblockter Sendungen bricht sofort ab und der Download " +
                    "wird als 'fehlerhaft' angezeigt.\n" +
                    "\n" +
                    "MTPlayer kennt aber nicht alle Muster für geogeblockte Sendungen. Ob ein fehlerhafter " +
                    "Download auf Geoblocking zurückzuführen ist zeigt sich beim Klick auf den Link zur " +
                    "Sendung ('zur Website' ganz unten in der Ansicht 'Filme'). Wenn die Sendung auch auf der " +
                    "Website des Senders nicht abgespielt werden kann liegt fast immer Geoblocking vor." +
                    "\n";

    public static final String CONFIG_STYLE =
            "Die Schriftgröße sollte sich automatisch an die vorgegebene Größe im " +
                    "Betriebssystem einstellen. Sie kann hier eingestellt werden, wenn " +
                    "die Automatik nicht funktioniert oder eine andere Größe gewünscht wird.\n" +
                    "\n" +
                    "Damit die Änderung wirksam wird ist evtl. ein Neustart des Programms erforderlich." +
                    "\n";

    public static final String GUI_FILMS_EDIT_FILTER =
            "Hier können Filter aktiviert und deaktiviert werden." +
                    "\n" +
                    "\n" +
                    "Deaktivierte Filter werden beim Suchen der Filme nicht berücksichtigt, " +
                    "daher ist eine Suche mit weniger Filtern schneller." +
                    "\n" +
                    "\n" +

                    "Suchbeginn verzögern: Hier kann eine Zeit eingestellt werden, die den Start der " +
                    "Suche verzögert." +
                    "\n" +
                    "\n" +
                    "\"Return\": Diese Einstellung startet die Suche in den Textfeldern erst nach Eingabe " +
                    "der Return-Taste." +
                    "\n" +
                    "\n" +
                    "In den Textfeldern wird die Suche immer sofort nach Eingabe der Return-Taste " +
                    "gestartet. Mit der Einstellung \"Return\" aber ausschließlich. " +
                    "Bei den anderen Suchfeldern (Zeit, Datum, ..) startet die " +
                    "Suche immer nach der eingestellten Wartezeit." +
                    "\n" +
                    "\n" +
                    "Ich kann eine Suche also starten wenn ich ein Suchfeld " +
                    "(egal welches) ändere und die Wartezeit abwarte. " +
                    "In einem Textfeld (egal welchem) kann ich zusätzlich die Suche starten " +
                    "wenn ich \"Return\" tippe." +
                    "\n";

    public static final String SETDATA_PREFIX =
            "Wenn die URL eines Films mit <Präfix> beginnt bzw. mit <Suffix> " +
                    "endet wird der Film von MTPlayer selbst geladen und nicht mit einem " +
                    "Hilfsprogramm gespeichert.\n" +
                    "\n" +
                    "Mehrere Einträge sind möglich, wenn sie durch Kommas " +
                    "getrennt sind (z.B. 'mp4,mp3,m4v,m4a')." +
                    "\n";

    public static final String SETDATA_RES =
            "Nicht jede Auflösung wird von jedem Sender angeboten. Wenn die gewünschte " +
                    "Auflösung nicht verfügbar ist wird automatisch die hohe Auflösung heruntergeladen.\n" +
                    "\n" +
                    "Die Auflösung gilt nur für manuell gestartete Downloads und zum " +
                    "Abspielen von Filmen. Für Abos wird die im Abo ausgewählte Auflösung verwendet." +
                    "\n";

    public static final String ABO_RES =
            "Nicht jede Auflösung wird von jedem Sender angeboten. " +
                    "Wenn die gewünschte Auflösung nicht verfügbar ist wird " +
                    "automatisch die hohe Auflösung heruntergeladen." +
                    "\n";

    public static final String ABO_SUBDIR =
            "Downloads aus Abos werden in einem Abo-eigenen Unterordner gespeichert, " +
                    "wenn \"bei Abos Unterordner anlegen\" im ausgewählten Programmset eingeschaltet ist. " +
                    "Diese Einstellung wird überschrieben, wenn hier ein eigener Zielpfad angegeben ist. " +
                    "Der Download wird dann immer in einem Unterordner mit dem im Zielpfad " +
                    "angegebenen Namen gespeichert." + "\n" +
                    "\n" +
                    "Mit der CheckBos links kann zwischen der Vorgabe des Programmsets " +
                    "und dem hier eingetragenen Pfad umgeschaltet werden.\n" +
                    "\n" +
                    "Beim Namen sind diese Parameter möglich:\n" +
                    "\n" +
                    "%D Sendedatum des Films, wenn leer von 'heute'\n" +
                    "%d Sendezeit des Films, wenn leer von 'jetzt'\n" +
                    "%H 'heute', aktuelles Datum im Format JJJJMMTT, z.B. '20090815' am 15.08.2009\n" +
                    "%h 'jetzt', aktuelle Uhrzeit im Format HHMMss, z.B. '152059' um 15:20:59 Uhr\n" +
                    "\n" +
                    "%1 Tag, vom Sendedatum des Films, wenn leer von 'heute'\n" +
                    "%2 Monat, ebenso\n" +
                    "%3 Jahr, ebenso\n" +
                    "\n" +
                    "%4 Stunde, von der Sendezeit des Films, wenn leer von 'jetzt'\n" +
                    "%5 Minute, ebenso\n" +
                    "%6 Sekunde, ebenso\n" +
                    "\n" +
                    "%s Sender des Films\n" +
                    "%T Titel des Films\n" +
                    "%t Thema des Films\n" +
                    "\n" +
                    "%q Qualität des Films ('HD', 'H', 'L')\n" +
                    "\n" +
                    "Beispiel:\n" +
                    "Am 10.05.2021 liefert '%H__%t__%T' z.B. '20210510__Natur__Wildes Shetland'." +
                    "\n";

    public static final String SETDATA_ABO_SUBDIR =
            "Downloads aus Abos werden in einem Abo-eigenen Unterordner gespeichert, " +
                    "wenn hier \"bei Abos Unterordner anlegen\" eingeschaltet ist. " +
                    "Der Name des Unterordners kann mit dem Auswahlmenü festgelegt werden.\n" +
                    "\n" +
                    "Diese Einstellung wird überschrieben, wenn im Abo ein eigener " +
                    "Abozielpfad angegeben ist. Der Download wird dann immer in einem " +
                    "Unterordner mit dem im Abozielpfad angegebenen Namen gespeichert." +
                    "\n";

    public static final String ABO_START_TIME =
            "Die Startzeit legt fest, ab wann ein Download aus diesem Abo gestartet wird.\n\n" +
                    "Der Download muss ganz normal wie andere Downloads aus Abos behandelt, also auch " +
                    "gestartet werden. Das Programm beginnt aber erst ab der vorgegebenen Startzeit " +
                    "mit dem Laden des Downloads." +
                    "\n";

    public static final String SETDATA_RESET_COLOR =
            "Wenn das Set in der Ansicht \"Filme\" als Button gestartet werden " +
                    "kann (\"Button\" im Set ist eingeschaltet), kann hier die " +
                    "Schriftfarbe des Buttons festgelegt werden." +
                    "\n";

    public static final String DOWNLOAD_REPLACELIST =
            "Die Tabelle wird von oben nach unten abgearbeitet. Es ist also möglich, " +
                    "dass eine Ersetzung durch eine weitere ganz oder teilweise " +
                    "rückgängig gemacht wird!" +
                    "\n";

    public static final String FILMTITEL_NOT_LOAD =
            "Beim Laden der Filmliste werden Filme die den Text im Titel enthalten, " +
                    "nicht geladen." +
                    "\n";

    public static final String DOWNLOAD_ONLY_ASCII =
            "Es werden alle Zeichen über ASCII 127 ersetzt. Umlaute werden aufgelöst (z.B. 'ö' -> 'oe').\n" +
                    "\n" +
                    "Wenn die Ersetzungstabelle aktiv ist wird sie vorher abgearbeitet." +
                    "\n";

    public static final String DOWNLOAD_ONE_SERVER =
            "Es sind maximal 2 gleichzeitige Downloads pro Server möglich. Hier kann " +
                    "auf nur 1 Download begrenzt werden, was manchmal bei " +
                    "Downloadproblemen hilft." +
                    "\n";

    public static final String DOWNLOAD_SSL_ALWAYS_TRUE =
            "Bei Downloads mit \"https-URL\" wird die Verbindung über SSL " +
                    "aufgebaut. Wenn SSL-Zertifikate auf dem Rechner fehlen oder das Server-Zertifikat fehlerhaft ist, kommt es " +
                    "zu Download-Fehlern. Der Download bricht mit einer Fehlermeldung ab. " +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Die Überprüfung der Zertifikate kann mit dieser Funktion abgeschaltet werden." +
                    "\n";
    public static final String DOWNLOAD_BANDWIDTH =
            "Mit den Schiebereglern [gleichzeitige Downloads] und [max. Bandbreite] kann die Anzahl " +
                    "der gleichzeitigen Downloads die geladen werden, festgelegt werden. " +
                    "Die vorgegebene maximale Bandbreite gilt pro Download.\n" +
                    "\n";

    public static final String DOWNLOAD_FINISHED =
            "Wenn ein Download erfolgreich beendet ist, wird mit einem Fenster informiert." +
                    "\n";

    public static final String DOWNLOAD_ERROR =
            "Wenn ein Download mit einem Fehler endet, wird mit einem Fenster informiert." +
                    "\n";

    public static final String DOWNLOAD_STOP =
            "Wenn ein bereits teilweise geladener Download abgebrochen wird, " +
                    "können die teilweise geladenen Dateien gleich gelöscht werden." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Hier kann ausgewählt werden, was gemacht werden soll:\n" +
                    "* Jedes mal vorher fragen\n" +
                    "* Immer den Download löschen oder Abbrechen und die Datei löschen\n" +
                    "* Nur den Download löschen oder abbrechen." +
                    "\n";

    public static final String DOWNLOAD_CONTINUE =
            "Wenn ein bereits teilweise geladener Download neu startet, " +
                    "kann er weitergeführt oder von Anfang an, neu gestartet werden." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Hier kann ausgewählt werden, was gemacht werden soll:\n" +
                    "* Jedes mal vorher fragen\n" +
                    "* Sofort weiterführen\n" +
                    "* Immer von Anfang an neu starten." +
                    "\n";

    public static final String DOWNLOAD_ADD_AT_TIME =
            "Downloads können mit einer Startzeit gestartet werden.\n\n" +
                    "\"Nur ausgewählte Downloads starten\" zeigt die markierten Downloads und\n" +
                    "\"Alle Downloads starten\" zeigt alle Downloads die gestartet werden können.\n\n" +
                    "Die Auswahl wird nur angezeigt, wenn auch Downloads markiert wurden." +
                    "\n";

    public static final String BLACKLIST_GEO =
            "Einschalten, um geogeblockte Filme von Anzeige und Abo-Download auszuschließen.\n" +
                    "Es wird der in den Einstellungen unter \"Allgemein\" angegebene Standort verwendet." +
                    "\n";

    public static final String BLACKLIST_DOUBLE =
            "Einschalten, um doppelte Filme von Anzeige und Abo-Download auszuschließen." +
                    "\n";

    public static final String BLACKLIST_SIZE =
            "Filme ohne Längenangabe werden immer angezeigt und ggf. von Abos als Download angelegt.\n" +
                    "\n" +
                    "Kurze Filme sind oft nur Trailer.\n" +
                    "\n" +
                    "Wenn die Filmliste hiermit verkürzt wird kann das Arbeiten mit den Filmen schneller werden." +
                    "\n";

    public static final String BLACKLIST_DAYS =
            "Filme ohne Datum werden immer angezeigt und ggf. von Abos als Download angelegt.\n" +
                    "\n" +
                    "Wenn die Filmliste hiermit verkürzt wird kann das Arbeiten mit den Filmen schneller werden." +
                    "\n";

    public static final String BLACKLIST_COUNT =
            "Beim \"Treffer zählen\" wird jeder Film gegen alle Filter geprüft und jeder Treffer " +
                    "wird gezählt.\n" +
                    "Anders beim Filtern der Filmliste: " +
                    "Dort wird nach dem ersten Treffer die weitere Suche abgebrochen. Das ist " +
                    "dadurch also etwas schneller." +
                    "\n\n" +
                    "\"Putzen\" entfernt doppelte und leere Blacklist-Einträge. Das wird auch automatisch " +
                    "beim Programmstart gemacht.";

    public static final String BLACKLIST_MOVE =
            "Beim \"Kopieren\" oder \"Verschieben\" werden die markierten Filter in den anderen Filmfilter " +
                    "\"Filme laden\" oder \"Blacklist\" kopiert oder verschoben.";

    public static final String BLACKLIST_FUTURE =
            "Filme mit Datum in der Zukunft sind meist nur Trailer." +
                    "\n";

    public static final String BLACKLIST_ABO =
            "Einschalten, wenn auch die von Abos gefundenen Downloads mit der " +
                    "Blacklist/Whitelist gefiltert werden sollen." +
                    "\n";

    public static final String LOAD_ONLY_FILMS =
            "\"Nur Filme der letzten Tage laden:\" Die Filmliste enthält nur Filme aus diesem Zeitraum. " +
                    "Filme ohne Datum sind immer enthalten.\n" +
                    "\n" +
                    "\"Nur Filme mit Mindestlänge laden:\" Die Filmliste enthält nur Filme von " +
                    "mindestens dieser Dauer. Filme ohne Längenangabe sind immer enthalten.\n" +
                    "\n" +
                    "Bei 'alles laden' sind alle Filme enthalten.\n" +
                    "\n" +
                    "Das Filtern der Filmliste kann bei älteren Rechnern mit wenig Speicher " +
                    "hilfreich sein: Bei 'maximal 250 Tage' oder 'mindestens 5 Minuten' ist die " +
                    "Filmliste nur etwa halb so groß (~ 300.000 Filme).\n" +
                    "\n" +
                    "Auswirkung hat das Filtern erst nach dem " +
                    "Neuladen der Filmliste." +
                    "\n";

    public static final String LOAD_FILMLIST_SENDER =
            "Filme der markierten Sender werden aus der Filmliste ausgeschlossen.\n" +
                    "\n" +
                    "Wirksam erst nach dem Neuladen der kompletten Filmliste." +
                    "\n";

    public static final String LOAD_FILMLIST_PROGRAMSTART =
            "Die Filmliste wird beim Programmstart automatisch geladen, " +
                    "wenn sie älter als 3 Stunden ist. Sie kann auch über den " +
                    "Button \"Filmliste\" aktualisiert werden.\n" +
                    "\n" +
                    "Zum Update werden dann nur noch Differenzlisten geladen " +
                    "(diese enthalten nur neu hinzugekommene Filme)." +
                    "\n";

    public static final String LOAD_FILMLIST_ONLY_MARK_DOUBLE =
            "In der Filmliste sind gleiche Filme bei verschiedenen Sendern enthalten. Doppelte Filme " +
                    "können markiert werden. Es gibt einen Filmfilter um sie nicht anzuzeigen. Sie können auch mit " +
                    "der Blacklist ausgeschlossen werden. Es ist auch " +
                    "möglich, sie bereits beim Laden der Filmliste auszuschließen." +
                    "\n\n" +
                    "Hier kann vorgegeben werden, ob sie beim Laden einer Filmliste entfernt werden. Da die Filmliste " +
                    "inzwischen sehr lang ist und über 150.000 Filme doppelt enthalten sind, wäre das eine " +
                    "sehr gute Entscheidung. In den Einstellungen kann das auch wieder geändert werden.";

    public static final String LOAD_FILMLIST_MARK_DOUBLE =
            "In der Filmliste sind gleiche Filme bei verschiedenen Sendern enthalten. Doppelte Filme " +
                    "können markiert werden. Es gibt einen Filmfilter um sie nicht anzuzeigen. Sie können auch mit " +
                    "der Blacklist ausgeschlossen werden. Es ist auch " +
                    "möglich, sie bereits beim Laden der Filmliste auszuschließen." +
                    "\n\n" +
                    "Hier kann vorgegeben werden, welche Sender bevorzugt werden sollen. D.h. Filme dieser " +
                    "Sender werden zuerst genommen. Taucht der gleiche Film bei einem " +
                    "anderen Sendern nochmal auf, wird er dann dort " +
                    "als \"Doppelt\" markiert." +
                    "\n\n" +
                    "Werden doppelte Filme beim Laden der Filmliste ausgeschlossen, ist " +
                    "bei Abos evtl. zu beachten, dass der Film dann möglicherweise von einem " +
                    "anderen Sender kommt.";

    public static final String LOAD_FILMLIST_IMMEDIATELY =
            "Wenn es eine neue Filmliste gibt, wird sie automatisch sofort geladen. " +
                    "Die Filmliste wird also sofort aktualisiert wenn es ein Update gibt." +
                    "\n";

    public static final String DIAKRITISCHE_ZEICHEN =
            "\"Diakritische Zeichen ändern\" meint, dass bestimmte Zeichen in den " +
                    "Filmfeldern: \"Titel, Thema und Beschreibung\" " +
                    "angepasst werden. Aus z.B.\n" +
                    "\"äöü ń ǹ ň ñ ṅ ņ ṇ ṋ ç č c\" wird dann\n" +
                    "\"äöü n n n n n n n n c c c\".\n" +
                    "\n" +
                    "Das Programm arbeitet dann mit der angepassten " +
                    "Filmliste. Beim Suchen nach Filmen, Downloads und Abos werden die *angepassten " +
                    "Zeichen* in Titel, Thema und Beschreibung verwendet. Dann werden z.B. " +
                    "\"Dvořak\", \"Noël\" und \"Niño\" nicht mehr gefunden, aber stattdessen " +
                    "\"Dvorak\", \"Noel\" und \"Nino\".\n" +
                    "\n" +
                    "Beim Suchen und Anlegen von Abos, muss also klar sein, ob die Funktion ein/ausgeschaltet ist, " +
                    "ob man die Funktion nutzt oder nicht.\n" +
                    "\n" +
                    "Das Ändern wird sofort nach Beenden des Dialogs gemacht. Sollen Diakritische Zeichen " +
                    "aber wieder eingeschaltet (also angezeigt) werden, muss dazu eine neue Filmliste geladen werden!" +
                    "\n";

    public static final String SEARCH_ABOS_IMMEDIATELY =
            "Nach dem Neuladen einer Filmliste wird automatisch nach neuen " +
                    "Downloads aus Abos gesucht. Wenn dies ausgeschaltet ist, muss man die " +
                    "Suche manuell anstoßen (in der Ansicht \"Downloads\" " +
                    "auf \"Downloads aktualisieren\" klicken)." +
                    "\n";

    public static final String SMALL_BUTTON =
            "In den Tabellen der Ansichten \"Filme\", \"Downloads\" und \"Abos\" können " +
                    "kleinere Buttons gewählt werden, um die Zeilenhöhe zu verringern." +
                    "\n";

    public static final String TRAY =
            "Im System Tray wird für das Programm ein Symbol angezeigt. " +
                    "Damit kann das Programm auf dem Desktop ausgeblendet werden." +
                    "\n";

    public static final String PROGRAM_ICON =
            "Das verwendete Programmicon kann damit geändert werden. " +
                    "Damit kann ein eigens Bild dafür verwendet werden." +
                    "\n";

    public static final String TRAY_OWN_ICON =
            "Im System Tray wird für das Programm ein Symbol angezeigt. " +
                    "Damit kann ein eigens Bild dafür verwendet werden." +
                    "\n";

    public static final String TIP_OF_DAY =
            "Beim Programmstart wird (einmal täglich) ein Tip zur Verwendung " +
                    "des Programms angezeigt. Das passiert so oft, bis alle Tips " +
                    "einmal angezeigt wurden." +
                    "\n";

    public static final String DARK_THEME =
            "Das Programm wird damit mit einer dunklen Programmoberfläche angezeigt. " +
                    "Damit alle Elemente der Programmoberfläche geändert werden, kann ein " +
                    "Programmneustart notwendig sein." +
                    "\n";

    public static final String SHORTCUT =
            "Zum Ändern eines Tastenkürzels seinen \"Ändern\"-Button klicken und dann " +
                    "die gewünschten neuen Tasten drücken.\n" +
                    "\n" +
                    "Der \"Zurücksetzen\"-Button stellt den Originalzustand wieder her.\n" +
                    "\n" +
                    "Damit die Änderungen wirksam werden muss das Programm neu gestartet werden." +
                    "\n";

    public static final String USER_AGENT =
            "Hier kann ein User Agent angegeben werden, der bei Downloads als Absender " +
                    "verwendet wird. Bleibt das Feld leer, wird kein User Agent verwendet.\n" +
                    "\n" +
                    "Solange alles funktioniert, kann das Feld leer bleiben. Ansonsten wäre " +
                    "das z.B. eine Möglichkeit: 'Mozilla/5.0'.\n" +
                    "\n" +
                    "Es sind nur ASCII-Zeichen erlaubt und die Textlänge ist begrenzt auf 100 Zeichen." +
                    "\n";

    public static final String START_DOWNLOADS_FROM_ABOS_IMMEDIATELY =
            "Downloads die aus Abos neu angelegt wurden, starten sofort. " +
                    "Wenn dies ausgeschaltet ist muss man sie manuell starten." +
                    "\n";

    public static final String ONLY_ONE_INSTANCE =
            "Werden mehre Instanzen mit dem gleichen Konfig-Ordner geöffnet, überschreiben sie " +
                    "ihre Einstellungen. Die Einstellungen der Instanz die zuletzt geschlossen wird, speichert dann die " +
                    "Einstellungen." +
                    "\n\n" +
                    "Um das zu erkennen, wird im Konfig-Ordner eine Lock-Datei erstellt. Ist diese bei einem " +
                    "weiteren Programmstart bereits vorhanden, wird eine Meldung ausgegeben, dass das Programm " +
                    "bereits läuft." +
                    "\n\n" +
                    "(Es ist möglich, das Programm mit unterschiedlichen Konfig-Ordnern zu starten -> Anleitung. Dann kann man mehrere " +
                    "Instanzen parallel mit unterschiedlichen Einstellungen betreiben.)" +
                    "\n";

    public static final String LOGFILE =
            "Im Logfile wird der Programmverlauf aufgezeichnet. Das kann hilfreich sein " +
                    "wenn das Programm nicht wie erwartet funktioniert.\n" +
                    "\n" +
                    "Der Standardordner für das Log ist 'Log' im Konfigurations-Ordner des " +
                    "Programms. Der Ort kann geändert werden.\n" +
                    "\n" +
                    "Ein geänderter Pfad zum Logfile wird erst nach einem Neustart des " +
                    "Programms genutzt; mit dem Button \"Pfad zum Logfile jetzt schon verwenden\" wird " +
                    "sofort ins neue Log geschrieben." +
                    "\n";

    public static final String MV_PATH =
            "Der Konfig-Ordner von MediathekView muss hier eingetragen werden. Normalerweise " +
                    "ist er im Benutzerverzeichnis und heist \".mediathek3\"" +
                    "\n";

    public static final String MV_SEARCH =
            "Hier wird versucht, im MediathekView Konfig-Ordner die Einstellungen zu suchen. Die " +
                    "gefundenen Einstellungen werden damit aber noch nicht importiert." +
                    "\n";
    public static final String MV_IMPORT =
            "Hier können die gefundenen Einstellungen im Programm importiert werden. Es wird aber kontrolliert, " +
                    "ob es die gleichen Abos/Blacks bereits gibt. Es werden also nur neue Abos/Blacks importiert. " +
                    "Drückt man den Button mehrfach, werden nicht ein weiteres mal, die Abos/Blacks importiert." +
                    "\n";

    public static final String FILEMANAGER =
            "In der Ansicht \"Downloads\" kann man über das Kontextmenü den Downloadordner " +
                    "(Zielordner) des jeweiligen Downloads öffnen. Normalerweise wird dafür der " +
                    "Dateimanager des Betriebssystems gefunden und geöffnet. Klappt das nicht, kann " +
                    "hier ein Programm dafür angegeben werden." +
                    "\n";

    public static final String VIDEOPLAYER =
            "In der Ansicht \"Downloads\" kann man über das Kontextmenü den gespeicherten " +
                    "Film in einem Videoplayer öffnen. Normalerweise wird der Videoplayer des " +
                    "Betriebssystems gefunden und geöffnet. Klappt das nicht, kann hier ein " +
                    "Programm dafür angegeben werden." +
                    "\n";

    public static final String WEBBROWSER =
            "Wenn das Programm versucht, einen Link zu öffnen (z.B. \"Anleitung im Web\" im " +
                    "Programm-Menü unter \"Hilfe\") und der Standardbrowser nicht startet, " +
                    "kann damit ein Programm (Firefox, Chromium, …) ausgewählt und fest " +
                    "zugeordnet werden." +
                    "\n";

    public static final String RESET_DIALOG =
            "-- Nichts ändern --\n" +
                    "Der Dialog wird ohne eine Änderung geschlossen.\n" +
                    "\n" +
                    "-- Einstellungen zum Abspielen und Aufzeichnen zurücksetzen --\n" +
                    "ALLE SETS WERDEN GELÖSCHT! Auch die selbst neu angelegten.\n" +
                    "Anschließend werden die aktuellen Standardsets eingerichtet. Es kann dann direkt " +
                    "damit weitergearbeitet werden.\n" +
                    "Abos und Blacklist bleiben erhalten.\n" +
                    "\n" +
                    "Bei Problemen sollte dies zuerst versucht werden, vor einem kompletten " +
                    "Zurücksetzen des Programms.\n" +
                    "\n" +
                    "-- Alle Einstellungen zurücksetzen --\n" +
                    "ALLE EINSTELLUNGEN WERDEN GELÖSCHT! Das Programm wird in den " +
                    "Ursprungszustand zurückgesetzt.\n" +
                    "Es beendet sich und muss neu gestartet werden.\n" +
                    "Der neue Start beginnt mit dem Einrichtungsdialog." +
                    "\n";

    public static final String EDIT_DOWNLOAD_WITH_PROG =
            "Der Programmaufruf wird von MTPlayer in die einzelnen Teile " +
                    "(Programmschalter) zerlegt. Die einzelnen Programmschalter " +
                    "werden durch \"<>\" getrennt angegeben. Dadurch ist es möglich, " +
                    "auch Pfade mit Leerzeichen zu verwenden.\n" +
                    "\n" +
                    "Bei ÄNDERUNGEN des Programmaufrufs in diesem Dialog, muss diese Form\n" +
                    "verwendet werden.\n" +
                    "\n" +
                    "der Aufruf:\n" +
                    "PFAD/ffmpeg -i URL -c copy -bsf:a aac_adtstoasc ZIELPFAD/DATEI.mp4\n" +
                    "\n" +
                    "wird so angegeben:\n" +
                    "Pfad/ffmpeg<>-i<>URL<>-c<>copy<>-bsf:a<>aac_adtstoasc<>ZIELPFAD/DATEI.mp4\n" +
                    "\n" +
                    "Der Programmaufruf kann auch so (ohne <>) angegben werden:\n" +
                    "PFAD/ffmpeg -i URL -c copy -bsf:a aac_adtstoasc ZIELPFAD/DATEI.mp4\n" +
                    "\n" +
                    "Das funktioniert, kann dann aber bei Leerzeichen im Pfad zu Problemen führen.\n" +
                    "\n";

    public static final String CONFIG_SHUT_DOWN_CALL =
            "Wird das Programm bei noch laufenden Downloads beendet, erscheint der \"Auf Downloads warten\" Dialog. " +
                    "Hier kann dann ausgewählt werden:" +
                    "\n\n" +
                    "-> Nicht beenden\n" +
                    "-> Beenden\n" +
                    "-> Warten" +
                    "\n\n" +
                    "Wird \"Warten\" ausgewählt, beendet sich das Programm erst wenn alle Downloads erledigt sind. " +
                    "Nach dem Warten ist es dann möglich, einen Systemaufruf abzusetzen. " +
                    "Gedacht ist das, um nach der Wartezeit das Programm zu beenden und " +
                    "den Rechner herunterzufahren. Für Linux und Windows " +
                    "bringt das Programm die Standard-Befehle dafür mit." +
                    "\n\n" +
                    "Diese können aber auch angepasst werden. " +
                    "So ist es z.B. auch möglich, einen ganz anderen Befehl oder ein eigenes Skript " +
                    "nach dem Warten auf die Downloads und dem Programmende, auszuführen." +
                    "\n";

    public static final String DOWNLOAD_CANCEL =
            "Wenn ein Download abgebrochen oder gelöscht wird, " +
                    "können zusätzlich auch noch bereits geladene oder " +
                    "teilweise geladene Filmdateien " +
                    "mit gelöscht werden." +
                    "\n\n" +
                    "Wird \"Nicht mehr fragen\" angeklickt, wird die Einstellung in Zukunft " +
                    "immer ausgeführt, der Dialog erscheint dann nicht mehr. Diese Einstellung kann aber " +
                    "in den Programmeinstellungen->Downloads auch wieder geändert werden." +
                    "\n";

    public static final String DOWNLOAD_ONLY_CANCEL =
            "Hier kann ein Download abgebrochen oder gelöscht werden. " +
                    "\n\n" +
                    "Wird \"Nicht mehr fragen\" angeklickt, wird der Download in Zukunft " +
                    "immer abgebrochen oder gelöscht. Der Dialog erscheint dann nicht mehr. Diese Einstellung kann aber " +
                    "in den Programmeinstellungen->Downloads auch wieder geändert werden." +
                    "\n";

    public static final String ABO_DELETE_CONFIG =
            "Soll ein Abo gelöscht werden, kann hier ausgewählt werden, was gemacht werden soll:" +
                    "\n\n" +
                    "* Jedes mal vorher fragen\n" +
                    "* Abo sofort löschen ohne zu fragen." +
                    "\n";
    public static final String ABO_DELETE_DIALOG =
            "Hier kann ein Abo gelöscht werden. " +
                    "\n\n" +
                    "Wird \"Nicht mehr fragen\" angeklickt, wird das Abo in Zukunft " +
                    "immer gelöscht. Der Dialog erscheint dann nicht mehr. Diese Einstellung kann aber " +
                    "in den Programmeinstellungen->Abo auch wieder geändert werden." +
                    "\n";
}
