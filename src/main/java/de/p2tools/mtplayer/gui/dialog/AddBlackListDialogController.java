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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class AddBlackListDialogController extends P2DialogExtra {

    private boolean ok = false;
    private Button btnOk = new Button("_Ok");
    private Button btnCancel = new Button("_Abbrechen");

    private final GridPane gridPane = new GridPane();
    private final Button btnCount = new Button("Treffer zählen");
    private final Label lblCount = new Label();

    public final RadioButton rbFilm = new RadioButton("Film");
    public final RadioButton rbAudio = new RadioButton("Audio");
    public final RadioButton rbFilmAudio = new RadioButton("Film und Audio");
    private final Button btnList = new Button();

    private final P2MenuButton mbChannel;
    private final TextField txtTheme = new TextField();
    private final P2ToggleSwitch tgTheme = new P2ToggleSwitch("exakt:");
    private final TextField txtTitle = new TextField();
    private final TextField txtThemeTitle = new TextField();
    private final Button btnChannel = new Button();
    private final Button btnTheme = new Button();
    private final Button btnTitel = new Button();
    private final Button btnClearChannel = new Button();
    private final Button btnClearTheme = new Button();
    private final Button btnClearTitel = new Button();
    private final Button btnClearThemeTitel = new Button();

    private final int list;
    private final String channel; // sind die Startwerte
    private final String theme; // sind die Startwerte
    private final String title; // sind die Startwerte

    private final BlackData blackData;

    public AddBlackListDialogController(BlackData blackData) {
        super(ProgData.getInstance().primaryStage, ProgConfig.ADD_BLACK_DIALOG_SIZE,
                "Blacklist-Eintrag erstellen", true, false);

        this.list = blackData.getList();
        this.channel = blackData.getChannel();
        this.theme = blackData.getTheme();
        this.title = blackData.getTitle();
        this.blackData = blackData;
        mbChannel = new P2MenuButton(this.blackData.channelProperty(),
                ThemeListFactory.allChannelListFilm, true);

        init(true);
    }

    @Override
    public void make() {
        addOkCancelButtons(btnOk, btnCancel);
        getHboxLeft().getChildren().addAll(btnCount, lblCount);
        getVBoxCont().getChildren().add(gridPane);
        initButton();
        initGridPane();
    }

    public boolean isOk() {
        return ok;
    }

    private void initButton() {
        btnOk.setOnAction(event -> {
            ok = true;
            close();
        });
        btnOk.disableProperty().bind(blackData.channelProperty().isEmpty().and(blackData.themeProperty().isEmpty().and(
                blackData.titleProperty().isEmpty().and(blackData.themeTitleProperty().isEmpty())
        )));

        btnCancel.setOnAction(event -> {
            ok = false;
            close();
        });
    }

    private void initGridPane() {
        btnCount.setOnAction(a -> {
            BlacklistFilterFactory.countHits(blackData);
            lblCount.setText(blackData.getCountHits() + "");
        });

        final ToggleGroup toggleGroupList = new ToggleGroup();
        rbFilmAudio.setToggleGroup(toggleGroupList);
        rbFilm.setToggleGroup(toggleGroupList);
        rbAudio.setToggleGroup(toggleGroupList);
        toggleGroupList.selectedToggleProperty().addListener((u, o, n) -> {
            if (rbFilmAudio.isSelected()) {
                blackData.setList(ProgConst.LIST_FILM_AUDIO);
            } else if (rbFilm.isSelected()) {
                blackData.setList(ProgConst.LIST_FILM);
            } else {
                blackData.setList(ProgConst.LIST_AUDIO);
            }
        });
        rbFilmAudio.setSelected(blackData.getList() == ProgConst.LIST_FILM_AUDIO);
        rbFilm.setSelected(blackData.getList() == ProgConst.LIST_FILM);
        rbAudio.setSelected(blackData.getList() == ProgConst.LIST_AUDIO);
        btnList.setOnAction(a -> {
            rbFilmAudio.setSelected(list == ProgConst.LIST_FILM_AUDIO);
            rbFilm.setSelected(list == ProgConst.LIST_FILM);
            rbAudio.setSelected(list == ProgConst.LIST_AUDIO);
        });
        btnList.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());

        btnChannel.setOnAction(a -> blackData.setChannel(channel));
        btnChannel.setTooltip(new Tooltip("Daten vom Film eintragen"));
        btnChannel.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());

        btnTheme.setOnAction(a -> blackData.setTheme(theme));
        btnTheme.setTooltip(new Tooltip("Daten vom Film eintragen"));
        btnTheme.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());

        btnTitel.setOnAction(a -> blackData.setTitle(title));
        btnTitel.setTooltip(new Tooltip("Daten vom Film eintragen"));
        btnTitel.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());

        btnClearChannel.setOnAction(a -> blackData.channelProperty().setValue(""));
        btnClearChannel.setTooltip(new Tooltip("Feld löschen"));
        btnClearChannel.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());

        btnClearTheme.setOnAction(a -> blackData.setTheme(""));
        btnClearTheme.setTooltip(new Tooltip("Feld löschen"));
        btnClearTheme.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());

        btnClearTitel.setOnAction(a -> blackData.setTitle(""));
        btnClearTitel.setTooltip(new Tooltip("Feld löschen"));
        btnClearTitel.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());

        btnClearThemeTitel.setOnAction(a -> blackData.setThemeTitle(""));
        btnClearThemeTitel.setTooltip(new Tooltip("Feld löschen"));
        btnClearThemeTitel.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());

        txtTheme.textProperty().bindBidirectional(blackData.themeProperty());
        tgTheme.selectedProperty().bindBidirectional(blackData.themeExactProperty());
        txtTitle.textProperty().bindBidirectional(blackData.titleProperty());
        txtThemeTitle.textProperty().bindBidirectional(blackData.themeTitleProperty());

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize());

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5));

        int row = 0;
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(rbFilmAudio, rbFilm, rbAudio);
        gridPane.add(new Label("Liste:"), 0, row);
        gridPane.add(hBox, 1, row);
        gridPane.add(btnList, 2, row);

        gridPane.add(new Label("Sender:"), 0, ++row);
        gridPane.add(mbChannel, 1, row);
        gridPane.add(btnChannel, 2, row);
        gridPane.add(btnClearChannel, 3, row);

        gridPane.add(new Label("Thema:"), 0, ++row);
        gridPane.add(txtTheme, 1, row);
        gridPane.add(btnTheme, 2, row);
        gridPane.add(btnClearTheme, 3, row);
        gridPane.add(tgTheme, 1, ++row);

        gridPane.add(new Label("Thema-Titel:"), 0, ++row);
        gridPane.add(txtThemeTitle, 1, row);
        gridPane.add(btnClearThemeTitel, 3, row);

        gridPane.add(new Label("Titel:"), 0, ++row);
        gridPane.add(txtTitle, 1, row);
        gridPane.add(btnTitel, 2, row);
        gridPane.add(btnClearTitel, 3, row);
    }
}
