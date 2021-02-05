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


package de.p2tools.mtplayer.tools.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ListePsetVorlagen;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.SetDataList;
import de.p2tools.mtplayer.gui.dialog.NewSetDialogController;
import de.p2tools.mtplayer.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.tools.PIndex;
import de.p2tools.p2Lib.tools.date.PDateFactory;
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

                final SetDataList listPsetStandard = ListePsetVorlagen.getStandarset(false /* replaceMuster */);
                final String version = ProgConfig.SYSTEM_UPDATE_PROGSET_VERSION.get();
                if (listPsetStandard == null) {
                    return;
                }

                if (!progData.setDataList.isEmpty()) {
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
                        progData.setDataList.clear();
                    } else if (!newSetDialogController.getAddNewSet()) {
                        // und wenn auch nicht "Anfügen" gewählt, dann halt nix
                        PLog.sysLog("Setanlegen: Abbruch");
                        if (!newSetDialogController.getAskAgain()) {
                            // dann auch die Versionsnummer aktualisieren
                            PLog.sysLog("Setanlegen: Nicht wieder nachfragen");
                            ProgConfig.SYSTEM_UPDATE_PROGSET_VERSION.setValue(listPsetStandard.version);
                        }
                        PLog.sysLog("==========================================");
                        return;
                    }
                }


                // ========================================
                // gibt keine Sets oder aktualisieren
                // damit die Variablen ersetzt werden
                SetDataList.progReplacePattern(listPsetStandard);
                ProgConfig.SYSTEM_UPDATE_PROGSET_VERSION.setValue(listPsetStandard.version);

                // die Zielpafade anpassen
                final SetDataList listPsetOrgSave = progData.setDataList.getSetDataListSave();

                if (!listPsetOrgSave.isEmpty()) {
                    for (final SetData psNew : listPsetStandard.getSetDataListSave()) {
                        psNew.setDestPath(listPsetOrgSave.get(0).getDestPath());
                        psNew.setGenAboSubDir(listPsetOrgSave.get(0).isGenAboSubDir());
                        psNew.setMaxSize(listPsetOrgSave.get(0).getMaxSize());
                        psNew.setMaxField(listPsetOrgSave.get(0).getMaxField());
                    }
                }

                if (!progData.setDataList.isEmpty()) {
                    // wenn leer, dann gibts immer die neuen und die sind dann auch aktiv
                    for (final SetData psNew : listPsetStandard) {
                        // die bestehenden Sets sollen nicht gestört werden
                        psNew.setPlay(false);
                        psNew.setAbo(false);
                        psNew.setButton(false);
                        psNew.setSave(false);
                    }

                    // damit man sie auch findet :)
                    final String date = PDateFactory.F_FORMAT_dd_MM_yyyy.format(new Date());
                    listPsetStandard.forEach((psNew) -> {
                        psNew.setId(PIndex.getIndexStr());
                        psNew.setVisibleName(psNew.getVisibleName() + ", neu: " + date);
                    });

                }

                SetsPrograms.addSetTemplate(listPsetStandard); // damit auch AddOns
                // geladen werden
                PLog.sysLog("Setanlegen: OK");
                PLog.sysLog("==========================================");
            });

        } catch (final Exception ignored) {
        }
    }
}
