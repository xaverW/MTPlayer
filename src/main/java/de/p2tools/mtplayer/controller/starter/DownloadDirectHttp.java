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
import de.p2tools.mtplayer.gui.tools.MTListener;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.mtdownload.MLBandwidthTokenBucket;
import de.p2tools.p2lib.mtdownload.MLInputStream;
import de.p2tools.p2lib.tools.log.PLog;
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
    private long aktBandwidth = 0, aktSize = 0;
    private double percent, ppercent = DownloadConstants.PROGRESS_WAITING,
            startPercent = DownloadConstants.PROGRESS_NOT_STARTED;
    private HttpURLConnection conn = null;
    private boolean work = false;

    private final MTListener listener = new MTListener(MTListener.EVENT_TIMER_HALF_SECOND, DownloadDirectHttp.class.getSimpleName()) {
        @Override
        public void ping() {
            work = true;
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
        MTListener.addListener(listener);
    }

    @Override
    public synchronized void run() {
        LogDownloadFactory.startMsg(download);
        StartDownloadFactory.makeDirAndLoadInfoSubtitle(download);

        runWhile();

        try {
            if (download.getDownloadStartDto().getInputStream() != null) {
                download.getDownloadStartDto().getInputStream().close();
            }
            if (fos != null) {
                fos.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        } catch (final Exception ignored) {
        }

        System.out.println("=========> ein Start fertig");
        StartDownloadFactory.finalizeDownload(download);
        MTListener.removeListener(listener);
    }

    private void runWhile() {
        BooleanProperty restartWithOutSSL = new SimpleBooleanProperty(false);
        boolean restart = true;

        download.getDownloadStartDto().setStartCounter(1);
        while (restart) {
            restart = false;

            try {
                if (!new CheckDownloadFileExists().checkIfContinue(progData, download, true)) {
                    // dann abbrechen
                    StartDownloadFactory.finalizeDownload(download);
                    return;
                }

                openConnForDownload(restartWithOutSSL);
                if (download.isStateStartedRun()) {
                    // dann passts, und der Download startet
                    downloadContent();
                }
            } catch (final Exception ex) {
                //Probleme über Probleme
                PLog.errorLog(316598941, ex, "Fehler");
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
                    download.getDownloadStartDto().getStartCounter() <= StartDownloadFactory.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP) {
                // dann nochmal starten
                download.getDownloadStartDto().addStartCounter();
                restart = true;
                try {
                    // vor den nächsten Versuchen etwas warten
                    sleep(2_000);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void openConnForDownload(BooleanProperty restartWithOutSSL) throws IOException {
        final URL url = new URL(download.getUrl());
        download.getDownloadSize().setSize(getContentLength(url));
        download.getDownloadSize().setActFileSize(0);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(1000 * ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue());
        conn.setReadTimeout(1000 * ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue());

        if ((restartWithOutSSL.getValue() || ProgConfig.SYSTEM_SSL_ALWAYS_TRUE.getValue())
                && conn instanceof HttpsURLConnection) {
            // dann die Verbindung ohne https aufmachen
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setHostnameVerifier((hostname, session) -> true);
        }

        setupHttpConnection(conn);
        conn.connect();
        final int httpResponseCode = conn.getResponseCode();
        if (httpResponseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
            // Range passt nicht, also neue Verbindung versuchen...
            String responseCode = "Responsecode: " + httpResponseCode + P2LibConst.LINE_SEPARATOR + conn.getResponseMessage();
            PLog.errorLog(915235789, responseCode);

            if (httpResponseCode == 416) {
                conn.disconnect();
                // Get a new connection and reset download param...
                conn = (HttpURLConnection) url.openConnection();
                download.getDownloadStartDto().setDownloaded(0);
                setupHttpConnection(conn);
                conn.connect();
                // hier war es dann nun wirklich...
                if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    download.setStateError("DownloadFehler, httpResponseCode 416: Der angeforderte Teil einer " +
                            "Ressource war ungültig oder steht auf dem Server nicht zur Verfügung.");
                }

            } else {
                // dann wars das
                download.setStateError("DownloadFehler, httpResponseCode " + httpResponseCode +
                        ": Der angeforderte Teil einer Ressource konnte nicht geladen werden.");
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
        download.getDownloadStartDto().setInputStream(new MLInputStream(conn.getInputStream(),
                bandwidthCalculationTimer,
                ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE,
                ProgData.FILMLIST_IS_DOWNLOADING));

        fos = new FileOutputStream(download.getDownloadStartDto().getFile(), (download.getDownloadStartDto().getDownloaded() != 0));
        download.getDownloadSize().setActFileSize(download.getDownloadStartDto().getDownloaded());
        final byte[] buffer = new byte[MLBandwidthTokenBucket.DEFAULT_BUFFER_SIZE];
        int len;


        while ((len = download.getDownloadStartDto().getInputStream().read(buffer)) != -1 && (!download.isStateStopped())) {
            download.getDownloadStartDto().setDownloaded(download.getDownloadStartDto().getDownloaded() + len);
            fos.write(buffer, 0, len);

            if (!work) {
                continue;
            }
            work = false;
            setDownloaded();
        }
        setDownloaded();

        if (!download.isStateStopped()) {
            StringProperty strErrorMsg = new SimpleStringProperty();
            if (download.getSource().equals(DownloadConstants.SRC_BUTTON)) {
                // direkter Start mit dem Button
                download.setStateFinished();
            } else if (StartDownloadFactory.checkDownloadWasOK(progData, download, strErrorMsg)) {
                // Anzeige ändern - fertig
                download.setStateFinished();
            } else {
                // Anzeige ändern - bei Fehler fehlt der Eintrag
                download.setStateError(strErrorMsg.getValueSafe());
            }
        } else {
            download.stopDownload(); // nochmal da RUNTIME_EXEC ja weiter läuft
        }
    }

    private void setDownloaded() {
        download.getDownloadSize().setActFileSize(download.getDownloadStartDto().getDownloaded());

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
                    download.getDownloadStartDto().setTimeLeftSeconds((int) timeLeft);

                    // anfangen zum Schauen kann man, wenn die Restzeit kürzer ist
                    // als die bereits geladene Spielzeit des Films
                    canAlreadyStarted(download);
                }
            }
        }
        aktBandwidth = download.getDownloadStartDto().getInputStream().getBandwidth(); // bytes per second
        if (aktBandwidth != download.getDownloadStartDto().getBandwidth()) {
            download.getDownloadStartDto().setBandwidth(aktBandwidth);
        }
    }

    private boolean restartHttps() {
        final ArrayList<String> text = new ArrayList<>();
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

    private void canAlreadyStarted(DownloadData downloadData) {
        if (downloadData.isStateStartedRun()) {

            if (downloadData.getDurationMinute() > 0
                    && downloadData.getDownloadStartDto().getTimeLeftSeconds() > 0
                    && downloadData.getDownloadSize().getActFileSize() > 0
                    && downloadData.getDownloadSize().getSize() > 0) {

                // macht nur dann Sinn
                final long filetimeAlreadyLoadedSeconds = downloadData.getDurationMinute() * 60
                        * downloadData.getDownloadSize().getActFileSize()
                        / downloadData.getDownloadSize().getSize();

                if (filetimeAlreadyLoadedSeconds > (downloadData.getDownloadStartDto().getTimeLeftSeconds() * 1.1 /* plus 10% zur Sicherheit */)) {
                    downloadData.getDownloadStartDto().setStartViewing(true);
                }
            }
        }
    }
}
