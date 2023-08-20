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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.filter.PMenuButton;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PTextAreaIgnoreTab;
import de.p2tools.p2lib.guitools.PTimePicker;
import de.p2tools.p2lib.guitools.prange.P2RangeBox;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class AboDialogController extends PDialogExtra {

    final GridPane gridPane = new GridPane();
    final Button btnOk = new Button("_Ok");
    final Button btnApply = new Button("_Anwenden");
    final Button btnCancel = new Button("_Abbrechen");

    final ComboBox<SetData> cboSetData = new ComboBox<>();
    final ComboBox<String> cboDestination = new ComboBox<>();
    final Slider slTimeRange = new Slider();
    final Label lblTimeRange = new Label();
    final P2RangeBox p2RangeBoxTime = new P2RangeBox("", false, 0, FilterCheck.FILTER_DURATION_MAX_MINUTE);
    final CheckBox cbxOn = new CheckBox();
    final Label[] lbl = new Label[AboFieldNames.MAX_ELEM];
    final TextField[] txt = new TextField[AboFieldNames.MAX_ELEM];
    final CheckBox[] cbx = new CheckBox[AboFieldNames.MAX_ELEM];
    final CheckBox[] cbxEditAll = new CheckBox[AboFieldNames.MAX_ELEM];
    final RadioButton rbHd = new RadioButton("HD");
    final RadioButton rbHigh = new RadioButton("hoch");
    final RadioButton rbLow = new RadioButton("niedrig");
    final TextArea textAreaDescription = new PTextAreaIgnoreTab(false, true);

    final TextArea textAreaTheme = new PTextAreaIgnoreTab(false, true);
    final TextArea textAreaThemeTitle = new PTextAreaIgnoreTab(false, true);
    final TextArea textAreaTitle = new PTextAreaIgnoreTab(false, true);
    final TextArea textAreaSomewhere = new PTextAreaIgnoreTab(false, true);

    final PMenuButton mbChannel;
    final PTimePicker pTimePicker = new PTimePicker();
    final CheckBox chkStartTime = new CheckBox();
    final ObservableList<AboData> aboList = FXCollections.observableArrayList();
    final AboData aboCopy;
    boolean addNewAbo;
    BooleanProperty okProp = new SimpleBooleanProperty(true);
    String memory = "";
    ProgData progData;

    public AboDialogController(ProgData progData, AboData abo) {
        // hier wird ein neues Abo angelegt!
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo anlegen", false, false, DECO.BORDER, true);

        this.progData = progData;
        this.addNewAbo = true;
        aboList.add(abo);
        this.aboCopy = abo.getCopy();
        this.mbChannel = new PMenuButton(aboCopy.channelProperty(),
                ThemeListFactory.allChannelList, true);

        initDialog();
    }

    public AboDialogController(ProgData progData, FilmFilter filmFilter, AboData abo) {
        // hier wird ein Abo an den Filter angepasst
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo anlegen", false, false, DECO.BORDER, true);

        this.progData = progData;
        this.addNewAbo = false;
        aboList.add(abo);
        aboCopy = abo.getCopy();
        this.mbChannel = new PMenuButton(aboCopy.channelProperty(),
                ThemeListFactory.allChannelList, true);

        final String channel = filmFilter.isChannelVis() ? filmFilter.getChannel() : "";
        final String theme = filmFilter.isThemeVis() ? filmFilter.getTheme() : "";
        final boolean themeExact = filmFilter.isThemeExact();
        final String title = filmFilter.isTitleVis() ? filmFilter.getTitle() : "";
        final String themeTitle = filmFilter.isThemeTitleVis() ? filmFilter.getThemeTitle() : "";
        final String somewhere = filmFilter.isSomewhereVis() ? filmFilter.getSomewhere() : "";
        final int timeRange = filmFilter.isTimeRangeVis() ? filmFilter.getTimeRange() : FilterCheck.FILTER_ALL_OR_MIN;
        final int minDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMinDur() : FilterCheck.FILTER_ALL_OR_MIN;
        final int maxDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMaxDur() : FilterCheck.FILTER_DURATION_MAX_MINUTE;

        aboCopy.setChannel(channel);
        aboCopy.setTheme(theme);
        aboCopy.setThemeExact(themeExact);
        aboCopy.setTitle(title);
        aboCopy.setThemeTitle(themeTitle);
        aboCopy.setSomewhere(somewhere);
        aboCopy.setTimeRange(timeRange);
        aboCopy.setMinDurationMinute(minDuration);
        aboCopy.setMaxDurationMinute(maxDuration);

        initDialog();
    }

    public AboDialogController(ProgData progData, List<AboData> aboList) {
        // hier werden Abos geändert
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo ändern", false, false, DECO.BORDER, true);

        this.progData = progData;
        this.addNewAbo = false;

        // hängt sonst an "getSelList"
        this.aboList.addAll(aboList);
        this.aboCopy = aboList.get(0).getCopy();
        this.mbChannel = new PMenuButton(aboCopy.channelProperty(),
                ThemeListFactory.allChannelList, true);

        initDialog();
    }

    @Override
    public void make() {
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        setMaskerPane();
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            setMaskerPane();
        });

        btnOk.disableProperty().bind(aboCopy.nameProperty().isEmpty().or(okProp.not()));
        btnOk.setOnAction(a -> {
            if (checkChanges()) {
                apply();
                close();
            }
        });

        btnApply.disableProperty().bind(aboCopy.nameProperty().isEmpty().or(okProp.not()));
        btnApply.setOnAction(a -> {
            if (checkChanges()) {
                apply();
            }
        });

        btnCancel.setOnAction(a -> close());

        gridPane.setHgap(5);
        gridPane.setVgap(10);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        if (aboList.size() > 1) {
            // dann werden mehre Abos geändert
            Label l1 = new Label(aboList.size() + " Abos ändern");
            l1.setStyle("-fx-font-weight: bold;");
            gridPane.add(l1, 1, 0);
            GridPane.setValignment(l1, VPos.CENTER);

            Label l2 = new Label("nur diese\nFelder\nändern");
            l2.setMinHeight(Region.USE_PREF_SIZE);
            l2.setTextAlignment(TextAlignment.CENTER);
            VBox vBox = new VBox();
            vBox.getStyleClass().add("chk-edit-all-text");
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(l2);
            gridPane.add(vBox, 2, 0);
        }

        makeControl();
        btnOk.requestFocus();
    }

    void makeControl() {
    }

    private void setMaskerPane() {
        if (progData.maskerPane.isVisible()) {
            this.setMaskerVisible(true);
        } else {
            this.setMaskerVisible(false);
        }
    }

    private void initDialog() {
        getVBoxCont().getChildren().add(gridPane);
        addOkCancelApplyButtons(btnOk, btnCancel, btnApply);
        addHlpButton(PButton.helpButton(getStageProp(), "Download", HelpText.ABO_SEARCH));
        SetData setData = aboCopy.getSetData(progData);
        if (setData == null) {
            Platform.runLater(() -> new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO));
        } else {
            init(true);
        }
    }

    private boolean checkChanges() {
        AboData abo;
        if (addNewAbo && (abo = AboFactory.aboExistsAlready(aboCopy)) != null) {
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

        if (addNewAbo && aboCopy.isEmpty()) {
            // dann ists leer
            PAlert.showErrorAlert(getStage(), "Fehler", "Abo anlegen",
                    "Das Abo ist \"leer\", es enthält keine Filter.");
            return false;
        }

        if (aboList.size() == 1) {
            // entweder nur ein Abo
            aboList.get(0).copyToMe(aboCopy);

        } else {
            // oder nur die markierten Felder bei ALLEN Abos
            updateAboList();
        }
        return true;
    }

    private void apply() {
        if (addNewAbo) {
            addNewAbo = false;
            progData.aboList.addAbo(aboCopy);
        }
        // als Vorgabe merken
        ProgConfig.ABO_MINUTE_MIN_SIZE.setValue(aboCopy.getMinDurationMinute());
        ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(aboCopy.getMaxDurationMinute());

        // da nicht modal!!
        progData.aboList.notifyChanges();

        // und jetzt noch die Einstellungen speichern
        ProgSave.saveAll();
    }

    private void updateAboList() {
        for (int i = 0; i < cbxEditAll.length; ++i) {
            if (cbxEditAll[i] == null || !cbxEditAll[i].isSelected()) {
                continue;
            }

            // dann wird das Feld bei allen Abos geändert
            for (final AboData abo : aboList) {
                if (i == AboFieldNames.ABO_MIN_DURATION_NO) {
                    // duration MIN dann AUCH max
                    abo.properties[AboFieldNames.ABO_MAX_DURATION_NO].setValue(aboCopy.properties[AboFieldNames.ABO_MAX_DURATION_NO].getValue());

                } else if (i == AboFieldNames.ABO_SET_DATA_ID_NO) {
                    // dann auch SetData
                    abo.setSetData(aboCopy.getSetData());
                }

                abo.properties[i].setValue(aboCopy.properties[i].getValue());
            }
        }
    }
}
