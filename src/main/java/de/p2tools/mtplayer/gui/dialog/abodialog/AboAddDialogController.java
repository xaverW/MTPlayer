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
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.scene.control.Button;

import java.util.List;

public class AboAddDialogController extends PDialogExtra {

    final Button btnOk = new Button("_Ok");
    final Button btnApply = new Button("_Anwenden");
    final Button btnCancel = new Button("_Abbrechen");

    boolean ok = false;
    ProgData progData;

    final AddAboDto addAboDto;

    public AboAddDialogController(ProgData progData, AboData abo) {
        // hier wird ein neues Abo angelegt!
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo anlegen", false, false, DECO.BORDER_SMALL, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(progData, true, abo);
        init(true);
    }

    public AboAddDialogController(ProgData progData, FilmFilter filmFilter, AboData abo) {
        // hier wird ein bestehendes Abo an den Filter angepasst
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo anlegen", false, false, DECO.BORDER_SMALL, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(progData, false, abo);

        final String channel = filmFilter.isChannelVis() ? filmFilter.getChannel() : "";
        final String theme = filmFilter.isThemeVis() ? filmFilter.getTheme() : "";
        final boolean themeExact = filmFilter.isThemeExact();
        final String title = filmFilter.isTitleVis() ? filmFilter.getTitle() : "";
        final String themeTitle = filmFilter.isThemeTitleVis() ? filmFilter.getThemeTitle() : "";
        final String somewhere = filmFilter.isSomewhereVis() ? filmFilter.getSomewhere() : "";
        final int timeRange = filmFilter.isTimeRangeVis() ? filmFilter.getTimeRange() : FilterCheck.FILTER_ALL_OR_MIN;
        final int minDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMinDur() : FilterCheck.FILTER_ALL_OR_MIN;
        final int maxDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMaxDur() : FilterCheck.FILTER_DURATION_MAX_MINUTE;

        addAboDto.getAct().abo.setChannel(channel);
        addAboDto.getAct().abo.setTheme(theme);
        addAboDto.getAct().abo.setThemeExact(themeExact);
        addAboDto.getAct().abo.setTitle(title);
        addAboDto.getAct().abo.setThemeTitle(themeTitle);
        addAboDto.getAct().abo.setSomewhere(somewhere);
        addAboDto.getAct().abo.setTimeRange(timeRange);
        addAboDto.getAct().abo.setMinDurationMinute(minDuration);
        addAboDto.getAct().abo.setMaxDurationMinute(maxDuration);

        init(true);
    }

    public AboAddDialogController(ProgData progData, List<AboData> aboList) {
        // hier werden bestehende Abos geändert
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo ändern", false, false, DECO.BORDER_SMALL, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(progData, false, aboList);
        init(true);
    }

    @Override
    public void make() {
        initGui();
        initButton();
        addAboDto.updateAct();
    }

    private void initGui() {
        if (progData.setDataList.getSetDataListSave().isEmpty() ||
                addAboDto.aboList.isEmpty()) {
            // Satz mit x, war wohl nix
            ok = false;
            close();
            return;
        }

        AboAddDialogGui aboAddDialogGui = new AboAddDialogGui(progData, addAboDto, getVBoxCont());
        aboAddDialogGui.addCont();
        aboAddDialogGui.init();

        addOkCancelApplyButtons(btnOk, btnCancel, btnApply);
        addHlpButton(P2Button.helpButton(getStageProp(), "Download", HelpText.ABO_SEARCH));
    }

    private void initButton() {
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        setMaskerPane();
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            setMaskerPane();
        });
        addAboDto.btnPrev.setOnAction(event -> {
            addAboDto.actAboIsShown.setValue(addAboDto.actAboIsShown.getValue() - 1);
            addAboDto.updateAct();
        });
        addAboDto.btnNext.setOnAction(event -> {
            addAboDto.actAboIsShown.setValue(addAboDto.actAboIsShown.getValue() + 1);
            addAboDto.updateAct();
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
        AboData abo = null;
        for (AddAboData addAboData : addAboDto.addAboData) {
            if ((abo = AboFactory.aboExistsAlready(addAboData.abo, true)) != null) {
                break;
            }
        }
        if (abo != null) {
            // dann gibts das Abo schon
            if (PAlert.showAlert_yes_no(getStage(), "Fehler", "Abo anlegen",
                    "Ein Abo mit den Einstellungen existiert bereits, es " +
                            "findet die gleichen (oder mehr) Filme:" +
                            "\n\n" +
                            ("Abo-Nr: " + abo.getNo() + "\n") +
                            (abo.getChannel().isEmpty() ? "" : "Sender: " + abo.getChannel() + "\n") +
                            (abo.getTheme().isEmpty() ? "" : "Thema: " + abo.getTheme() + "\n") +
                            (abo.getThemeTitle().isEmpty() ? "" : "Thema-Titel: " + abo.getThemeTitle() + "\n") +
                            (abo.getTitle().isEmpty() ? "" : "Titel: " + abo.getTitle() + "\n") +
                            (abo.getSomewhere().isEmpty() ? "" : "Irgendwo: " + abo.getSomewhere()) +
                            "\n\n" +
                            (addAboDto.isNewAbo ? "Trotzdem anlegen?" : "Trotzdem aktualisieren?")).equals(PAlert.BUTTON.NO)) {
                return false;
            }
        }

        boolean empty = false;
        for (AddAboData addAboData : addAboDto.addAboData) {
            abo = addAboData.abo;
            if (abo.isEmpty()) {
                empty = true;
                break;
            }
        }
        if (empty) {
            // dann ists leer
            PAlert.showErrorAlert(getStage(), "Fehler", "Abo anlegen",
                    "Abo-Nr: " + abo.getNo() + "\n" +
                            "Das Abo ist \"leer\", es enthält keine Filter.");
            return false;
        }

        return true;
    }

    private void apply() {
        if (addAboDto.isNewAbo) {
            // dann soll ein neues angelegt werden
            addNewAbos();
        } else {
            // dann bestehende anpassen
            updateAboList();
        }

        // als Vorgabe merken
        ProgConfig.ABO_MINUTE_MIN_SIZE.setValue(addAboDto.getAct().abo.getMinDurationMinute());
        ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(addAboDto.getAct().abo.getMaxDurationMinute());

        // da nicht modal!!
        progData.aboList.notifyChanges();

        // und jetzt noch die Einstellungen speichern
        ProgSave.saveAll();
    }

    private void addNewAbos() {
        // dann erst mal die ORG anlegen und diese dann einpflegen
        for (AddAboData addAboData : addAboDto.addAboData) {
            addAboData.aboOrg.copyToMe(addAboData.abo);
            progData.aboList.addAbo(addAboData.aboOrg);
            addAboData.abo.setNo(addAboData.aboOrg.getNo()); // die wird beim Einfügen neu gesetzt
        }

        // ab jetzt ists ein Update!!
        addAboDto.isNewAbo = false;
    }

    private void updateAboList() {
        for (AddAboData addAboData : addAboDto.addAboData) {
            addAboData.aboOrg.copyToMe(addAboData.abo);
        }
    }
}
