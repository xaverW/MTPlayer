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
import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.gui.dialog.DownloadContinueDialogController;
import de.p2tools.mtplayer.gui.dialog.DownloadErrorDialogController;
import de.p2tools.mtplayer.gui.tools.MTInfoFile;
import de.p2tools.mtplayer.gui.tools.MTSubtitle;
import de.p2tools.mtplayer.tools.MLBandwidthTokenBucket;
import de.p2tools.mtplayer.tools.MLInputStream;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.tools.log.PLog;
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

public class DirectHttpDownload extends Thread {

    private final ProgData progData;
    private final Download download;
    private HttpURLConnection conn = null;
    private long downloaded = 0;
    private File file = null;
    private String responseCode;
    private String exMessage;

    private FileOutputStream fos = null;

    private final java.util.Timer bandwidthCalculationTimer;
    private boolean retBreak;
    private boolean dialogBreakIsVis;

    public DirectHttpDownload(ProgData progData, Download d, java.util.Timer bandwidthCalculationTimer) {
        super();
        this.progData = progData;
        this.bandwidthCalculationTimer = bandwidthCalculationTimer;
        download = d;
        setName("DIRECT DL THREAD: " + d.getTitle());
        download.setStateStartedRun();
    }

    /**
     * HTTP Timeout in milliseconds.
     */
    private static final int TIMEOUT_LENGTH = 5000;

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
                bandwidthCalculationTimer, ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getIntegerProperty()));
        fos = new FileOutputStream(file, (downloaded != 0));
        download.getDownloadSize().addAktFileSize(downloaded);
        final byte[] buffer = new byte[MLBandwidthTokenBucket.DEFAULT_BUFFER_SIZE];
        double percent, ppercent = DownloadConstants.PROGRESS_WAITING, startPercent = DownloadConstants.PROGRESS_NOT_STARTED;
        int len;
        long aktBandwidth = 0, aktSize = 0;

        while ((len = download.getStart().getInputStream().read(buffer)) != -1 && (!download.isStateStoped())) {
            downloaded += len;
            fos.write(buffer, 0, len);
            download.getDownloadSize().addAktFileSize(len);

            // für die Anzeige prüfen ob sich was geändert hat
            if (aktSize != download.getDownloadSize().getActFileSize()) {
                aktSize = download.getDownloadSize().getActFileSize();
            }
            if (download.getDownloadSize().getFilmSize() > 0) {
                percent = 1.0 * aktSize / download.getDownloadSize().getFilmSize();
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
                        // sonst macht es noch keinen Sinn
//                        final int diffTime = download.getStart().getStartTime().diffInSeconds();
//                        final double restPercent = DownloadConstants.PROGRESS_FINISHED - percent;
//                        download.getStart().setTimeLeftSeconds((long) (diffTime * restPercent / (percent - startPercent)));

                        long timeLeft = 0;
                        long sizeLeft = download.getDownloadSize().getFilmSize() - download.getDownloadSize().getActFileSize();
                        if (sizeLeft <= 0) {
                            timeLeft = 0;
                        } else if (aktBandwidth > 0) {
                            timeLeft = sizeLeft / aktBandwidth;
                        }
                        download.getStart().setTimeLeftSeconds(timeLeft);

                        // anfangen zum Schauen kann man, wenn die Restzeit kürzer ist
                        // als die bereits geladene Speilzeit des Films
                        canAlreadyStarted(download);
                    }
                }
            }
            aktBandwidth = download.getStart().getInputStream().getBandwidth(); // bytes per second
            if (aktBandwidth != download.getStart().getBandwidth()) {
                download.getStart().setBandwidth(aktBandwidth);
            }
        }

        if (!download.isStateStoped()) {
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
        }
    }

    @Override
    public synchronized void run() {
        StarterClass.startMsg(download);

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
                    //If the server uses self-signed X.509 certificate, we will get SSLHandshakeException -> BR!!
                    //https://nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/
//                    // Create a trust manager that does not validate certificate chains
//                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                            return null;
//                        }
//
//                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                        }
//
//                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                        }
//                    }};
//                    // Install the all-trusting trust manager
//                    SSLContext sc = SSLContext.getInstance("SSL");
//                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
//                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


//                    if (ProgConfig.SYSTEM_SSL_ALWAYS_TRUE.getBool()) {
//                        // Create all-trusting host name verifier
//                        HostnameVerifier allHostsValid = (hostname, session) -> true;
//                        // Install the all-trusting host verifier
//                        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//                    }

                    download.getDownloadSize().setSize(getContentLength(url));
                    download.getDownloadSize().setActFileSize(0);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(1000 * ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getInt());
                    conn.setReadTimeout(1000 * ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getInt());

                    if (ProgConfig.SYSTEM_SSL_ALWAYS_TRUE.getBool() && conn instanceof HttpsURLConnection) {
                        HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                        httpsConn.setHostnameVerifier(
                                // Create all-trusting host name verifier
                                (hostname, session) -> true);
//                        httpsConn.setSSLSocketFactory(sc.getSocketFactory());
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
                if ((ex instanceof java.io.IOException)
                        && restartCount < ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getInt()) {

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
                    exMessage = ex.getMessage();
                    PLog.errorLog(316598941, ex, "Fehler");
                    if (download.getStart().getRestartCounter() == 0) {
                        // nur beim ersten Mal melden -> nervt sonst
                        Platform.runLater(() -> new DownloadErrorDialogController(download, exMessage));
                    }
                    download.setErrorMessage(exMessage);
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
    }

    private boolean cancelDownload() {
        if (!file.exists()) {
            // dann ist alles OK
            return false;
        }
        dialogBreakIsVis = true;
        retBreak = true;
        Platform.runLater(() -> {
            retBreak = break_();
            dialogBreakIsVis = false;
        });
        while (dialogBreakIsVis) {
            try {
                wait(100);
            } catch (final Exception ignored) {

            }
        }
        return retBreak;
    }

    private boolean break_() {
        boolean cancel = false;
        if (file.exists()) {
            DownloadContinueDialogController downloadContinueDialogController =
                    new DownloadContinueDialogController(ProgConfig.DOWNLOAD_DIALOG_CONTINUE_SIZE.getStringProperty(),
                            progData, download, true /* weiterführen */);

            DownloadState.ContinueDownload result = downloadContinueDialogController.getResult();
            boolean isNewName = downloadContinueDialogController.isNewName();
            downloadContinueDialogController = null;

            switch (result) {
                case CANCEL_DOWNLOAD:
                    // dann wars das
                    download.stopDownload();
                    cancel = true;
                    break;

                case CONTINUE_DOWNLOAD:
                    downloaded = file.length();
                    break;

                case RESTART_DOWNLOAD:
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

    private void canAlreadyStarted(Download dataDownload) {
        if (dataDownload.getFilm() != null && dataDownload.isStateStartedRun()) {

            if (dataDownload.getFilm().getDurationMinute() > 0
                    && dataDownload.getStart().getTimeLeftSeconds() > 0
                    && dataDownload.getDownloadSize().getActFileSize() > 0
                    && dataDownload.getDownloadSize().getFilmSize() > 0) {

                // macht nur dann Sinn
                final long filetimeAlreadyLoadedSeconds = dataDownload.getFilm().getDurationMinute() * 60
                        * dataDownload.getDownloadSize().getActFileSize()
                        / dataDownload.getDownloadSize().getFilmSize();

                if (filetimeAlreadyLoadedSeconds > (dataDownload.getStart().getTimeLeftSeconds() * 1.1 /* plus 10% zur Sicherheit */)) {
                    dataDownload.getStart().setStartViewing(true);
                }
            }
        }
    }
}
