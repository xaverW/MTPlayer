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

package de.p2tools.mtplayer.controller.starter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.mtdownload.DownloadSize;
import de.p2tools.p2lib.tools.log.PLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuntimeExecDownload {

    private static final int INPUT = 1;
    private static final int ERROR = 2;
    private Process process = null;
    private static final Pattern patternFfmpeg = Pattern.compile("(?<=  Duration: )[^,]*"); // Duration: 00:00:30.28, start: 0.000000, bitrate: N/A
    private static final Pattern patternTime = Pattern.compile("(?<=time=)[^ ]*");  // frame=  147 fps= 17 q=-1.0 size=    1588kB time=00:00:05.84 bitrate=2226.0kbits/s
    private static final Pattern patternSize = Pattern.compile("(?<=size=)[^k]*");  // frame=  147 fps= 17 q=-1.0 size=    1588kB time=00:00:05.84 bitrate=2226.0kbits/s

    private double totalSecs = 0;
    private long oldSize = 0;
    private long oldSecs = 0;
    private DownloadData download = null;
    private DownloadSize mVFilmSize = null;
    private final String strProgCall;
    private String[] arrProgCallArray = null;
    private String strProgCallArray = "";
    private PlayerMessage playerMessage = new PlayerMessage();

    public RuntimeExecDownload(DownloadData download) {
        this.download = download;

        this.mVFilmSize = download.getDownloadSize();
        this.strProgCall = download.getProgramCall();

        this.strProgCallArray = download.getProgramCallArray();
        arrProgCallArray = strProgCallArray.split(DownloadConstants.TRENNER_PROG_ARRAY);
        if (arrProgCallArray.length <= 1) {
            arrProgCallArray = null;
        }
    }

    public RuntimeExecDownload(String p) {
        strProgCall = p;
    }

    //===================================
    // Public
    //===================================
    public Process exec(boolean log) {
        try {
            if (arrProgCallArray != null) {
                if (log) {
                    PLog.sysLog("=====================");
                    PLog.sysLog("Starte Array: ");
                    PLog.sysLog(" -> " + strProgCallArray);
                    PLog.sysLog("=====================");
                }
                process = Runtime.getRuntime().exec(arrProgCallArray);
            } else {
                if (log) {
                    PLog.sysLog("=====================");
                    PLog.sysLog("Starte nicht als Array:");
                    PLog.sysLog(" -> " + strProgCall);
                    PLog.sysLog("=====================");
                }
                process = Runtime.getRuntime().exec(strProgCall);
            }

            Thread clearIn = new Thread(new ClearInOut(INPUT, process));
            clearIn.setName("exec-in");
            clearIn.start();

            Thread clearOut = new Thread(new ClearInOut(ERROR, process));
            clearOut.setName("exec-out");
            clearOut.start();
        } catch (final Exception ex) {
            PLog.errorLog(450028932, ex, "Fehler beim Starten");
        }
        return process;
    }

    //===================================
    // Private
    //===================================
    private class ClearInOut implements Runnable {

        private final int art;
        private BufferedReader buff;
        private InputStream in;
        private final Process process;
        private double percent = DownloadConstants.PROGRESS_WAITING;
        private double percent_start = DownloadConstants.PROGRESS_NOT_STARTED;

        public ClearInOut(int a, Process p) {
            art = a;
            process = p;
        }

        @Override
        public void run() {
            String title = "";
            try {
                switch (art) {
                    case INPUT:
                        in = process.getInputStream();
                        title = "INPUTSTREAM";
                        break;
                    case ERROR:
                        in = process.getErrorStream();
                        //TH
                        synchronized (this) {
                            title = "ERRORSTREAM";
                        }
                        break;
                }
                buff = new BufferedReader(new InputStreamReader(in));
                String inStr;
                while ((inStr = buff.readLine()) != null) {
                    getPercentageFromErrorStream(inStr);
                    playerMessage.playerMessage(title + ": " + inStr);
                }
            } catch (final IOException ignored) {
            } finally {
                try {
                    buff.close();
                } catch (final IOException ignored) {
                }
            }
        }

        private void getPercentageFromErrorStream(String input) {
            Matcher matcher;
            // für ffmpeg
            // ffmpeg muss dazu mit dem Parameter -i gestartet werden:
            // -i %f -acodec copy -vcodec copy -y **
            try {
                // Gesamtzeit
                matcher = patternFfmpeg.matcher(input);
                if (matcher.find()) {
                    // Find duration
                    final String duration = matcher.group().trim();
                    final String[] hms = duration.split(":");
                    totalSecs = Integer.parseInt(hms[0]) * 3600
                            + Integer.parseInt(hms[1]) * 60
                            + Double.parseDouble(hms[2]);
                }

                // Bandbreite
                matcher = patternSize.matcher(input);
                if (matcher.find()) {
                    final String s = matcher.group().trim();
                    if (!s.isEmpty()) {
                        try {
                            final long actSize = Integer.parseInt(s.replace("kB", ""));
                            mVFilmSize.setFileActuallySize(actSize * 1_000);
                            final long akt = download.getDownloadStartDto().getStartTime().diffInSeconds();
                            if (oldSecs < akt - 5) {
                                // nur alle 5s machen
                                download.setBandwidth((actSize - oldSize) * 1_000 / (akt - oldSecs)); // bytes per second
                                oldSecs = akt;
                                oldSize = actSize;
                            }
                        } catch (final NumberFormatException ignored) {
                        }
                    }
                }

                // Fortschritt
                matcher = patternTime.matcher(input);
                if (totalSecs > 0 && matcher.find()) {
                    // ffmpeg    1611kB time=00:00:06.73 bitrate=1959.7kbits/s   
                    // avconv    size=   26182kB time=100.96 bitrate=2124.5kbits/s 
                    final String time = matcher.group();
                    if (time.contains(":")) {
                        final String[] hms = time.split(":");
                        final double aktSecs = Integer.parseInt(hms[0]) * 3600
                                + Integer.parseInt(hms[1]) * 60
                                + Double.parseDouble(hms[2]);
                        final double d = aktSecs / totalSecs * 100;
                        notifyDouble(d);
                    } else {
                        final double aktSecs = Double.parseDouble(time);
                        final double d = aktSecs / totalSecs * 100;
                        notifyDouble(d);
                    }
                }
            } catch (final Exception ex) {
                if (ProgData.debug) {
                    PLog.errorLog(912036780, input);
                }
            }
        }

        private void notifyDouble(double d) {
            // d = 0 - 100%
            final double pNeu = d / 100;
            download.setProgress(pNeu);
            if (pNeu != percent) {
                percent = pNeu;
                if (percent_start == DownloadConstants.PROGRESS_NOT_STARTED) {
                    // für wiedergestartete Downloads
                    percent_start = percent;
                }
                if (percent > (percent_start + 5 * DownloadConstants.PROGRESS_1_PERCENT)) {
                    // sonst macht es noch keinen Sinn
                    final int diffTime = download.getDownloadStartDto().getStartTime().diffInSeconds();
                    final double diffPercent = percent - percent_start;
                    final double restPercent = DownloadConstants.PROGRESS_FINISHED - percent;
                    download.getDownloadStartDto().setTimeLeftSeconds((int) (diffTime * restPercent / diffPercent));
                }
            }
        }
    }
}
