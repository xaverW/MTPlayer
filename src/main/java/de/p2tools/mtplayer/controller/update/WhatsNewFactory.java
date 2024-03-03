/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.dialogs.WhatsNewDialog;
import de.p2tools.p2lib.dialogs.WhatsNewInfo;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WhatsNewFactory {

    static final LocalDate lastWhatsNewDate = LocalDate.of(2024, 3, 3); // erhöht sich, wenn whatsNew geändert wird

    private WhatsNewFactory() {
    }

    public static void checkUpdate() {
        if (!showWhatsNew(false)) {
            // sonst ist's ja schon eine neue Version
            checkProgUpdate();
        }
    }

    public static void setWhatsNewDate() {
        ProgConfig.SYSTEM_WHATS_NEW_DATE_LAST_SHOWN.setValue(P2LDateFactory.toStringR(lastWhatsNewDate));
    }

    public static boolean showWhatsNew(boolean showAlways) {
        // zeigt die Infos "whatsNew" an, wenn "showAlways" oder wenn die letzte Anzeige vor "whatsNewDate"
        boolean ret = false;
        try {
            ArrayList<WhatsNewInfo> list = new ArrayList<>();
            addWhatsNew(list, showAlways);
            if (list.isEmpty()) {
                // dann gibts nix
                ret = false;

            } else {
                Platform.runLater(() -> new WhatsNewDialog(ProgData.getInstance().primaryStage, ProgConst.PROGRAM_NAME,
                        ProgConst.URL_WEBSITE, ProgConfig.SYSTEM_PROG_OPEN_URL,
                        ProgConfig.SYSTEM_DARK_THEME.getValue(), list).make());
                ret = true;
            }
        } catch (Exception ignore) {
            ret = false;
        }

        setWhatsNewDate();
        return ret;
    }

    private static void addWhatsNew(ArrayList<WhatsNewInfo> list, boolean showAlways) {
        final LocalDate lastShown = P2LDateFactory.fromStringR(ProgConfig.SYSTEM_WHATS_NEW_DATE_LAST_SHOWN.getValueSafe());

        WhatsNewInfo whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_01.png",

                "Doppelte Filme",
                "In den Einstellungen kann zum Suchen von doppelten Filmen jetzt die Suchreihenfolge " +
                        "vorgegeben werden. Es ist jetzt auch möglich, dass doppelte Filme beim Laden der Filmliste " +
                        "gleich ausgeschlossen werden. Es ist jetzt auch möglich, doppelte Filme mit der Blacklist " +
                        "auszuschließen.", 100);
        if (showAlways || lastShown.isBefore(whatsNewInfo.getDate())) {
            list.add(whatsNewInfo);
        }

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_02.png",

                "Filme mit Untertitel",
                "Es gibt Filme, die den Untertitel im \"Film\" anzeigen. " +
                        "In den Einstellungen kann angegeben werden " +
                        "ob diese Filme auch als \"Film mit Untertitel\" geführt werden sollen. " +
                        "Für welche Filme das dann zutrifft, kann man hier vorgegeben." +
                        "\n\n" +
                        "Im Kontextmenü in der Tabelle mit den Filmen, gibt es jetzt einen weiteren Menüpunkt. " +
                        "Damit können die Untertitel-Dateien direkt heruntergeladen werden.", 150);
        if (showAlways || lastShown.isBefore(whatsNewInfo.getDate())) {
            list.add(whatsNewInfo);
        }

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_03.png",

                "Filmfilter \"Thema exact\"",
                "Es ist jetzt möglich, durch Texteingabe die angezeigte Liste der Themen " +
                        "zu filtern\n" +
                        "Mit ENTER wird das selektierte Theme gewählt. Um ein Thema auszuwählen, können die " +
                        "Courser-Tasten und die Tab-Tasten benutzt werden.", 100);
        if (showAlways || lastShown.isBefore(whatsNewInfo.getDate())) {
            list.add(whatsNewInfo);
        }

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11), "/de/p2tools/mtplayer/res/whatsnew/whatsNew_04.png",
                "Infotab Downloadfehler",
                "Im Tab Download gibts bei den Infos einen neuen Reiter: \"Downloadfehler\". Dort " +
                        "werden die Fehlermeldungen von fehlerhaften Downloads angezeigt.", 80);
        if (showAlways || lastShown.isBefore(whatsNewInfo.getDate())) {
            list.add(whatsNewInfo);
        }

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_05.png",

                "Proxy-Server",
                "Es ist jetzt möglich, einen Proxy-Server zu verwenden. In den \"Programmeinstellungen->Proxy\" " +
                        "kann er angegeben und eingeschaltet werden. Die Downloads laufen dann über " +
                        "den Proxy-Server.", 70);
        if (showAlways || lastShown.isBefore(whatsNewInfo.getDate())) {
            list.add(whatsNewInfo);
        }

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 21),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_06.png",

                "Infos der markierten Tabellen-Zeile anzeigen",
                "Die Anzeige der Infos einer markierten Zeile in den Tabellen Filme/Downloads/Abos " +
                        "kann ein- und ausgeschaltet werden. Das ist im Kontextmenü der Tabelle möglich.", 70);
        if (showAlways || lastShown.isBefore(whatsNewInfo.getDate())) {
            list.add(whatsNewInfo);
        }

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 3, 3),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_07.png",

                "Filmfilter",
                "Die verwendeten Filmfilter werden jetzt gespeichert und sind bei " +
                        "einem Programmneustart wieder vorhanden. " +
                        "Eine Auswahl listet sie auf und können so auch ausgewählt werden.", 70);
        if (showAlways || lastShown.isBefore(whatsNewInfo.getDate())) {
            list.add(whatsNewInfo);
        }

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 3, 3), "",
                "Was sich sonst noch geändert hat",
                "* Die Filme des Senders \"Radio Bremen TV\" wurden mit dem Sendernamen \"Radio Bremen TV\", " +
                        "\"rbtv\" und \"RBTV\" gelistet. Die werden jetzt zusammengefasst zu dem " +
                        "Sendernamen: \"RBTV\"." +
                        "\n\n" +
                        "* Es gibt ein neues ShortCut zum Anzeigen der Blacklist-Einstellungen: ALT+B" +
                        "\n\n" +
                        "* In der Ersetzungstabelle für Download-Namen (Einstellungen->Download) " +
                        "können jetzt auch RegEx verwendet werden.",
                180);
        if (showAlways || lastShown.isBefore(whatsNewInfo.getDate())) {
            list.add(whatsNewInfo);
        }
    }

    private static void checkProgUpdate() {
        // Prüfen obs ein Programmupdate gibt
        PDuration.onlyPing("checkProgUpdate");
        if (ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue() &&
                !updateCheckTodayDone()) {
            // nach Updates suchen
            runUpdateCheck(false);

        } else {
            // will der User nicht --oder-- wurde heute schon gemacht
            List<String> list = new ArrayList<>(5);
            list.add("Kein Update-Check:");
            if (!ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue()) {
                list.add("  der User will nicht");
            }
            if (updateCheckTodayDone()) {
                list.add("  heute schon gemacht");
            }
            PLog.sysLog(list);
        }
    }

    private static boolean updateCheckTodayDone() {
        return ProgConfig.SYSTEM_UPDATE_DATE.get().equals(P2DateConst.F_FORMAT_yyyy_MM_dd.format(new Date()));
    }

    private static void runUpdateCheck(boolean showAlways) {
        ProgConfig.SYSTEM_UPDATE_DATE.setValue(P2DateConst.F_FORMAT_yyyy_MM_dd.format(new Date()));
        new SearchProgramUpdate(ProgData.getInstance()).searchNewProgramVersion(showAlways);
    }
}
