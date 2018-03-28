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
import de.mtplayer.mLib.tools.SysMsg;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.ListePsetVorlagen;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.gui.dialog.NewSetDialogController;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import javafx.application.Platform;

import java.util.Date;

import static java.lang.Thread.sleep;

public class CheckUpdate {

    private final Daten daten;

    public CheckUpdate(Daten daten) {
        this.daten = daten;
    }

    public void checkProgUpdate() {
        new Thread(this::prog).start();
    }

    private synchronized void prog() {
        try {
            if (!Boolean.parseBoolean(Config.SYSTEM_UPDATE_SEARCH.get())) {
                // will der User nicht
                return;
            }


            if (Config.SYSTEM_BUILD_NR.get().equals(Functions.getProgVersion())
                    && Config.SYSTEM_UPDATE_DATE.get().equals(StringFormatters.FORMATTER_yyyyMMdd.format(new Date()))) {
                // keine neue Version und heute schon gemacht
                return;
            }

            // damit geänderte Sets gleich gemeldet werden und nicht erst morgen
            final ProgrammUpdateSuchen pgrUpdate = new ProgrammUpdateSuchen();
            if (pgrUpdate.checkVersion(false, false /* bei aktuell anzeigen */, true /* Hinweis */,
                    false /* hinweiseAlleAnzeigen */)) {
                Listener.notify(Listener.EREIGNIS_GUI_UPDATE_VERFUEGBAR, CheckUpdate.class.getSimpleName());
            } else {
                Listener.notify(Listener.EREIGNIS_GUI_PROGRAMM_AKTUELL, CheckUpdate.class.getSimpleName());
            }

            // ==============================================
            // Sets auf Update prüfen
            checkForPsetUpdates();

            try {
                sleep(10_000);
            } catch (final InterruptedException ignored) {
            }
            Listener.notify(Listener.EREIGNIS_GUI_ORG_TITEL, CheckUpdate.class.getSimpleName());

        } catch (final Exception ex) {
            Log.errorLog(794612801, ex);
        }
    }

    private void checkForPsetUpdates() {
        try {
            Platform.runLater(() -> {

                final SetList listePsetStandard = ListePsetVorlagen.getStandarset(false /* replaceMuster */);
                final String version = Config.SYSTEM_VERSION_PROGRAMMSET.get();
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

                    final NewSetDialogController newSetDialogController = new NewSetDialogController(daten);
                    if (newSetDialogController.getReplaceSet()) {
                        // dann werden die Sets durch die Neuen ersetzt
                        daten.setList.clear();
                    } else if (!newSetDialogController.getAddNewSet()) {
                        // und wenn auch nicht "Anfügen" gewählt, dann halt nix
                        SysMsg.sysMsg("Setanlegen: Abbruch");
                        if (!newSetDialogController.getAskAgain()) {
                            // dann auch die Versionsnummer aktualisieren
                            SysMsg.sysMsg("Setanlegen: Nicht wieder nachfragen");
                            Config.SYSTEM_VERSION_PROGRAMMSET.setValue(listePsetStandard.version);
                        }
                        SysMsg.sysMsg("==========================================");
                        return;
                    }
                }


                // ========================================
                // gibt keine Sets oder aktualisieren
                // damit die Variablen ersetzt werden
                SetList.progMusterErsetzen(listePsetStandard);
                Config.SYSTEM_VERSION_PROGRAMMSET.setValue(listePsetStandard.version);

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
                SysMsg.sysMsg("Setanlegen: OK");
                SysMsg.sysMsg("==========================================");
            });

        } catch (final Exception ignored) {
        }
    }

}
