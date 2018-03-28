/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

package de.mtplayer.mtp.tools.update;

import de.mtplayer.mLib.tools.Functions;
import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import javafx.application.Platform;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class ProgrammUpdateSuchen {

    private static final String UPDATE_SEARCH_TITLE = "Software-Aktualisierung";
    private static final String UPDATE_ERROR_MESSAGE =
            "Es ist ein Fehler bei der Softwareaktualisierung aufgetreten.\n"
                    + "Die aktuelle Version konnte nicht ermittelt werden.";

    private static final int UPDATE_RETRIEVE_PROGRAM_INFORMATION = 1;
    private static final int UPDATE_CHECK_FOR_NEWER_VERSION = 2;
    private static final int UPDATE_SHOW_PROGRAM_INFORMATION = 3;
    /**
     * Connection timeout in milliseconds.
     */
    private static final int TIMEOUT = 10_000;
    private final ArrayList<String[]> listInfos = new ArrayList<>();
    private boolean neueVersion = false;

    public boolean checkVersion(boolean showError, boolean anzeigen, boolean showProgramInformation, boolean showAllInformation) {
        // prüft auf neue Version, aneigen: wenn true, dann AUCH wenn es keine neue Version gibt ein
        // Fenster
        neueVersion = false;

        final Optional<ServerProgramInformation> opt = retrieveProgramInformation();
        if (!opt.isPresent() && showError) {
            Platform.runLater(() ->
                    new MTAlert().showErrorAlert("Fehler", UPDATE_SEARCH_TITLE, UPDATE_ERROR_MESSAGE));
        } else {
            // Update-Info anzeigen
            final ServerProgramInformation progInfo = opt.get();
            if (showProgramInformation) {
                showProgramInformation(showAllInformation);
            }

            if (progInfo.getVersion() < 0) {
                // dann konnte die "Version" im xml nicht geparst werden
                Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler", UPDATE_SEARCH_TITLE, UPDATE_ERROR_MESSAGE));
            } else {
                Config.SYSTEM_BUILD_NR.setValue(Functions.getProgVersion());
                Config.SYSTEM_UPDATE_DATE.setValue(StringFormatters.FORMATTER_yyyyMMdd.format(new Date()));

                if (progInfo.getVersion() > Functions.getProgVersionInt()) {
                    neueVersion = true;
                    displayNotification(progInfo);
                } else if (anzeigen) {
                    Platform.runLater(() -> new MTAlert().showInfoAlert("Programmversion", UPDATE_SEARCH_TITLE,
                            "Sie benutzen die neueste Version von MTPlayer."));
                }
            }
        }

        return neueVersion;
    }

    private void displayNotification(ServerProgramInformation progInfo) {
        final StringBuilder text = new StringBuilder();
        text.append("" +
                "=========================================\n"
                + "Neue Version:\n"
                + progInfo.getVersion() + ""
                + "\n\n"
                + "=========================================\n"
                + "Änderungen:\n"
                + progInfo.getReleaseNotes()
                + "\n\n"
                + "=========================================\n"
                + "URL:\n"
                + progInfo.getUpdateUrl()
                + "\n\n\n\n"
                + "=========================================\n"
                + "(Diese Meldung kann man in den Einstellungen abschalten.)\n\n");

        Platform.runLater(() -> new MTAlert().showInfoAlert("Programminfos", "Neue Version verfügbar", text.toString()));

    }

    private void showProgramInformation(boolean showAll) {
        if (listInfos.isEmpty()) {
            // no info available
            if (showAll) {
                Platform.runLater(() -> new MTAlert().showInfoAlert("Programmversion", UPDATE_SEARCH_TITLE,
                        "Es liegen keine Programminfos vor."));
            }
        } else {
            // display available info...
            try {
                final StringBuilder text = new StringBuilder();
                int angezeigt = 0;
                if (Config.SYSTEM_INFO_NR_SHOWN.get().isEmpty()) {
                    Config.SYSTEM_INFO_NR_SHOWN.setValue(Integer.toString(-1));
                } else {
                    angezeigt = Integer.parseInt(Config.SYSTEM_INFO_NR_SHOWN.get());
                }
                int index = 0;
                for (final String[] h : listInfos) {
                    index = Integer.parseInt(h[0]);
                    if (showAll || angezeigt < index) {
                        text.append("=======================================\n");
                        text.append(h[1]);
                        text.append('\n');
                        text.append('\n');
                    }
                }
                if (text.length() > 0) {
                    Platform.runLater(() -> new MTAlert()
                            .showInfoAlert("Programminfos", "Neue Infos zum Programm", text.toString()));
                    Config.SYSTEM_INFO_NR_SHOWN.setValue(Integer.toString(index));
                }
            } catch (final Exception ex) {
                Log.errorLog(UPDATE_SHOW_PROGRAM_INFORMATION, ex);
            }
        }
    }

    private InputStream connectToServer() throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) new URL(Const.ADRESSE_MTPLAYER_VERSION).openConnection();
        conn.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);

        return conn.getInputStream();
    }

    /**
     * Load and parse the update information.
     *
     * @return parsed update info for further use when successful
     */
    private Optional<ServerProgramInformation> retrieveProgramInformation() {
        int event;
        XMLStreamReader parser = null;
        ServerProgramInformation progInfo;

        final XMLInputFactory inFactory = XMLInputFactory.newInstance();
        inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);

        try (InputStreamReader inReader = new InputStreamReader(connectToServer(), StandardCharsets.UTF_8)) {
            parser = inFactory.createXMLStreamReader(inReader);
            progInfo = new ServerProgramInformation();

            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    switch (parser.getLocalName()) {
                        case ServerProgramInformation.ParserTags.VERSION:
                            progInfo.setVersion(parser.getElementText());
                            break;
                        case ServerProgramInformation.ParserTags.RELEASE_NOTES:
                            progInfo.setReleaseNotes(parser.getElementText());
                            break;
                        case ServerProgramInformation.ParserTags.UPDATE_URL:
                            progInfo.setUpdateUrl(parser.getElementText());
                            break;
                        case ServerProgramInformation.ParserTags.INFO:
                            final int count = parser.getAttributeCount();
                            String nummer = "";
                            for (int i = 0; i < count; ++i) {
                                if (parser.getAttributeName(i).toString().equals(ServerProgramInformation.ParserTags.INFO_NO)) {
                                    nummer = parser.getAttributeValue(i);
                                }
                            }
                            final String info = parser.getElementText();
                            if (!nummer.isEmpty() && !info.isEmpty()) {
                                listInfos.add(new String[]{nummer, info});
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            return Optional.of(progInfo);
        } catch (final Exception ex) {
            Log.errorLog(UPDATE_RETRIEVE_PROGRAM_INFORMATION, ex);
            return Optional.empty();
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
            } catch (final Exception ignored) {
            }
        }
    }
}
