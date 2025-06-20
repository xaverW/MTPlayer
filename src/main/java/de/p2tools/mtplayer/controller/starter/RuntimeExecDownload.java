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
import de.p2tools.p2lib.mediathek.download.DownloadSize;
import de.p2tools.p2lib.tools.log.P2Log;

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
    private static final Pattern patternYtDlp = Pattern.compile("(?<=\\[download\\] [ ])[^%]*"); // [download]   1.8% of ~   9.91MiB at  239.18KiB/s ETA Unknown (frag 3/146)
    private static final Pattern patternYtDlpSize = Pattern.compile("(?<=of ~[ ])[^M]*");  // [download]   1.8% of ~   9.91MiB at  239.18KiB/s ETA Unknown (frag 3/146)
    private static final Pattern patternYtDlpBandwidth = Pattern.compile("(?<=at [ ])[^.]*");  // [download]   1.8% of ~   9.91MiB at  239.18KiB/s ETA Unknown (frag 3/146)

    private static final Pattern patternFfmpegDuration = Pattern.compile("(?<=  Duration: )[^,]*"); // Duration: 00:00:30.28, start: 0.000000, bitrate: N/A
    private static final Pattern patternFfmpegTime = Pattern.compile("(?<=time=)[^ ]*");  // frame=  147 fps= 17 q=-1.0 size=    1588kB time=00:00:05.84 bitrate=2226.0kbits/s
    private static final Pattern patternFfmpegSize = Pattern.compile("(?<=size=)[^k]*");  // frame=  147 fps= 17 q=-1.0 size=    1588kB time=00:00:05.84 bitrate=2226.0kbits/s
    private static final Pattern patternFfmpegBitrate = Pattern.compile("(?<=bitrate=)[^k]*");  // size=    1536kB time=00:00:06.24 bitrate=2016.6kbits/s speed=  12x

    private double totalSecs = 0;
    private long oldSize = 0;
    private long oldSecs = 0;
    private DownloadData download = null;
    private DownloadSize mVFilmSize = null;
    private final String strProgCall;
    private String[] arrProgCallArray = null;
    private String strProgCallArray = "";
    private final PlayerMessage playerMessage = new PlayerMessage();

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

