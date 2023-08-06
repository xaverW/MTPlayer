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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.tools.MLBandwidthTokenBucket;
import de.p2tools.mtplayer.controller.tools.MLInputStream;
import de.p2tools.mtplayer.gui.dialog.DownloadContinueDialogController;
import de.p2tools.mtplayer.gui.dialog.DownloadErrorDialogController;
import de.p2tools.mtplayer.gui.tools.MTInfoFile;
import de.p2tools.mtplayer.gui.tools.MTListener;
import de.p2tools.mtplayer.gui.tools.MTSubtitle;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DirectHttpDownload extends Thread {

    /**
     * HTTP Timeout in milliseconds.
     */
    private static final int TIMEOUT_LENGTH = 5000;
    private final ProgData progData;
    private final DownloadData download;
    private final java.util.Timer bandwidthCalculationTimer;
    private long aktBandwidth = 0, aktSize = 0;
    private double percent, ppercent = DownloadConstants.PROGRESS_WAITING, startPercent = DownloadConstants.PROGRESS_NOT_STARTED;
    private HttpURLConnection conn = null;
    private long downloaded = 0;
    private File file = null;
    private String responseCode;
    private boolean work = false;
    private final MTListener listener = new MTListener(MTListener.EVENT_TIMER_HALF_SECOND, DirectHttpDownload.class.getSimpleName()) {
        @Override
        public void ping() {
            work = true;
        }
    };
    private FileOutputStream fos = null;

    public DirectHttpDownload(ProgData progData, DownloadData d, java.util.Timer bandwidthCalculationTimer) {
        super();
        this.progData = progData;
        this.bandwidthCalculationTimer = bandwidthCalculationTimer;
        download = d;
        setName("DIRECT DL THREAD: " + d.getTitle());
        download.setStateStartedRun();
        MTListener.addListener(listener);
    }

    @Override
    public synchronized void run() {
        StarterClass.startMsg(download);
        boolean restartWithOutSSL = false;

        try {
            Files.createDirectories(Paths.get(download.getDestPath()));
        } catch (final IOException ignored) {
        }

        int restartCount = 0;
        boolean restart = true;
        while (restart) {
            restart = false;
            try {
                final URL url = new URL(download.getUrl());
                file = new File(download.getDestPathFile());

                if (!cancelDownload()) {
                    download.getDownloadSize().setSize(getContentLength(url));
                    download.getDownloadSize().setActFileSize(0);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(1000 * ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue());
                    conn.setReadTimeout(1000 * ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue());

                    if ((restartWithOutSSL || ProgConfig.SYSTEM_SSL_ALWAYS_TRUE.getValue())
                            && conn instanceof HttpsURLConnection) {
                        //Create a trust manager that does not validate certificate chains
                        HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                        httpsConn.setHostnameVerifier(
                                // Create all-trusting host name verifier
                                (hostname, session) -> true);
                    }

                    setupHttpConnection(conn);
                    conn.connect();
                    final int httpResponseCode = conn.getResponseCode();
                    if (httpResponseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                        // Range passt nicht, also neue Verbindung versuchen...
                        if (httpResponseCode == 416) {
                            conn.disconnect();
                            // Get a new connection and reset download param...
                            conn = (HttpURLConnection) url.openConnection();
                            downloaded = 0;
                            setupHttpConnection(conn);
                            conn.connect();
                            // hier war es dann nun wirklich...
                            if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                                download.setStateError();
                            }

                        } else {
                            // ==================================
                            // dann wars das
                            responseCode = "Responsecode: " + conn.getResponseCode() + P2LibConst.LINE_SEPARATOR + conn.getResponseMessage();
                            PLog.errorLog(915236798, "HTTP-Fehler: " + conn.getResponseCode() + ' ' + conn.getResponseMessage());
                            if (download.getStart().getRestartCounter() == 0) {
                                // nur beim ersten Mal melden -> nervt sonst
                                Platform.runLater(() -> new DownloadErrorDialogController(download, responseCode));
                            }
                            download.setErrorMessage(responseCode);
                            download.setStateError();
                        }
                    }
                }

                if (download.isStateStartedRun()) {
                    downloadContent();
                }

            } catch (final Exception ex) {
                //===========
                //Probleme über Probleme
                if (ex instanceof javax.net.ssl.SSLHandshakeException
                        && restartCount < ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getValue()) {

                    //der mehrfache Versuch, fehlerhafte Downloads zu starten, macht hier keinen sinn
                    download.getStart().setRestartCounter(ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getValue());

                    //dann gabs Probleme bei https
                    if (!restartWithOutSSL) {
                        //nur dann nochmal fragen
                        restartWithOutSSL = restartHttps(restartCount);
                    }
                    if (restartWithOutSSL) {
                        restartCount++;
                        restart = true;
                    } else {
                        download.setErrorMessage(ex.getMessage());
                        download.setStateError();
                    }

                } else if (ex instanceof java.io.IOException
                        && restartCount < ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getValue()) {
                    if (ex instanceof java.net.SocketTimeoutException) {
                        // Timeout Fehlermeldung für zxd :)
                        final ArrayList<String> text = new ArrayList<>();
                        text.add("Timeout, Download Restarts: " + restartCount);
                        text.add("Ziel: " + download.getDestPathFile());
                        text.add("URL: " + download.getUrl());
                        PLog.sysLog(text.toArray(new String[text.size()]));
                    }
                    restartCount++;
                    restart = true;

                } else {
                    // dann weiß der Geier!
                    PLog.errorLog(316598941, ex, "Fehler");
                    if (download.getStart().getRestartCounter() == 0) {
                        // nur beim ersten Mal melden -> nervt sonst
                        Platform.runLater(() -> new DownloadErrorDialogController(download, ex.getMessage()));
                    }
                    download.setErrorMessage(ex.getMessage());
                    download.setStateError();
                }
            }
        }

        try {
            if (download.getStart().getInputStream() != null) {
                download.getStart().getInputStream().close();
            }
            if (fos != null) {
                fos.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        } catch (final Exception ignored) {
        }

        StarterClass.finalizeDownload(download);
        MTListener.removeListener(listener);
    }

    /**
     * Return the content length of the requested Url.
     *
     * @param url {@link java.net.URL} to the specified content.
     * @return Length in bytes or -1 on error.
     */
    private long getContentLength(final URL url) {
        long ret = -1;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
            connection.setReadTimeout(TIMEOUT_LENGTH);
            connection.setConnectTimeout(TIMEOUT_LENGTH);
            if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                ret = connection.getContentLengthLong();
            }
            // alles unter 300k sind Playlisten, ...
            if (ret < 300 * 1000) {
                ret = -1;
            }
        } catch (final Exception ex) {
            ret = -1;
            PLog.errorLog(643298301, ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return ret;
    }

    /**
     * Setup the HTTP connection common settings
     *
     * @param conn The active connection.
     */
    private void setupHttpConnection(HttpURLConnection conn) {
        conn.setRequestProperty("Range", "bytes=" + downloaded + '-');
        conn.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
        conn.setDoInput(true);
        conn.setDoOutput(true);
    }

    /**
     * Start the actual download process here.
     *
     * @throws Exception
     */
    private void downloadContent() throws Exception {
        if (download.getInfoFile()) {
            MTInfoFile.writeInfoFile(download);
        }
        if (download.isSubtitle()) {
            new MTSubtitle().writeSubtitle(download);
        }

        download.getStart().setInputStream(new MLInputStream(conn.getInputStream(),
                bandwidthCalculationTimer, ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE));
        fos = new FileOutputStream(file, (downloaded != 0));
        download.getDownloadSize().setActFileSize(downloaded);
        final byte[] buffer = new byte[MLBandwidthTokenBucket.DEFAULT_BUFFER_SIZE];
        int len;


        while ((len = download.getStart().getInputStream().read(buffer)) != -1 && (!download.isStateStopped())) {
            downloaded += len;
            fos.write(buffer, 0, len);

            if (!work) {
                continue;
            }
            work = false;
            setDownloaded();
        }
        setDownloaded();

        if (!download.isStateStopped()) {
            if (download.getSource().equals(DownloadConstants.SRC_BUTTON)) {
                // direkter Start mit dem Button
                download.setStateFinished();
            } else if (StarterClass.check(progData, download)) {
                // Anzeige ändern - fertig
                download.setStateFinished();
            } else {
                // Anzeige ändern - bei Fehler fehlt der Eintrag
                download.setStateError();
            }
        } else {
            download.stopDownload(); // nochmal da RUNTIME_EXEC ja weiter läuft
        }
    }

    private void setDownloaded() {
        download.getDownloadSize().setActFileSize(downloaded);

        // für die Anzeige prüfen ob sich was geändert hat
        if (aktSize != download.getDownloadSize().getActFileSize()) {
            aktSize = download.getDownloadSize().getActFileSize();
        }
        if (download.getDownloadSize().getSize() > 0) {
            percent = 1.0 * aktSize / download.getDownloadSize().getSize();
            if (startPercent == DownloadConstants.PROGRESS_NOT_STARTED) {
                startPercent = percent;
            }

            // percent muss zwischen 0 und 1 liegen
            if (percent == DownloadConstants.PROGRESS_WAITING) {
                percent = DownloadConstants.PROGRESS_STARTED;
            } else if (percent >= DownloadConstants.PROGRESS_FINISHED) {
                percent = DownloadConstants.PROGRESS_NEARLY_FINISHED;
            }
            download.setProgress(percent);
            if (percent != ppercent) {
                ppercent = percent;

                // Restzeit ermitteln
                if (percent > (DownloadConstants.PROGRESS_STARTED) && percent > startPercent) {
                    long timeLeft = 0;
                    long sizeLeft = download.getDownloadSize().getSize() - download.getDownloadSize().getActFileSize();
                    if (sizeLeft > 0 && aktBandwidth > 0) {
                        timeLeft = sizeLeft / aktBandwidth;
                    }
                    download.getStart().setTimeLeftSeconds((int) timeLeft);

                    // anfangen zum Schauen kann man, wenn die Restzeit kürzer ist
                    // als die bereits geladene Spielzeit des Films
                    canAlreadyStarted(download);
                }
            }
        }
        aktBandwidth = download.getStart().getInputStream().getBandwidth(); // bytes per second
        if (aktBandwidth != download.getStart().getBandwidth()) {
            download.getStart().setBandwidth(aktBandwidth);
        }
    }

    private boolean restartHttps(int restartCount) {
        final ArrayList<String> text = new ArrayList<>();
        text.add("https, Download Restarts: " + restartCount);
        text.add("Ziel: " + download.getDestPathFile());
        text.add("URL: " + download.getUrl());
        PLog.sysLog(text.toArray(new String[text.size()]));

        AtomicBoolean dialog = new AtomicBoolean(true);
        AtomicBoolean ret = new AtomicBoolean(false);
        Platform.runLater(() -> {
            ret.set(PAlert.showAlertOkCancel("HTTPS",
                    "Problem mit der HTTPS-Verbindung",
                    "Beim Verbindungsaufbau mit der HTTPS-URL trat ein Problem auf. Soll " +
                            "versucht werden, die Verbindung ohne die Prüfung des Zertifikats " +
                            "aufzubauen?\n\n" +
                            download.getTitle() + "\n\n" +
                            download.getUrl()));
            dialog.set(false);
        });

        while (dialog.get()) {
            try {
                wait(100);
            } catch (final Exception ignored) {
            }
        }
        return ret.get();
    }

    private boolean cancelDownload() {
        if (!file.exists()) {
            // dann ist alles OK
            return false;
        }

        AtomicBoolean dialogBreakIsVis = new AtomicBoolean(true);
        AtomicBoolean retBreak = new AtomicBoolean(true);
        Platform.runLater(() -> {
            retBreak.set(break_());
            dialogBreakIsVis.set(false);
        });
        while (dialogBreakIsVis.get()) {
            try {
                wait(100);
            } catch (final Exception ignored) {

            }
        }
        return retBreak.get();
    }

    private boolean break_() {
        boolean cancel = false;
        if (file.exists()) {

            AskBeforeDeleteState.ContinueDownload result;
            boolean isNewName = false;

            if (ProgConfig.DOWNLOAD_CONTINUE.getValue() == AskBeforeDeleteState.DOWNLOAD_RESTART__CONTINUE) {
                //weiterführen
                result = AskBeforeDeleteState.ContinueDownload.CONTINUE;

            } else if (ProgConfig.DOWNLOAD_CONTINUE.getValue() == AskBeforeDeleteState.DOWNLOAD_RESTART__RESTART) {
                //neu starten
                result = AskBeforeDeleteState.ContinueDownload.RESTART;

            } else {
                //vorher fragen
                DownloadContinueDialogController downloadContinueDialogController =
                        new DownloadContinueDialogController(ProgConfig.DOWNLOAD_DIALOG_CONTINUE_SIZE,
                                progData, download, true /* weiterführen */);
                result = downloadContinueDialogController.getResult();
            }

            switch (result) {
                case CANCEL:
                    // dann wars das
                    download.stopDownload();
                    cancel = true;
                    break;

                case CONTINUE:
                    downloaded = file.length();
                    break;

                case RESTART:
                    if (!isNewName) {
                        // dann mit gleichem Namen und Datei vorher löschen
                        try {
                            Files.deleteIfExists(file.toPath());
                            file = new File(download.getDestPathFile());
                        } catch (final Exception ex) {
                            // kann nicht gelöscht werden, evtl. klappt ja das Überschreiben
                            PLog.errorLog(915263654, ex,
                                    "file exists: " + download.getDestPathFile());
                        }
                    } else {
                        // dann mit neuem Namen
                        try {
                            Files.createDirectories(Paths.get(download.getDestPath()));
                        } catch (final IOException ignored) {
                        }
                        file = new File(download.getDestPathFile());
                    }
                    break;
            }
        }
        return cancel;
    }

    private void canAlreadyStarted(DownloadData downloadData) {
        if (downloadData.isStateStartedRun()) {

            if (downloadData.getDurationMinute() > 0
                    && downloadData.getStart().getTimeLeftSeconds() > 0
                    && downloadData.getDownloadSize().getActFileSize() > 0
                    && downloadData.getDownloadSize().getSize() > 0) {

                // macht nur dann Sinn
                final long filetimeAlreadyLoadedSeconds = downloadData.getDurationMinute() * 60
                        * downloadData.getDownloadSize().getActFileSize()
                        / downloadData.getDownloadSize().getSize();

                if (filetimeAlreadyLoadedSeconds > (downloadData.getStart().getTimeLeftSeconds() * 1.1 /* plus 10% zur Sicherheit */)) {
                    downloadData.getStart().setStartViewing(true);
                }
            }
        }
    }
}
