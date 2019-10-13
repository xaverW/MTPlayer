/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.controller.data.download;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.abo.Abo;

import java.text.NumberFormat;
import java.util.Locale;

public class DownloadInfosFactory {

    private static final String SEPARATOR = "  ||  ";
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
    private static final ProgData progData = ProgData.getInstance();

    private DownloadInfosFactory() {
    }

    public static synchronized String getStatusInfosFilm() {
        String textLinks;
        final int sumFilmlist = progData.filmlist.size();
        final int sumFilmShown = progData.filmGuiController.getFilmCount();
        final int runs = progData.downloadListButton.getListOfStartsNotFinished(DownloadConstants.SRC_BUTTON).size();

        String sumFilmlistStr = numberFormat.format(sumFilmShown);
        String sumFilmShownStr = numberFormat.format(sumFilmlist);

        // Anzahl der Filme
        if (sumFilmShown == 1) {
            textLinks = "1 Film";
        } else {
            textLinks = sumFilmlistStr + " Filme";
        }
        if (sumFilmlist != sumFilmShown) {
            textLinks += " (Insgesamt: " + sumFilmShownStr + " )";
        }
        // laufende Programme
        if (runs == 1) {
            textLinks += SEPARATOR;
            textLinks += (runs + " laufender Film");
        } else if (runs > 1) {
            textLinks += SEPARATOR;
            textLinks += (runs + " laufende Filme");
        }
        // auch die Downloads anzeigen
        if (progData.downloadInfos.getAmount() > 0) {
            textLinks += SEPARATOR;

            // Anzahl der Downloads
            if (progData.downloadInfos.getAmount() == 1) {
                textLinks += "1 Download";
            } else {
                textLinks += progData.downloadInfos.getAmount() + " Downloads";
            }
            textLinks += ": ";
            textLinks += getRunningDownloadsInfos();
        }

        return textLinks;
    }

    public static String getStatusInfosDownload() {
        String textLinks;
        String sumDownloadListStr = numberFormat.format(progData.downloadList.size());
        String sumDownloadsShownStr = numberFormat.format(progData.downloadGuiController.getDownloadsShown());

        // Anzahl der Downloads
        if (progData.downloadGuiController.getDownloadsShown() == 1) {
            textLinks = "1 Download";
        } else {
            textLinks = sumDownloadsShownStr + " Downloads";
        }

        // weitere Infos anzeigen
        if (progData.downloadList.size() != progData.downloadGuiController.getDownloadsShown()) {
            textLinks += " (Insgesamt: " + sumDownloadListStr;
            if (progData.downloadInfos.getPlacedBack() >= 1) {
                textLinks += ", zurückgestellt: " + numberFormat.format(progData.downloadInfos.getPlacedBack());
            }
            textLinks += ")";
        }

        if (progData.downloadInfos.getAmountAbo() > 0
                && progData.downloadInfos.getAmountDownload() == 0) {
            // nur Abos
            textLinks += SEPARATOR;
            textLinks += "nur aus Abos";
            textLinks += SEPARATOR;

        } else if (progData.downloadInfos.getAmountAbo() == 0 &&
                progData.downloadInfos.getAmountDownload() > 0) {
            // keine Abos
            textLinks += SEPARATOR;
            textLinks += "nur direkte Downloads";
            textLinks += SEPARATOR;

        } else if (progData.downloadInfos.getAmountAbo() > 0
                && progData.downloadInfos.getAmountDownload() > 0) {
            textLinks += SEPARATOR;

            // Abos
            textLinks += numberFormat.format(progData.downloadInfos.getAmountAbo()) + " aus Abos, ";

            // direkte Downloads
            if (progData.downloadInfos.getAmountDownload() == 1) {
                textLinks += "1 direkter Download";
            } else if (progData.downloadInfos.getAmountDownload() > 1) {
                textLinks += numberFormat.format(progData.downloadInfos.getAmountDownload()) + " direkte Downloads";
            }

            textLinks += SEPARATOR;
        }


        if (progData.downloadInfos.getAmount() > 0) {
            // nur wenn ein Download läuft, wartet, ..
            textLinks += getRunningDownloadsInfos();
        }

        return textLinks;
    }

    public static String getStatusInfosAbo() {
        String textLinks;
        int countOn = 0;
        int countOff = 0;
        final int sumAboList = progData.aboList.size();
        final int sumAboShown = progData.aboGuiController.getAboCount();

        for (final Abo abo : progData.aboList) {
            if (abo.isActive()) {
                ++countOn;
            } else {
                ++countOff;
            }
        }

        String sumAboListStr = numberFormat.format(sumAboList);
        String sumAboShownStr = numberFormat.format(sumAboShown);

        // Anzahl der Abos
        if (sumAboShown == 1) {
            textLinks = "1 Abo";
        } else {
            textLinks = sumAboShownStr + " Abos";
        }
        if (sumAboList != sumAboShown) {
            textLinks += " (Insgesamt: " + sumAboListStr;
            textLinks += ")";
        }

        textLinks += SEPARATOR + countOn + " eingeschaltet, " + countOff + " ausgeschaltet";

        return textLinks;
    }

    private static synchronized String getRunningDownloadsInfos() {
        String textLinks = "";
        if (progData.downloadInfos.getLoading() == 1) {
            textLinks += "1 läuft";
        } else {
            textLinks += progData.downloadInfos.getLoading() + " laufen";
        }

        if (progData.downloadInfos.getLoading() > 0) {
            textLinks += " (" + progData.downloadInfos.getBandwidthStr() + ')';
        }

        if (progData.downloadInfos.getStartedNotLoading() == 1) {
            textLinks += ", 1 wartet";
        } else {
            textLinks += ", " + numberFormat.format(progData.downloadInfos.getStartedNotLoading()) + " warten";
        }
        if (progData.downloadInfos.getFinishedOk() > 0) {
            if (progData.downloadInfos.getFinishedOk() == 1) {
                textLinks += ", 1 fertig";
            } else {
                textLinks += ", " + numberFormat.format(progData.downloadInfos.getFinishedOk()) + " fertig";
            }
        }
        if (progData.downloadInfos.getFinishedError() > 0) {
            if (progData.downloadInfos.getFinishedError() == 1) {
                textLinks += ", 1 fehlerhaft";
            } else {
                textLinks += ", " + numberFormat.format(progData.downloadInfos.getFinishedError()) + " fehlerhaft";
            }
        }

        return textLinks;
    }
}
