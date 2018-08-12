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

package de.mtplayer.mtp.controller.starter;

import de.mtplayer.mLib.tools.MLProperty;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.controller.data.download.DownloadSize;
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuntimeExec {

    public static final String TRENNER_PROG_ARRAY = "<>";
    private static final int INPUT = 1;
    private static final int ERROR = 2;
    Thread clearIn;
    Thread clearOut;
    private Process process = null;
    private static int procNr = 0;
    private static final Pattern patternFlvstreamer = Pattern.compile("([0-9]*.[0-9]{1}%)");
    private static final Pattern patternFlvstreamerComplete = Pattern.compile("Download complete");
    private static final Pattern patternFfmpeg = Pattern.compile("(?<=  Duration: )[^,]*"); // Duration: 00:00:30.28, start: 0.000000, bitrate: N/A
    private static final Pattern patternTime = Pattern.compile("(?<=time=)[^ ]*");  // frame=  147 fps= 17 q=-1.0 size=    1588kB time=00:00:05.84 bitrate=2226.0kbits/s
    private static final Pattern patternSize = Pattern.compile("(?<=size=)[^k]*");  // frame=  147 fps= 17 q=-1.0 size=    1588kB time=00:00:05.84 bitrate=2226.0kbits/s

    private double totalSecs = 0;
    private long oldSize = 0;
    private long oldSecs = 0;
    private Download download = null;
    private DownloadSize mVFilmSize = null;
    private final String strProgCall;
    private String[] arrProgCallArray = null;
    private String strProgCallArray = "";
    private PlayerMessage playerMessage = new PlayerMessage();

    public RuntimeExec(Download download) {
        this.download = download;

        this.mVFilmSize = download.getDownloadSize();
        this.strProgCall = download.getProgramCall();
        arrProgCallArray = strProgCallArray.split(TRENNER_PROG_ARRAY);
        this.strProgCallArray = download.getProgramCallArray();
        if (arrProgCallArray.length <= 1) {
            arrProgCallArray = null;
        }

    }

    public RuntimeExec(String p) {
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

            clearIn = new Thread(new ClearInOut(INPUT, process));
            clearOut = new Thread(new ClearInOut(ERROR, process));

            clearIn.setName("exec-in");
            clearIn.start();

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
        private double percent = DownloadInfos.PROGRESS_WAITING;
        private double percent_start = DownloadInfos.PROGRESS_NOT_STARTED;

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
                    GetPercentageFromErrorStream(inStr);
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

        private void GetPercentageFromErrorStream(String input) {
            // by: siedlerchr für den flvstreamer und rtmpdump
            Matcher matcher;
            matcher = patternFlvstreamer.matcher(input);
            if (matcher.find()) {
                try {
                    String percent = matcher.group();
                    percent = percent.substring(0, percent.length() - 1);
                    final double d = Double.parseDouble(percent);
                    notifyDouble(d);
                } catch (final Exception ex) {
                    PLog.errorLog(912036780, input);
                }
                return;
            }
            matcher = patternFlvstreamerComplete.matcher(input);
            if (matcher.find()) {
                // dann ist der Download fertig, zur sicheren Erkennung von 100%
                notifyDouble(100);
                return;
            }

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
                            final long aktSize = Integer.parseInt(s.replace("kB", ""));
                            mVFilmSize.setAktFileSize(aktSize * 1_000);
                            final long akt = download.getStart().getStartTime().diffInSeconds();
                            if (oldSecs < akt - 5) {
                                download.getStart().setBandwidth((aktSize - oldSize) * 1_000 / (akt - oldSecs));
                                oldSecs = akt;
                                oldSize = aktSize;
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
                PLog.errorLog(912036780, input);
            }
        }

        private void notifyDouble(double d) {
            // d = 0 - 100%
            final double pNeu = d / 100;
            MLProperty.setProperty(download.progressProperty(), pNeu);
            if (pNeu != percent) {
                percent = pNeu;
                if (percent_start == DownloadInfos.PROGRESS_NOT_STARTED) {
                    // für wiedergestartete Downloads
                    percent_start = percent;
                }
                if (percent > (percent_start + 5 * DownloadInfos.PROGRESS_1_PERCENT)) {
                    // sonst macht es noch keinen Sinn
                    final int diffTime = download.getStart().getStartTime().diffInSeconds();
                    final double diffPercent = percent - percent_start;
                    final double restPercent = DownloadInfos.PROGRESS_FINISHED - percent;
                    download.getStart().setTimeLeft((long) (diffTime * restPercent / diffPercent));
                }
            }
        }
    }
}
