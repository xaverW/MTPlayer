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

package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.scene.control.Button;

import java.util.List;

public class AboEditDialogController extends PDialogExtra {

    final Button btnOk = new Button("_Ok");
    final Button btnApply = new Button("_Anwenden");
    final Button btnCancel = new Button("_Abbrechen");

    boolean ok = false;
    ProgData progData;

    final AddAboDto addAboDto;

    public AboEditDialogController(ProgData progData, AboData abo) {
        // hier wird ein neues Abo angelegt!
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo anlegen", false, false, DECO.BORDER_SMALL, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(true, abo);
        init(true);
    }

    public AboEditDialogController(ProgData progData, FilmFilter filmFilter, AboData abo) {
        // hier wird ein bestehendes Abo an den Filter angepasst
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo anlegen", false, false, DECO.BORDER_SMALL, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(false, abo);

        final String channel = filmFilter.isChannelVis() ? filmFilter.getChannel() : "";
        final String theme = filmFilter.isThemeVis() ? filmFilter.getTheme() : "";
        final boolean themeExact = filmFilter.isThemeExact();
        final String title = filmFilter.isTitleVis() ? filmFilter.getTitle() : "";
        final String themeTitle = filmFilter.isThemeTitleVis() ? filmFilter.getThemeTitle() : "";
        final String somewhere = filmFilter.isSomewhereVis() ? filmFilter.getSomewhere() : "";
        final int timeRange = filmFilter.isTimeRangeVis() ? filmFilter.getTimeRange() : FilterCheck.FILTER_ALL_OR_MIN;
        final int minDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMinDur() : FilterCheck.FILTER_ALL_OR_MIN;
        final int maxDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMaxDur() : FilterCheck.FILTER_DURATION_MAX_MINUTE;

        addAboDto.aboCopy.setChannel(channel);
        addAboDto.aboCopy.setTheme(theme);
        addAboDto.aboCopy.setThemeExact(themeExact);
        addAboDto.aboCopy.setTitle(title);
        addAboDto.aboCopy.setThemeTitle(themeTitle);
        addAboDto.aboCopy.setSomewhere(somewhere);
        addAboDto.aboCopy.setTimeRange(timeRange);
        addAboDto.aboCopy.setMinDurationMinute(minDuration);
        addAboDto.aboCopy.setMaxDurationMinute(maxDuration);

        init(true);
    }

    public AboEditDialogController(ProgData progData, List<AboData> aboList) {
        // hier werden bestehende Abos geändert
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo ändern", false, false, DECO.BORDER_SMALL, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(false, aboList);
        init(true);
    }

    @Override
    public void make() {
        initGui();
        initButton();
    }

    private void initGui() {
        if (progData.setDataList.getSetDataListSave().isEmpty() ||
                addAboDto.aboList.isEmpty()) {
            // Satz mit x, war wohl nix
            ok = false;
            close();
            return;
        }

        AboEditDialogGui aboEditDialogGui = new AboEditDialogGui(progData, addAboDto, getVBoxCont());
        aboEditDialogGui.addCont();
        aboEditDialogGui.init();

        addOkCancelApplyButtons(btnOk, btnCancel, btnApply);
        addHlpButton(P2Button.helpButton(getStageProp(), "Download", HelpText.ABO_SEARCH));
    }

    private void initButton() {
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        setMaskerPane();
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            setMaskerPane();
        });

//        btnOk.disableProperty().bind(addAboDto.aboCopy.nameProperty().isEmpty().or(okProp.not()));
        btnOk.setOnAction(a -> {
            if (check()) {
                apply();
                close();
            }
        });

//        btnApply.disableProperty().bind(addAboDto.aboCopy.nameProperty().isEmpty().or(okProp.not()));
        btnApply.setOnAction(a -> {
            if (check()) {
                apply();
            }
        });

        btnCancel.setOnAction(a -> {
            ok = false;
            close();
        });
        btnOk.requestFocus();
    }

    private void setMaskerPane() {
        if (progData.maskerPane.isVisible()) {
            this.setMaskerVisible(true);
        } else {
            this.setMaskerVisible(false);
        }
    }

    private boolean check() {
        AboData abo;
        if (addAboDto.addNewAbo && (abo = AboFactory.aboExistsAlready(addAboDto.aboCopy)) != null) {
            // dann gibts das Abo schon
            if (PAlert.showAlert_yes_no(getStage(), "Fehler", "Abo anlegen",
                    "Ein Abo mit den Einstellungen existiert bereits:" +
                            "\n\n" +
                            (abo.getChannel().isEmpty() ? "" : "Sender: " + abo.getChannel() + "\n") +
                            (abo.getTheme().isEmpty() ? "" : "Thema: " + abo.getTheme() + "\n") +
                            (abo.getThemeTitle().isEmpty() ? "" : "Thema-Titel: " + abo.getThemeTitle() + "\n") +
                            (abo.getTitle().isEmpty() ? "" : "Titel: " + abo.getTitle() + "\n") +
                            (abo.getSomewhere().isEmpty() ? "" : "Irgendwo: " + abo.getSomewhere()) +
                            "\n\n" +
                            "Trotzdem anlegen?").equals(PAlert.BUTTON.NO)) {
                return false;
            }
        }

        if (addAboDto.addNewAbo && addAboDto.aboCopy.isEmpty()) {
            // dann ists leer
            PAlert.showErrorAlert(getStage(), "Fehler", "Abo anlegen",
                    "Das Abo ist \"leer\", es enthält keine Filter.");
            return false;
        }

        if (addAboDto.aboList.size() == 1) {
            // entweder nur ein Abo
            addAboDto.aboList.get(0).copyToMe(addAboDto.aboCopy);

        } else {
            // oder nur die markierten Felder bei ALLEN Abos
            updateAboList();
        }
        return true;
    }

    private void apply() {
        if (addAboDto.addNewAbo) {
            addAboDto.addNewAbo = false;
            progData.aboList.addAbo(addAboDto.aboCopy);
        }
        // als Vorgabe merken
        ProgConfig.ABO_MINUTE_MIN_SIZE.setValue(addAboDto.aboCopy.getMinDurationMinute());
        ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(addAboDto.aboCopy.getMaxDurationMinute());

        // da nicht modal!!
        progData.aboList.notifyChanges();

        // und jetzt noch die Einstellungen speichern
        ProgSave.saveAll();
    }

    private void updateAboList() {
        for (int i = 0; i < addAboDto.cbxEditAll.length; ++i) {
            if (addAboDto.cbxEditAll[i] == null || !addAboDto.cbxEditAll[i].isSelected()) {
                continue;
            }

            // dann wird das Feld bei allen Abos geändert
            for (final AboData abo : addAboDto.aboList) {
                if (i == AboFieldNames.ABO_MIN_DURATION_NO) {
                    // duration MIN dann AUCH max
                    abo.properties[AboFieldNames.ABO_MAX_DURATION_NO].setValue(addAboDto.aboCopy.properties[AboFieldNames.ABO_MAX_DURATION_NO].getValue());

                } else if (i == AboFieldNames.ABO_SET_DATA_ID_NO) {
                    // dann auch SetData
                    abo.setSetData(addAboDto.aboCopy.getSetData());
                }

                abo.properties[i].setValue(addAboDto.aboCopy.properties[i].getValue());
            }
        }
    }
}
