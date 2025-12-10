/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogFactory;
import de.p2tools.p2lib.guitools.P2TextAreaIgnoreTab;
import de.p2tools.p2lib.guitools.P2TimePicker;
import de.p2tools.p2lib.guitools.pcbo.P2CboCheckBoxListString;
import de.p2tools.p2lib.guitools.prange.P2RangeBox;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.List;

public class AddAboDto {

    public boolean isNewAbo = true;
    public InitChannelTTDescription initChannelTTDescription;
    public InitName initName;
    public InitResolution initResolution;
    public InitTimeRangeAndDuration initTimeRangeAndDuration;
    public InitStartTime initStartTime;
    public InitDestination initDestination;
    public InitSetData initSetData;

    public final ProgData progData;
    public AddAboData[] addAboData;
    public IntegerProperty actAboIsShown = new SimpleIntegerProperty(0);
    public final Label lblHit = new Label();

    public final Button btnPrev = new Button("<");
    public final Button btnNext = new Button(">");
    public final Label lblAboNo = new Label("");
    public final Label lblLastAbo = new Label("2023, ...");
    public final Label lblGenDate = new Label("2023, ...");
    public final Label lblSum = new Label("");
    public final Button btnAll = new Button("Für alle ändern");

    public final CheckAll chkActiveAll = new CheckAll();
    public final CheckAll chkSourceAll = new CheckAll();
    public final CheckAll chkDescriptionAll = new CheckAll();
    public final CheckAll chkResolutionAll = new CheckAll();
    public final CheckAll chkChannelAll = new CheckAll();
    public final CheckAll chkThemeAll = new CheckAll();
    public final CheckAll chkThemeExactAll = new CheckAll();
    public final CheckAll chkThemeTitleAll = new CheckAll();
    public final CheckAll chkTitleAll = new CheckAll();
    public final CheckAll chkSomewhereAll = new CheckAll();
    public final CheckAll chkTimeRangeAll = new CheckAll();
    public final CheckAll chkDurationAll = new CheckAll();
    public final CheckAll chkStartTimeAll = new CheckAll();
    public final CheckAll chkDestAboDirAll = new CheckAll();
    public final CheckAll chkDestAboFileNameAll = new CheckAll();
    public final CheckAll chkSetAll = new CheckAll();

    public final CheckBox chkActive = new CheckBox();
    public final CheckBox chkThemeExact = new CheckBox();
    public final TextField txtName = new TextField();

    public final RadioButton rbFilm = new RadioButton("Film");
    public final RadioButton rbAudio = new RadioButton("Audio");
    public final RadioButton rbFilmAudio = new RadioButton("Film und Audio");

    public final Text textSet = DownloadAddDialogFactory.getText("Set:");
    public final ComboBox<SetData> cboSetData = new ComboBox<>();

    // Path
    public final ComboBox<String> cboAboSubDir = new ComboBox<>();
    public final ComboBox<String> cboAboDir = new ComboBox<>();
    public final ComboBox<String> cboAboFileName = new ComboBox<>();
    public final Label lblSetSubDir = new Label();
    public final Label lblSetFileName = new Label();
    public final Label lblResPath = new Label("resPath");
    public final Label lblResFileName = new Label("resFileName");

    public final CheckBox chkAboSubDir = new CheckBox();
    public final RadioButton rbSetPath = new RadioButton("Einstellungen aus dem Set verwenden");
    public final RadioButton rbOwnPath = new RadioButton("Eigene Einstellungen verwenden");
    public final RadioButton rbSetFileName = new RadioButton("Einstellungen aus dem Set verwenden");
    public final RadioButton rbOwnFileName = new RadioButton("Eigene Einstellungen verwenden");

    public final Slider slTimeRange = new Slider();
    public final Label lblTimeRange = new Label();
    public final P2RangeBox p2RangeBoxDuration = new P2RangeBox("", true, 0, FilterCheck.FILTER_DURATION_MAX_MINUTE);
    public final TextField[] txt = new TextField[AboFieldNames.MAX_ELEM];
    public final RadioButton rbHd = new RadioButton("HD");
    public final RadioButton rbHigh = new RadioButton("Hoch");
    public final RadioButton rbLow = new RadioButton("Niedrig");
    public final TextArea textAreaDescription = new P2TextAreaIgnoreTab(false, true);