//    public RuntimeExecDownload(String p) {
//        strProgCall = p;
//    }

    //===================================
    // Public
    //===================================
    public Process exec(boolean log) {
        try {
            if (arrProgCallArray != null) {
                if (log) {
                    P2Log.sysLog("=====================");
                    P2Log.sysLog("Starte Array: ");
                    P2Log.sysLog(" -> " + strProgCallArray);
                    P2Log.sysLog("=====================");
                }
                process = Runtime.getRuntime().exec(arrProgCallArray);
            } else {
                if (log) {
                    P2Log.sysLog("=====================");
                    P2Log.sysLog("Starte nicht als Array:");
                    P2Log.sysLog(" -> " + strProgCall);
                    P2Log.sysLog("=====================");
                }
                process = Runtime.getRuntime().exec(strProgCall);
            }

            Thread clearIn = new Thread(new ClearInOut(INPUT, process));
            clearIn.setName("exec-in");
            clearIn.start();

            Thread clearOut = new Thread(new ClearInOut(ERROR, process));
            clearOut.setName("exec-out");
            clearOut.start();

        } catch (final IOException ex) {
            P2Log.errorLog(958454789, ex, "IOFehler beim Starten");
            String error = "Das Programm: [" + download.getProgramName() + "] konnte nicht gestartet werden.";
            download.getDownloadStartDto().addErrMsg(error);

        } catch (final Exception ex) {
            P2Log.errorLog(450028932, ex, "Fehler beim Starten");
            String error = ex.getLocalizedMessage();
            download.getDownloadStartDto().addErrMsg(error);
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
            // da wird nur hier aufgezeichnet, deswegen vor jedem Start löschen
            download.getDownloadStartDto().getErrStreamList().clear();

            String title = "";
            try {
                switch (art) {
                    case INPUT:
                        in = process.getInputStream();
                        title = "INPUTSTREAM";
                        break;
                    case ERROR:
                        in = process.getErrorStream();
                        synchronized (this) {
                            title = "ERRORSTREAM";
                        }
                        break;
                }
                buff = new BufferedReader(new InputStreamReader(in));
                String inStr;

                while ((inStr = buff.readLine()) != null) {
                    if (download.getProgramCall().contains("ffmpeg")) {
                        getFromErrorStreamFfmpeg(inStr);
                    } else if (download.getProgramCall().contains("yt-dlp")) {
                        getFromErrorStreamYtDlp(inStr);
                    }

                    download.getDownloadStartDto().addErrStream(inStr); // für den Fehlerdialog
                    playerMessage.playerMessage(title + ": " + inStr); // und fürs log
                }

            } catch (final IOException ignored) {
            } finally {
                try {
                    buff.close();
                } catch (final IOException ignored) {
                }
            }
        }

        private void getFromErrorStreamFfmpeg(String input) {
            Matcher matcher;
            // für ffmpeg
            // ffmpeg muss dazu mit dem Parameter -i gestartet werden:
            // -i %f -acodec copy -vcodec copy -y **
            try {
                // 404
                if (input.contains("HTTP error 404 Not Found")) {
                    download.getDownloadStartDto().addErrMsg("Die URL wurde nicht gefunden.");
                }

                // Gesamtzeit ffmpeg
                matcher = patternFfmpegDuration.matcher(input);
                if (matcher.find()) {
                    // Find duration
                    final String duration = matcher.group().trim();
                    final String[] hms = duration.split(":");
                    totalSecs = Integer.parseInt(hms[0]) * 3600
                            + Integer.parseInt(hms[1]) * 60
                            + Double.parseDouble(hms[2]);
                }

                // Gesamtgröße ffmpeg
                if (totalSecs > 0) {
                    // macht nur dann Sinn
                    matcher = patternFfmpegBitrate.matcher(input);
                    if (matcher.find()) {
                        final String bitrate = matcher.group().trim();
                        if (!bitrate.isEmpty()) {
                            try {
                                //Byte/s
                                final double rate = Double.parseDouble(bitrate) * 1000 / 8;
                                if (rate > 0) {
                                    final long sumSize = (long) (totalSecs * rate);
                                    mVFilmSize.setTargetSize(sumSize);
                                }
                            } catch (final NumberFormatException ignored) {
                            }
                        }
                    }
                }

                // Bandbreite ffmpeg
                matcher = patternFfmpegSize.matcher(input);
                if (matcher.find()) {
                    final String size = matcher.group().trim();
                    if (!size.isEmpty()) {
                        try {
                            final long actSize = Integer.parseInt(size.replace("kB", ""));
                            mVFilmSize.setActuallySize(actSize * 1_000);
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

                // Fortschritt ffmpeg
                matcher = patternFfmpegTime.matcher(input);
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
                    P2Log.errorLog(320154795, input);
                }
            }
        }

        private void getFromErrorStreamYtDlp(String input) {
            Matcher matcher;
            try {
                // Size yt-dlp
                matcher = patternYtDlpSize.matcher(input);
                if (matcher.find()) {
                    // [download]   1.8% of ~   9.91MiB at  239.18KiB/s ETA Unknown (frag 3/146)
                    // [download]  90.1% of ~  20.04MiB at    1.21MiB/s ETA 00:02 (frag 196/218)
                    final String size = matcher.group().trim();
                    if (!size.isEmpty()) {
                        try {
                            final double targetSize = Double.parseDouble(size) * 1_000 * 1_000;
                            final long targetLong = (long) targetSize;
                            if (mVFilmSize.getTargetSize() < targetLong) {
                                // sonst springt die Anzeige
                                mVFilmSize.setTargetSize(targetLong);
                            }
                        } catch (final NumberFormatException ignored) {
                        }
                    }
                }

                // Bandbreite yt-dlp
                matcher = patternYtDlpBandwidth.matcher(input);
                if (matcher.find()) {
                    // [download]   1.8% of ~   9.91MiB at  239.18KiB/s ETA Unknown (frag 3/146)
                    final String bandwidth = matcher.group().trim();
                    if (!bandwidth.isEmpty()) {
                        try {
                            final long b = Long.parseLong(bandwidth);
                            final long akt = download.getDownloadStartDto().getStartTime().diffInSeconds();
                            if (oldSecs < akt - 5) {
                                // nur alle 5s machen
                                download.setBandwidth(b * 1_000 * 1_000); // bytes per second
                                oldSecs = akt;
                            }
                        } catch (final NumberFormatException ignored) {
                        }
                    }
                }

                // Fortschritt yt-dlp
                matcher = patternYtDlp.matcher(input);
                if (matcher.find()) {
                    // [download]   1.8% of ~   9.91MiB at  239.18KiB/s ETA Unknown (frag 3/146)
                    final String percent = matcher.group().trim();
                    double d = Double.parseDouble(percent);
                    notifyDouble(d);
                }

            } catch (final Exception ex) {
                if (ProgData.debug) {
                    P2Log.errorLog(951254697, input);
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
