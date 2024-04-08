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

import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.mtdownload.MLBandwidthTokenBucket;
import de.p2tools.p2lib.mtdownload.MLInputStream;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.net.ssl.HttpsURLConnection;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadDirectHttp extends Thread {

    /**
     * HTTP Timeout in milliseconds.
     */
    private static final int TIMEOUT_LENGTH = 5000;
    private final ProgData progData;
    private final DownloadData download;
    private final java.util.Timer bandwidthCalculationTimer;
    private long fileSizeLoaded = 0;
    private double percentOld = DownloadConstants.PROGRESS_WAITING;
    private double startPercent = DownloadConstants.PROGRESS_NOT_STARTED;
    private HttpURLConnection httpURLConn = null;
    private boolean updateDownloadInfos = false;

    private final PListener listener = new PListener(PListener.EVENT_TIMER_HALF_SECOND, DownloadDirectHttp.class.getSimpleName()) {
        @Override
        public void ping() {
            updateDownloadInfos = true;
        }
    };
    private FileOutputStream fos = null;

    public DownloadDirectHttp(ProgData progData, DownloadData d, java.util.Timer bandwidthCalculationTimer) {
        super();
        this.progData = progData;
        this.bandwidthCalculationTimer = bandwidthCalculationTimer;
        download = d;
        setName("DIRECT DL THREAD: " + d.getTitle());
        download.setStateStartedRun();
        PListener.addListener(listener);
    }

    @Override
    public synchronized void run() {
        LogMsgFactory.startMsg(download);
        StartDownloadFactory.makeDirAndLoadInfoSubtitle(download);
        runWhile();
        StartDownloadFactory.finalizeDownload(download);
        PListener.removeListener(listener);
    }

    private void runWhile() {
        BooleanProperty restartWithOutSSL = new SimpleBooleanProperty(false);
        boolean restart = true;

        while (restart) {
            restart = false;

            try {
                if (!new CheckDownloadFileExists().checkIfContinue(progData, download, true)) {
                    // dann abbrechen
                    closeConn();
                    return;
                }
                download.getDownloadStartDto().addStartCounter();
                openConnForDownload(restartWithOutSSL);
                if (download.isStateStartedRun()) {
                    // dann passts, und der Download startet
                    downloadContent();
                }
            } catch (final Exception ex) {
                //Probleme über Probleme
                P2Log.errorLog(316598941, ex, "Fehler");
                if (ex instanceof javax.net.ssl.SSLHandshakeException) {
                    //dann gabs Probleme bei https
                    if (download.getDownloadStartDto().getStartCounter() == 1 && !restartWithOutSSL.getValue()) {
                        //nur beim ersten mal fragen, ob beim neuen Versuch ohne SSL
                        restartWithOutSSL.setValue(restartHttps());
                    }
                    download.setStateError("DownloadFehler beim SSLHandshake: " + ex.getLocalizedMessage());

                } else if (ex instanceof java.net.SocketTimeoutException) {
                    download.setStateError("DownloadFehler, Timeout: " + ex.getLocalizedMessage());
                } else {
                    download.setStateError("DownloadFehler: " + ex.getLocalizedMessage());
                }
            }
            if (download.isStateError() &&
                    download.getDownloadStartDto().getStartCounter() <
                            StartDownloadFactory.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART) {
                // dann nochmal starten
                restart = true;
                download.setStateStartedRun();
                try {
                    // vor den nächsten Versuchen etwas warten
                    sleep(2_000);
                } catch (Exception ignored) {
                }
            }
        }
        closeConn();
    }

    private void closeConn() {
        try {
            if (download.getDownloadStartDto().getInputStream() != null) {
                download.getDownloadStartDto().getInputStream().close();
            }
            if (fos != null) {
                fos.close();
            }
            if (httpURLConn != null) {
                httpURLConn.disconnect();
            }
        } catch (final Exception ignored) {
        }
    }

    private void openConnForDownload(BooleanProperty restartWithOutSSL) throws IOException {
        final URL url = new URL(download.getUrl());
        download.getDownloadSize().setTargetSize(getContentLength(url));
        download.getDownloadSize().setActuallySize(0);

        httpURLConn = ProxyFactory.getUrlConnection(url);

        if ((restartWithOutSSL.getValue() || ProgConfig.SYSTEM_SSL_ALWAYS_TRUE.getValue())
                && httpURLConn instanceof HttpsURLConnection) {
            // dann die Verbindung ohne https aufmachen
            HttpsURLConnection httpsConn = (HttpsURLConnection) httpURLConn;
            httpsConn.setHostnameVerifier((hostname, session) -> true);
        }

        setupHttpConnection(httpURLConn);
        httpURLConn.connect();
        final int httpResponseCode = httpURLConn.getResponseCode();
        if (httpResponseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
            // Range passt nicht, also neue Verbindung versuchen...
            String responseCode = "ResponseCode: " + httpResponseCode + P2LibConst.LINE_SEPARATOR + httpURLConn.getResponseMessage();
            P2Log.errorLog(915235789, responseCode);

            if (httpResponseCode == 416) {
                // 416 = Range Not Satisfiable
                httpURLConn.disconnect();
                // dann nochmal mit Start von Anfang an versuchen
//                httpURLConn = (HttpURLConnection) url.openConnection();
                httpURLConn = ProxyFactory.getUrlConnection(url);

                download.getDownloadStartDto().setDownloaded(0);
                setupHttpConnection(httpURLConn);
                httpURLConn.connect();
                // hier wars es dann nun wirklich...
                if (httpURLConn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    download.setStateError("DownloadFehler, httpResponseCode 416: Der angeforderte Teil einer " +
                            "Ressource war ungültig oder steht auf dem Server nicht zur Verfügung.");
                }

            } else {
                // dann wars das
                if (httpResponseCode == 404) {
                    download.setStateError("Die URL wurde nicht gefunden.");
                } else {
                    download.setStateError("DownloadFehler, httpResponseCode " + httpResponseCode +
                            ": Der angeforderte Teil einer Ressource konnte nicht geladen werden.");
                }
            }
        }
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
            // connection = (HttpURLConnection) url.openConnection();
            connection = ProxyFactory.getUrlConnection(url);
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
            P2Log.errorLog(643298301, ex);
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
        conn.setRequestProperty("Range", "bytes=" + download.getDownloadStartDto().getDownloaded() + '-');
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
        download.getDownloadStartDto().setInputStream(new MLInputStream(httpURLConn.getInputStream(),
                bandwidthCalculationTimer,
                ProgConfig.DOWNLOAD_MAX_BANDWIDTH_BYTE,
                ProgData.FILMLIST_IS_DOWNLOADING));

        fos = new FileOutputStream(download.getDownloadStartDto().getFile(), (download.getDownloadStartDto().getDownloaded() != 0));
        download.getDownloadSize().setActuallySize(download.getDownloadStartDto().getDownloaded());
        final byte[] buffer = new byte[MLBandwidthTokenBucket.DEFAULT_BUFFER_SIZE];
        int len;

        while ((len = download.getDownloadStartDto().getInputStream().read(buffer)) != -1 && (!download.isStateStopped())) {
            download.getDownloadStartDto().setDownloaded(download.getDownloadStartDto().getDownloaded() + len);
            fos.write(buffer, 0, len);

            // die Infos zum Download aktualisieren
            if (!updateDownloadInfos) {
                continue;
            }
            updateDownloadInfos = false;
            setDownloadProgress(false);
        }
        setDownloadProgress(true);

        if (!download.isStateStopped()) {
            StringProperty strErrorMsg = new SimpleStringProperty();
            if (download.getSource().equals(DownloadConstants.SRC_BUTTON)) {
                // direkter Start mit dem Button
                download.setStateFinished();

            } else if (StartDownloadFactory.checkDownloadWasOK(progData, download, strErrorMsg)) {
                // prüfen und Anzeige ändern - fertig
                download.setStateFinished();

            } else {
                // Anzeige ändern - bei Fehler fehlt der Eintrag
                download.setStateError(strErrorMsg.getValueSafe());
            }
        } else {
            download.stopDownload(); // nochmal da RUNTIME_EXEC ja weiter läuft
        }
    }

    private void setDownloadProgress(boolean withExactFileSize) {
        // hier wird die Anzeige des Fortschritts in der Tabelle Downloads gemacht
        if (withExactFileSize) {
            // dann die tatsächliche Dateigröße ermitteln
            if (download.getFile().exists()) {
                download.getDownloadSize().setActuallySize(download.getFile().length());
            } else {
                download.getDownloadSize().setActuallySize(0);
            }

        } else {
            // schnell nur das was geladen wurde nehmen
            download.getDownloadSize().setActuallySize(download.getDownloadStartDto().getDownloaded());
        }

        if (fileSizeLoaded == download.getDownloadSize().getActuallySize()) {
            // für die Anzeige prüfen ob sich was geändert hat
            return;
        }

        long aktBandwidth = download.getDownloadStartDto().getInputStream().getBandwidth(); // bytes per second
        if (aktBandwidth != download.getBandwidth()) {
            download.setBandwidth(aktBandwidth);
        }

        fileSizeLoaded = download.getDownloadSize().getActuallySize();
        long fileTargetSize = download.getDownloadSize().getTargetSize();
        if (fileTargetSize > 0) {
            double percentActProgress = 1.0 * fileSizeLoaded / fileTargetSize;
            if (startPercent == DownloadConstants.PROGRESS_NOT_STARTED) {
                startPercent = percentActProgress;
            }

            // percent muss zwischen 0 und 1 liegen
            if (percentActProgress == DownloadConstants.PROGRESS_WAITING) {
                percentActProgress = DownloadConstants.PROGRESS_STARTED;
            } else if (percentActProgress >= DownloadConstants.PROGRESS_FINISHED) {
                percentActProgress = DownloadConstants.PROGRESS_NEARLY_FINISHED;
            }
            download.setProgress(percentActProgress);

            if (percentActProgress != percentOld) {
                // dann hat sich die percent geändert und muss neu berechnet werden
                percentOld = percentActProgress;

                // Restzeit ermitteln
                if (percentActProgress > DownloadConstants.PROGRESS_STARTED &&
                        percentActProgress > startPercent) {
                    long timeLeft = 0;
                    long sizeLeft = fileTargetSize - fileSizeLoaded;
                    if (sizeLeft > 0 && aktBandwidth > 0) {
                        timeLeft = sizeLeft / aktBandwidth;
                    }
                    download.getDownloadStartDto().setTimeLeftSeconds((int) timeLeft);

                    // anfangen zum Schauen kann man, wenn die Restzeit kürzer ist
                    // als die bereits geladene Spielzeit des Films
                    StartDownloadFactory.canAlreadyStarted(download);
                }
            }
        }
    }

    private boolean restartHttps() {
        final ArrayList<String> text = new ArrayList<>();
        text.add("Ziel: " + download.getDestPathFile());
        text.add("URL: " + download.getUrl());
        P2Log.sysLog(text.toArray(new String[0]));

        AtomicBoolean dialog = new AtomicBoolean(true);
        AtomicBoolean ret = new AtomicBoolean(false);
        Platform.runLater(() -> {
            ret.set(P2Alert.showAlertOkCancel("HTTPS",
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
                wait(200);
            } catch (final Exception ignored) {
            }
        }
        return ret.get();
    }
}