    public final TextArea textAreaTheme = new P2TextAreaIgnoreTab(false, true);
    public final TextArea textAreaThemeTitle = new P2TextAreaIgnoreTab(false, true);
    public final TextArea textAreaTitle = new P2TextAreaIgnoreTab(false, true);
    public final TextArea textAreaSomewhere = new P2TextAreaIgnoreTab(false, true);

    public P2CboCheckBoxListString mbChannel;
    public final StringProperty channelProperty = new SimpleStringProperty();

    public final P2TimePicker p2TimePicker = new P2TimePicker();
    public final CheckBox chkStartTime = new CheckBox();
    public final ObservableList<AboData> aboList = FXCollections.observableArrayList(); // Liste der Org-Abos!


    public AddAboDto(ProgData progData, boolean isNewAbo, AboData abo) {
        this.progData = progData;
        this.isNewAbo = isNewAbo;
        this.aboList.setAll(abo);

        addAboData = InitAddAboArray.initAboArray(abo);
        init();
    }

    public AddAboDto(ProgData progData, boolean isNewAbo, List<AboData> aboList) {
        // hier werden Abos geändert
        this.progData = progData;
        this.isNewAbo = isNewAbo;
        this.aboList.setAll(aboList);

        addAboData = InitAddAboArray.initAboArray(aboList);
        init();
    }

    private void init() {
        new FilterCheckRegEx(textAreaTheme);
        new FilterCheckRegEx(textAreaThemeTitle);
        new FilterCheckRegEx(textAreaTitle);
        new FilterCheckRegEx(textAreaSomewhere);

        ToggleGroup tgSource = new ToggleGroup();
        rbFilm.setToggleGroup(tgSource);
        rbAudio.setToggleGroup(tgSource);
        rbFilmAudio.setToggleGroup(tgSource);

        ToggleGroup tg = new ToggleGroup();
        rbHd.setToggleGroup(tg);
        rbHigh.setToggleGroup(tg);
        rbLow.setToggleGroup(tg);

        ToggleGroup tgl1 = new ToggleGroup();
        rbSetPath.setToggleGroup(tgl1);
        rbOwnPath.setToggleGroup(tgl1);

        ToggleGroup tgl2 = new ToggleGroup();
        rbSetFileName.setToggleGroup(tgl2);
        rbOwnFileName.setToggleGroup(tgl2);

        mbChannel = new P2CboCheckBoxListString(channelProperty, ThemeListFactory.allChannelListFilm, true);
        channelProperty.addListener((u, o, n) -> getAct().abo.setChannel(channelProperty.getValueSafe()));

        initChannelTTDescription = new InitChannelTTDescription(this);
        initName = new InitName(this);
        initResolution = new InitResolution(this);
        initTimeRangeAndDuration = new InitTimeRangeAndDuration(this);
        initStartTime = new InitStartTime(this);
        initDestination = new InitDestination(this);
        initSetData = new InitSetData(this);
    }

    public AddAboData getAct() {
        return addAboData[actAboIsShown.getValue()];
    }

    public void updateAct() {
        final int nr = actAboIsShown.getValue() + 1;
        lblSum.setText("Abo " + nr + " von " + addAboData.length + " Abos");
        lblHit.textProperty().bind(getAct().aboOrg.hitProperty().asString());
        lblAboNo.textProperty().bind(getAct().aboOrg.noProperty().asString());

        if (actAboIsShown.getValue() == 0) {
            btnPrev.setDisable(true);
            btnNext.setDisable(false);
        } else if (actAboIsShown.getValue() == addAboData.length - 1) {
            btnPrev.setDisable(false);
            btnNext.setDisable(true);
        } else {
            btnPrev.setDisable(false);
            btnNext.setDisable(false);
        }

        initChannelTTDescription.makeAct();
        initName.makeAct();
        initResolution.makeAct();
        initTimeRangeAndDuration.makeAct();
        initStartTime.makeAct();
        initDestination.makeAct();
        initSetData.makeAct();
    }

    public static class CheckAll extends CheckBox {
        public CheckAll() {
            super();
            setPadding(new Insets(0, 5, 0, 15));
        }
    }
}
