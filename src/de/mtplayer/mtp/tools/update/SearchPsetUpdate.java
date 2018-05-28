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
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ListePsetVorlagen;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.gui.dialog.NewSetDialogController;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.util.Date;

public class SearchPsetUpdate {
    private final ProgData progData;

    public SearchPsetUpdate(ProgData progData) {
        this.progData = progData;
    }

    public void checkForPsetUpdates() {
        try {
            Platform.runLater(() -> {

                final SetList listPsetStandard = ListePsetVorlagen.getStandarset(false /* replaceMuster */);
                final String version = ProgConfig.SYSTEM_UPDATE_PROGSET_VERSION.get();
                if (listPsetStandard == null) {
                    return;
                }

                if (!progData.setList.isEmpty()) {
                    // ansonsten ist die Liste leer und dann gibts immer was
                    if (listPsetStandard.version.isEmpty()) {
                        // dann hat das Laden der aktuellen Standardversion nicht geklappt
                        return;
                    }

                    if (version.equals(listPsetStandard.version)) {
                        // dann passt alles
                        return;
                    }

                    //todo PLog zusammenfassen
                    final NewSetDialogController newSetDialogController = new NewSetDialogController(progData);
                    if (newSetDialogController.getReplaceSet()) {
                        // dann werden die Sets durch die Neuen ersetzt
                        progData.setList.clear();
                    } else if (!newSetDialogController.getAddNewSet()) {
                        // und wenn auch nicht "Anfügen" gewählt, dann halt nix
                        PLog.userLog("Setanlegen: Abbruch");
                        if (!newSetDialogController.getAskAgain()) {
                            // dann auch die Versionsnummer aktualisieren
                            PLog.userLog("Setanlegen: Nicht wieder nachfragen");
                            ProgConfig.SYSTEM_UPDATE_PROGSET_VERSION.setValue(listPsetStandard.version);
                        }
                        PLog.userLog("==========================================");
                        return;
                    }
                }


                // ========================================
                // gibt keine Sets oder aktualisieren
                // damit die Variablen ersetzt werden
                SetList.progReplacePattern(listPsetStandard);
                ProgConfig.SYSTEM_UPDATE_PROGSET_VERSION.setValue(listPsetStandard.version);

                // die Zielpafade anpassen
                final SetList listPsetOrgSave = progData.setList.getListSave();

                if (!listPsetOrgSave.isEmpty()) {
                    for (final SetData psNew : listPsetStandard.getListSave()) {
                        psNew.setDestPath(listPsetOrgSave.get(0).getDestPath());
                        psNew.setGenTheme(listPsetOrgSave.get(0).getGenTheme());
                        psNew.setMaxSize(listPsetOrgSave.get(0).getMaxSize());
                        psNew.setMaxField(listPsetOrgSave.get(0).getMaxField());
                    }
                }

                if (!progData.setList.isEmpty()) {
                    // wenn leer, dann gibts immer die neuen und die sind dann auch aktiv
                    for (final SetData psNew : listPsetStandard) {
                        // die bestehenden Sets sollen nicht gestört werden
                        psNew.setPlay(false);
                        psNew.setAbo(false);
                        psNew.setButton(false);
                        psNew.setSave(false);
                    }

                    // damit man sie auch findet :)
                    final String date = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());
                    listPsetStandard.forEach((psNew) -> psNew.setName(psNew.getName() + ", neu: " + date));

                }

                SetsPrograms.addSetTemplate(listPsetStandard); // damit auch AddOns
                // geladen werden
                PLog.userLog("Setanlegen: OK");
                PLog.userLog("==========================================");
            });

        } catch (final Exception ignored) {
        }
    }
}
