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


package de.p2tools.mtplayer.controller.data.download;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.tools.MLBandwidthTokenBucket;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.file.PFileSize;

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
//        final int runs = progData.downloadListButton.getListOfStartsNotFinished(DownloadConstants.SRC_BUTTON).size();

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
//        if (runs == 1) {
//            textLinks += SEPARATOR;
//            textLinks += (runs + " laufender Film");
//        } else if (runs > 1) {
//            textLinks += SEPARATOR;
//            textLinks += (runs + " laufende Filme");
//        }
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

        } else if (progData.downloadInfos.getAmountAbo() == 0 &&
                progData.downloadInfos.getAmountDownload() > 0) {
            // keine Abos
            textLinks += SEPARATOR;
            textLinks += "nur direkte Downloads";

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
        }

        if (progData.downloadInfos.getAmount() > 0) {
            // nur wenn ein Download läuft, wartet, ..
            textLinks += SEPARATOR;
            textLinks += getRunningDownloadsInfos();
        }

        if (ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue() != MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE) {
            textLinks += SEPARATOR;
            textLinks += "Max. Bandbreite: " + ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue() + "kB/s";
        }
        return textLinks;
    }

    public static String getStatusInfosAbo() {
        String textLinks;
        int countOn = 0;
        int countOff = 0;
        final int sumAboList = progData.aboList.size();
        final int sumAboShown = progData.aboGuiController.getAboCount();

        for (final AboData abo : progData.aboList) {
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

    public static synchronized String getTrayInfo() {
        if (progData.downloadList.size() == 0) {
            // dann gibts keine :)
            return "keine Downloads";
        }

        String text1;
        text1 = getInfoText();
        text1 += P2LibConst.LINE_SEPARATOR;

        //Größe laufende Downloads
        text1 += "laufende Downloads: ";
        if (progData.downloadInfos.getByteLoadingDownloads() > 0 ||
                progData.downloadInfos.getByteLoadingDownloadsAlreadyLoaded() > 0) {
            if (progData.downloadInfos.getByteLoadingDownloads() > 0) {
                text1 += PFileSize.convertToStr(progData.downloadInfos.getByteLoadingDownloadsAlreadyLoaded()) + " von "
                        + PFileSize.convertToStr(progData.downloadInfos.getByteLoadingDownloads());
            } else {
                text1 += PFileSize.convertToStr(progData.downloadInfos.getByteLoadingDownloadsAlreadyLoaded());
            }
            text1 += ", " + progData.downloadInfos.getBandwidthStr();
        }
        text1 += P2LibConst.LINE_SEPARATOR;

        //Größe wartende Downloads
        text1 += "wartende Downloads: ";
        if (progData.downloadInfos.getByteWaitingDownloads() > 0) {
            text1 += PFileSize.convertToStr(progData.downloadInfos.getByteWaitingDownloads());
        }

        return text1;
    }

    private static String getInfoText() {
        String text1 = "Downloads: " + progData.downloadList.size();

        if (progData.downloadList.size() > 0) {
            String txt;
            txt = ", (";

            if (progData.downloadInfos.getPlacedBack() != 0) {
                txt += progData.downloadInfos.getPlacedBack() + " zurückgestellt, ";
            }

            if (progData.downloadInfos.getLoading() == 1) {
                txt += "1 läuft";
            } else {
                txt += progData.downloadInfos.getLoading() + " laufen";
            }

            if (progData.downloadInfos.getStartedNotLoading() == 1) {
                txt += ", 1 wartet";
            } else {
                txt += ", " + progData.downloadInfos.getStartedNotLoading() + " warten";
            }

            if (progData.downloadInfos.getFinishedOk() > 0) {
                if (progData.downloadInfos.getFinishedOk() == 1) {
                    txt += ", 1 fertig";
                } else {
                    txt += ", " + progData.downloadInfos.getFinishedOk() + " fertig";
                }
            }

            if (progData.downloadInfos.getFinishedError() > 0) {
                if (progData.downloadInfos.getFinishedError() == 1) {
                    txt += ", 1 fehlerhaft";
                } else {
                    txt += ", " + progData.downloadInfos.getFinishedError() + " fehlerhaft";
                }
            }
            txt += ")";
            text1 += txt;
        }

        return text1;
    }
}
