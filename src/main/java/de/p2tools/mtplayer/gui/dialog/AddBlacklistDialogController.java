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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFactory;
import de.p2tools.mtplayer.gui.filter.PMenuButton;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class AddBlacklistDialogController extends PDialogExtra {

    private boolean ok = false;
    private Button btnOk = new Button("_Ok");
    private Button btnCancel = new Button("_Abbrechen");

    private final GridPane gridPane = new GridPane();
    private final Button btnCount = new Button("Treffer zählen");
    private final Label lblCount = new Label();

    private final PMenuButton mbChannel;
    private final TextField txtTheme = new TextField();
    private final PToggleSwitch tgTheme = new PToggleSwitch("exakt:");
    private final TextField txtTitle = new TextField();
    private final TextField txtThemeTitle = new TextField();
    private final Button btnChannel = new Button();
    private final Button btnTheme = new Button();
    private final Button btnTitel = new Button();
    private final Button btnClearChannel = new Button();
    private final Button btnClearTheme = new Button();
    private final Button btnClearTitel = new Button();
    private final Button btnClearThemeTitel = new Button();

    private final ArrayList<CheckMenuItem> checkMenuItemsList = new ArrayList<>();
    private final FilmDataMTP filmDataMTP;
    private final BlackData blackData;
    private final ProgData progData;

    public AddBlacklistDialogController(ProgData progData, FilmDataMTP filmDataMTP, BlackData blackData) {
        super(progData.primaryStage, ProgConfig.ADD_BLACK_DIALOG_SIZE,
                "Blacklist-Eintrag erstellen", true, false);

        this.progData = progData;
        this.filmDataMTP = filmDataMTP;
        this.blackData = blackData;

        mbChannel = new PMenuButton(blackData.channelProperty(),
                ProgData.getInstance().worker.getAllChannelList(), true);


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
//        initSenderMenu();
        btnCount.setOnAction(a -> {
            BlacklistFactory.countHits(blackData);
            lblCount.setText(blackData.getCountHits() + "");
        });

        btnChannel.setOnAction(a -> {
            blackData.setChannel(filmDataMTP.getChannel());
//            blackData.channelProperty().setValue("");
        });
        btnChannel.setTooltip(new Tooltip("Daten vom Film eintragen"));
        btnChannel.setGraphic(ProgIcons.Icons.ICON_BUTTON_RESET.getImageView());

        btnTheme.setOnAction(a -> blackData.setTheme(filmDataMTP.getTheme()));
        btnTheme.setTooltip(new Tooltip("Daten vom Film eintragen"));
        btnTheme.setGraphic(ProgIcons.Icons.ICON_BUTTON_RESET.getImageView());

        btnTitel.setOnAction(a -> blackData.setTitle(filmDataMTP.getTitle()));
        btnTitel.setTooltip(new Tooltip("Daten vom Film eintragen"));
        btnTitel.setGraphic(ProgIcons.Icons.ICON_BUTTON_RESET.getImageView());

        btnClearChannel.setOnAction(a -> blackData.channelProperty().setValue(""));
        btnClearChannel.setTooltip(new Tooltip("Feld löschen"));
        btnClearChannel.setGraphic(ProgIcons.Icons.ICON_BUTTON_CLEAR.getImageView());

        btnClearTheme.setOnAction(a -> blackData.setTheme(""));
        btnClearTheme.setTooltip(new Tooltip("Feld löschen"));
        btnClearTheme.setGraphic(ProgIcons.Icons.ICON_BUTTON_CLEAR.getImageView());

        btnClearTitel.setOnAction(a -> blackData.setTitle(""));
        btnClearTitel.setTooltip(new Tooltip("Feld löschen"));
        btnClearTitel.setGraphic(ProgIcons.Icons.ICON_BUTTON_CLEAR.getImageView());

        btnClearThemeTitel.setOnAction(a -> blackData.setThemeTitle(""));
        btnClearThemeTitel.setTooltip(new Tooltip("Feld löschen"));
        btnClearThemeTitel.setGraphic(ProgIcons.Icons.ICON_BUTTON_CLEAR.getImageView());

        txtTheme.textProperty().bindBidirectional(blackData.themeProperty());
        tgTheme.selectedProperty().bindBidirectional(blackData.themeExactProperty());
        txtTitle.textProperty().bindBidirectional(blackData.titleProperty());
        txtThemeTitle.textProperty().bindBidirectional(blackData.themeTitleProperty());

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize());

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5));

        int row = 0;
        gridPane.add(new Label("Sender:"), 0, row);
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

//    private void initSenderMenu() {
//        mbChannel.getItems().clear();
//        checkMenuItemsList.clear();
//        mbChannel.getStyleClass().add("cbo-menu");
//
//        List<String> senderArr = new ArrayList<>();
//        String sender = blackData.channelProperty().get();
//        if (sender != null) {
//            if (sender.contains(",")) {
//                senderArr.addAll(Arrays.asList(sender.replace(" ", "").toLowerCase().split(",")));
//            } else {
//                senderArr.add(sender.toLowerCase());
//            }
//            senderArr.stream().forEach(s -> s = s.trim());
//        }
//
//        MenuItem mi = new MenuItem("Auswahl löschen");
//        mi.setOnAction(a -> clearMenuText());
//        mbChannel.getItems().add(mi);
//
//        for (String s : ProgData.getInstance().worker.getAllChannelList()) {
//            if (s.isEmpty()) {
//                continue;
//            }
//            CheckMenuItem miCheck = new CheckMenuItem(s);
//            if (senderArr.contains(s.toLowerCase())) {
//                miCheck.setSelected(true);
//            }
//            miCheck.setOnAction(a -> setMenuText());
//
//            checkMenuItemsList.add(miCheck);
//            mbChannel.getItems().add(miCheck);
//        }
//        setMenuText();
//    }
//
//    private void clearMenuText() {
//        for (CheckMenuItem cmi : checkMenuItemsList) {
//            cmi.setSelected(false);
//        }
//        mbChannel.setText("");
//    }
//
//    private void setMenuText() {
//        String text = "";
//        for (CheckMenuItem cmi : checkMenuItemsList) {
//            if (cmi.isSelected()) {
//                text = text + (text.isEmpty() ? "" : ", ") + cmi.getText();
//            }
//        }
//        mbChannel.setText(text);
//    }
}
