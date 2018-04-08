/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.tools.update;

import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.ListePsetVorlagen;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.gui.dialog.NewSetDialogController;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.util.Date;

public class SearchPsetUpdate {
    private final Daten daten;

    public SearchPsetUpdate(Daten daten) {
        this.daten = daten;
    }

    public void checkForPsetUpdates() {
        try {
            Platform.runLater(() -> {

                final SetList listePsetStandard = ListePsetVorlagen.getStandarset(false /* replaceMuster */);
                final String version = Config.SYSTEM_UPDATE_PROGSET_VERSION.get();
                if (listePsetStandard == null) {
                    return;
                }

                if (!daten.setList.isEmpty()) {
                    // ansonsten ist die Liste leer und dann gibts immer was
                    if (listePsetStandard.version.isEmpty()) {
                        // dann hat das Laden der aktuellen Standardversion nicht geklappt
                        return;
                    }

                    if (version.equals(listePsetStandard.version)) {
                        // dann passt alles
                        return;
                    }

                    //todo PLog zusammenfassen
                    final NewSetDialogController newSetDialogController = new NewSetDialogController(daten);
                    if (newSetDialogController.getReplaceSet()) {
                        // dann werden die Sets durch die Neuen ersetzt
                        daten.setList.clear();
                    } else if (!newSetDialogController.getAddNewSet()) {
                        // und wenn auch nicht "Anfügen" gewählt, dann halt nix
                        PLog.userLog("Setanlegen: Abbruch");
                        if (!newSetDialogController.getAskAgain()) {
                            // dann auch die Versionsnummer aktualisieren
                            PLog.userLog("Setanlegen: Nicht wieder nachfragen");
                            Config.SYSTEM_UPDATE_PROGSET_VERSION.setValue(listePsetStandard.version);
                        }
                        PLog.userLog("==========================================");
                        return;
                    }
                }


                // ========================================
                // gibt keine Sets oder aktualisieren
                // damit die Variablen ersetzt werden
                SetList.progMusterErsetzen(listePsetStandard);
                Config.SYSTEM_UPDATE_PROGSET_VERSION.setValue(listePsetStandard.version);

                // die Zielpafade anpassen
                final SetList listePsetOrgSpeichern = daten.setList.getListeSpeichern();

                if (!listePsetOrgSpeichern.isEmpty()) {
                    for (final SetData psNew : listePsetStandard.getListeSpeichern()) {
                        psNew.setDestPath(listePsetOrgSpeichern.get(0).getDestPath());
                        psNew.setGenThema(listePsetOrgSpeichern.get(0).isGenThema());
                        psNew.setMaxSize(listePsetOrgSpeichern.get(0).getMaxSize());
                        psNew.setMaxField(listePsetOrgSpeichern.get(0).getMaxField());
                    }
                }

                if (!daten.setList.isEmpty()) {
                    // wenn leer, dann gibts immer die neuen und die sind dann auch aktiv
                    for (final SetData psNew : listePsetStandard) {
                        // die bestehenden Sets sollen nicht gestört werden
                        psNew.setPlay(false);
                        psNew.setAbo(false);
                        psNew.setButton(false);
                        psNew.setSave(false);
                    }

                    // damit man sie auch findet :)
                    final String date = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());
                    listePsetStandard.forEach((psNew) -> psNew.setName(psNew.getName() + ", neu: " + date));

                }

                SetsPrograms.addSetVorlagen(listePsetStandard); // damit auch AddOns
                // geladen werden
                PLog.userLog("Setanlegen: OK");
                PLog.userLog("==========================================");
            });

        } catch (final Exception ignored) {
        }
    }
}
