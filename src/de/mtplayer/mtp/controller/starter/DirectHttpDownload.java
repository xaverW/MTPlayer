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

import de.mtplayer.mLib.tools.*;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.gui.dialog.DownloadContinueDialogController;
import de.mtplayer.mtp.gui.dialog.DownloadErrorDialogController;
import de.mtplayer.mtp.gui.tools.MTInfoFile;
import de.mtplayer.mtp.gui.tools.MTSubtitle;
import javafx.application.Platform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static de.mtplayer.mtp.controller.starter.StarterClass.pruefen;
import static de.mtplayer.mtp.controller.starter.StarterClass.startmeldung;

public class DirectHttpDownload extends Thread {

    private final Daten daten;
    private final Download download;
    private HttpURLConnection conn = null;
    private long downloaded = 0;
    private File file = null;
    private String responseCode;
    private String exMessage;

    private FileOutputStream fos = null;

    private final java.util.Timer bandwidthCalculationTimer;
    private boolean retAbbrechen;
    private boolean dialogAbbrechenIsVis;

    public DirectHttpDownload(Daten daten, Download d, java.util.Timer bandwidthCalculationTimer) {
        super();
        this.daten = daten;
        this.bandwidthCalculationTimer = bandwidthCalculationTimer;
        download = d;
        setName("DIRECT DL THREAD: " + d.getTitel());
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
            Log.errorLog(643298301, ex);
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
        if (download.isInfodatei()) {
            MTInfoFile.writeInfoFile(download);
        }
        if (download.isSubtitle()) {
            new MTSubtitle().writeSubtitle(download);
        }

        download.getStart().setInputStream(new MLInputStream(conn.getInputStream(),
                bandwidthCalculationTimer, Config.SYSTEM_DOWNLOAD_BANDBREITE_KBYTE.getIntegerProperty()));

        fos = new FileOutputStream(file, (downloaded != 0));

        download.getDownloadSize().addAktFileSize(downloaded);
        final byte[] buffer = new byte[MLBandwidthTokenBucket.DEFAULT_BUFFER_SIZE];
        double p, pp = DownloadInfos.PROGRESS_WARTEN, startProzent = DownloadInfos.PROGRESS_NICHT_GESTARTET;
        int len;
        long aktBandwidth, aktSize = 0;
        boolean melden = false;

        while ((len = download.getStart().getInputStream().read(buffer)) != -1 && (!download.isStateStoped())) {
            downloaded += len;
            fos.write(buffer, 0, len);
            download.getDownloadSize().addAktFileSize(len);

            // für die Anzeige prüfen ob sich was geändert hat
            if (aktSize != download.getDownloadSize().getAktFileSize()) {
                aktSize = download.getDownloadSize().getAktFileSize();
                melden = true;
            }
            if (download.getDownloadSize().getFilmSize() > 0) {
                p = 1.0 * aktSize / download.getDownloadSize().getFilmSize();
                if (startProzent == DownloadInfos.PROGRESS_NICHT_GESTARTET) {
                    startProzent = p;
                }
                // p muss zwischen 1 und 999 liegen
                if (p == DownloadInfos.PROGRESS_WARTEN) {
                    p = DownloadInfos.PROGRESS_GESTARTET;
                } else if (p >= DownloadInfos.PROGRESS_FERTIG) {
                    p = DownloadInfos.PROGRESS_FAST_FERTIG;
                }
                MLProperty.setProperty(download.progressProperty(), p);
                if (p != pp) {
                    pp = p;

                    // Restzeit ermitteln
                    if (p > (DownloadInfos.PROGRESS_GESTARTET) &&
                            p > startProzent) {
                        // sonst macht es noch keinen Sinn
                        final int diffZeit = download.getStart().getStartTime().diffInSekunden();
                        final double restProzent = DownloadInfos.PROGRESS_FERTIG - p;

                        download.getStart().setTimeLeft((long) (diffZeit * restProzent / (p - startProzent)));
                        // anfangen zum Schauen kann man, wenn die Restzeit kürzer ist
                        // als die bereits geladene Speilzeit des Films
                        bereitsAnschauen(download);
                    }

                    melden = true;
                }
            }
            aktBandwidth = download.getStart().getInputStream().getBandwidth(); // bytes per second
            if (aktBandwidth != download.getStart().getBandwidth()) {
                download.getStart().setBandwidth(aktBandwidth);
                melden = true;
            }
            if (melden) {
                melden = false;
            }
        }

        if (!download.isStateStoped()) {
            if (download.getSource().equals(DownloadInfos.SRC_BUTTON)) {
                // direkter Start mit dem Button
                download.setStateFinished();
            } else if (pruefen(daten, download)) {
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
        startmeldung(download);

        try {
            Files.createDirectories(Paths.get(download.getZielPfad()));
        } catch (final IOException ignored) {
        }

        int restartCount = 0;
        boolean restart = true;
        while (restart) {
            restart = false;
            try {
                final URL url = new URL(download.getUrl());
                file = new File(download.getZielPfadDatei());

                if (!cancelDownload()) {

                    download.getDownloadSize().setSize(getContentLength(url));
                    download.getDownloadSize().setAktFileSize(0);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(1000 * Config.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SEKUNDEN.getInt());
                    conn.setReadTimeout(1000 * Config.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SEKUNDEN.getInt());

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
                            responseCode = "Responsecode: " + conn.getResponseCode() + '\n' + conn.getResponseMessage();
                            Log.errorLog(915236798, "HTTP-Fehler: " + conn.getResponseCode() + ' ' + conn.getResponseMessage());
                            if (download.getStart().getRestartCounter() == 0) {
                                // nur beim ersten Mal melden -> nervt sonst
                                Platform.runLater(() -> new DownloadErrorDialogController(download, responseCode));
                            }
                            download.setStateError();
                        }
                    }
                }
                if (download.isStateStartedRun()) {
                    downloadContent();
                }
            } catch (final Exception ex) {
                if ((ex instanceof java.io.IOException)
                        && restartCount < Config.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getInt()) {

                    if (ex instanceof java.net.SocketTimeoutException) {
                        // Timeout Fehlermeldung für zxd :)
                        final ArrayList<String> text = new ArrayList<>();
                        text.add("Timeout, Download Restarts: " + restartCount);
                        text.add("Ziel: " + download.getZielPfadDatei());
                        text.add("URL: " + download.getUrl());
                        SysMsg.sysMsg(text.toArray(new String[text.size()]));
                    }

                    restartCount++;
                    restart = true;
                } else {
                    // dann weiß der Geier!
                    exMessage = ex.getMessage();
                    Log.errorLog(316598941, ex, "Fehler");
                    download.setStateError();
                    if (download.getStart().getRestartCounter() == 0) {
                        // nur beim ersten Mal melden -> nervt sonst
                        Platform.runLater(() -> new DownloadErrorDialogController(download, exMessage));
                    }
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
        dialogAbbrechenIsVis = true;
        retAbbrechen = true;
        Platform.runLater(() -> {
            retAbbrechen = abbrechen_();
            dialogAbbrechenIsVis = false;
        });
        while (dialogAbbrechenIsVis) {
            try {
                wait(100);
            } catch (final Exception ignored) {

            }
        }
        return retAbbrechen;
    }

    private boolean abbrechen_() {
        boolean cancel = false;
        if (file.exists()) {
            final DownloadContinueDialogController downloadContinueDialogController =
                    new DownloadContinueDialogController(daten, download, true /* weiterführen */);

            switch (downloadContinueDialogController.getResult()) {
                case CANCEL_DOWNLOAD:
                    // dann wars das
                    download.stopDownload();
                    cancel = true;
                    break;

                case CONTINUE_DOWNLOAD:
                    downloaded = file.length();
                    break;

                case RESTART_DOWNLOAD:
                    if (!downloadContinueDialogController.isNewName()) {
                        // dann mit gleichem Namen und Datei vorher löschen
                        try {
                            Files.deleteIfExists(file.toPath());
                            file = new File(download.getZielPfadDatei());
                        } catch (final Exception ex) {
                            // kann nicht gelöscht werden, evtl. klappt ja das Überschreiben
                            Log.errorLog(915263654, ex,
                                    "file exists: " + download.getZielPfadDatei());
                        }
                    } else {
                        // dann mit neuem Namen
                        try {
                            Files.createDirectories(Paths.get(download.getZielPfad()));
                        } catch (final IOException ignored) {
                        }
                        file = new File(download.getZielPfadDatei());
                    }
                    break;
            }
        }
        return cancel;
    }

    private void bereitsAnschauen(Download datenDownload) {
        if (datenDownload.getFilm() != null && datenDownload.isStateStartedRun()) {
            if (datenDownload.getFilm().dauerL > 0 && datenDownload.getStart().getTimeLeft() > 0
                    && datenDownload.getDownloadSize().getAktFileSize() > 0
                    && datenDownload.getDownloadSize().getFilmSize() > 0) {
                // macht nur dann Sinn
                final long zeitGeladen =
                        datenDownload.getFilm().dauerL * datenDownload.getDownloadSize().getAktFileSize() / datenDownload.getDownloadSize().getFilmSize();
                if (zeitGeladen > (datenDownload.getStart().getTimeLeft() * 1.1 /* plus 10% zur Sicherheit */)) {
                    datenDownload.getStart().setStartViewing(true);
                }
            }
        }
    }
}
