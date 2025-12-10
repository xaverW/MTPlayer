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

package de.p2tools.mtplayer.gui.configdialog.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.load.LoadAudioFactory;
import de.p2tools.mtplayer.controller.load.LoadFilmFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneFilmDouble {

    private final P2ToggleSwitch tglRemove = new P2ToggleSwitch("Doppelte Filme beim Laden der Liste ausschließen");
    private final P2ToggleSwitch tglTT = new P2ToggleSwitch("Filme sind nur gleich, wenn auch Thema und Titel gleich sind");

    private final VBox vBox = new VBox(P2LibConst.PADDING);
    private final ListView<String> lvSender = new ListView<>();
    private final HBox hBoxButton = new HBox();
    private final Label lblDouble = new Label();

    private final ObservableList<String> selList = FXCollections.observableArrayList();
    private final ProgData progData;
    private final Stage stage;

    public PaneFilmDouble(Stage stage, ProgData progData) {
        this.stage = stage;
        this.progData = progData;
    }

    public void close() {
        tglRemove.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_FILMLIST_REMOVE_DOUBLE);
        tglTT.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_FILMLIST_DOUBLE_WITH_THEME_TITLE);
    }

    public TitledPane make(Collection<TitledPane> result) {
        tglRemove.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILMLIST_REMOVE_DOUBLE);
        tglTT.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILMLIST_DOUBLE_WITH_THEME_TITLE);
        final Button btnHelpMark = P2Button.helpButton(stage, "Doppelte Filme markieren",
                HelpText.LOAD_FILMLIST_MARK_DOUBLE);


        Button btnLoadAudio = new Button("_Audioliste mit diesen Einstellungen neu laden");
        btnLoadAudio.setTooltip(new Tooltip("Eine komplette neue Audioliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Audioliste werden so sofort übernommen"));
        btnLoadAudio.setOnAction(event -> {
            LoadAudioFactory.loadAudioListFromWeb(true, true);
        });
        btnLoadAudio.setMaxWidth(Double.MAX_VALUE);
        btnLoadAudio.disableProperty().bind(ProgConfig.SYSTEM_USE_AUDIOLIST.not());
        HBox.setHgrow(btnLoadAudio, Priority.ALWAYS);

        Button btnLoadFilm = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoadFilm.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoadFilm.setOnAction(event -> {
            LoadFilmFactory.loadFilmListFromWeb(true, true);
        });
        btnLoadFilm.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnLoadFilm, Priority.ALWAYS);

        HBox hBoxBtn = new HBox(5);
        hBoxBtn.setAlignment(Pos.CENTER_RIGHT);
        hBoxBtn.getChildren().addAll(btnLoadAudio, btnLoadFilm);


        Separator sp2 = new Separator();
        sp2.getStyleClass().add("pseperator2");
        sp2.setMinHeight(0);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        int row = 0;
        gridPane.add(tglRemove, 0, row);
        gridPane.add(btnHelpMark, 1, row, 1, 2);
        GridPane.setValignment(btnHelpMark, VPos.TOP);

        gridPane.add(tglTT, 0, ++row);

        gridPane.add(lvSender, 0, ++row, 2, 1);
        gridPane.add(hBoxButton, 0, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize());

        vBox.setPadding(new Insets(P2LibConst.PADDING));
        vBox.getChildren().addAll(gridPane, P2GuiTools.getVBoxGrower(), hBoxBtn);

        addSenderList();

        TitledPane tpConfig = new TitledPane("Doppelte Beiträge markieren", vBox);
        result.add(tpConfig);
        return tpConfig;
    }

    private void addSenderList() {
        String mark = ProgConfig.SYSTEM_MARK_DOUBLE_CHANNEL_LIST.getValueSafe();
        String[] markArr = mark.split(",");
        for (String s : markArr) {
            if (!s.isEmpty()) {
                selList.add(s);
            }
        }
        lvSender.setItems(selList);

        final ComboBox<String> cboSender = new ComboBox<>();
        cboSender.setItems(ThemeListFactory.allChannelListFilm);
        cboSender.getSelectionModel().select(0);

        Button btnAdd = new Button("");
        btnAdd.setTooltip(new Tooltip("Einen neuen Sender hinzufügen"));
        btnAdd.setGraphic(ProgIcons.ICON_BUTTON_ADD.getImageView());
        btnAdd.setOnAction(event -> {
            String str = cboSender.getSelectionModel().getSelectedItem();
            if (str != null && !str.isEmpty() && !selList.contains(str)) {
                selList.add(cboSender.getSelectionModel().getSelectedItem());
                lvSender.getSelectionModel().select(str);
            }
            addSelList();
        });

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Markierten Sender löschen"));
        btnDel.setGraphic(ProgIcons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            String str = lvSender.getSelectionModel().getSelectedItem();
            if (str != null) {
                selList.remove(lvSender.getSelectionModel().getSelectedItem());
            }
            addSelList();
        });

        Button btnUp = new Button("");
        btnUp.setTooltip(new Tooltip("Markierten Sender nach oben schieben"));
        btnUp.setGraphic(ProgIcons.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = up(sel, true);
                lvSender.getSelectionModel().select(newSel);
            }
            addSelList();
        });

        Button btnDown = new Button("");
        btnDown.setTooltip(new Tooltip("Markierten Sender nach unten schieben"));
        btnDown.setGraphic(ProgIcons.ICON_BUTTON_MOVE_DOWN.getImageView());
        btnDown.setOnAction(event -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = up(sel, false);
                lvSender.getSelectionModel().select(newSel);
            }
            addSelList();
        });

        ProgConfig.SYSTEM_FILMLIST_COUNT_DOUBLE.addListener((u, o, n) -> {
            Platform.runLater(this::setLblDouble);
        });
        setLblDouble();

        hBoxButton.setSpacing(P2LibConst.SPACING_HBOX);
        hBoxButton.getChildren().addAll(cboSender, btnAdd, btnDel,
                P2GuiTools.getHDistance(20), btnUp, btnDown, P2GuiTools.getHBoxGrower(),
                new Label("Anzahl Doppelte: "), lblDouble);
    }

    private void setLblDouble() {
        lblDouble.setText(ProgConfig.SYSTEM_FILMLIST_COUNT_DOUBLE.getValue() + "");
    }

    private void addSelList() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String ss : selList) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(",");
            }
            stringBuilder.append(ss);
        }
        ProgConfig.SYSTEM_MARK_DOUBLE_CHANNEL_LIST.setValue(stringBuilder.toString());
    }

    private int getSelectedLine() {
        final int sel = lvSender.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            P2Alert.showInfoNoSelection(stage);
        }
        return sel;
    }

    private int up(int idx, boolean up) {
        final String prog = selList.remove(idx);
        int newIdx = idx;
        if (up) {
            if (newIdx > 0) {
                --newIdx;
            }
        } else if (newIdx < selList.size()) {
            ++newIdx;
        }
        selList.add(newIdx, prog);
        return newIdx;
    }

}