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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgData;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilmFilterControllerTextFilter extends VBox {

    private final ComboBox<String> cboChannel = new ComboBox<>();
    private final ComboBox<String> cbxTheme = new ComboBox<>();
    private final TextField txtThemeTitle = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtSomewhere = new TextField();
    private final TextField txtUrl = new TextField();

    private final MenuButton mbChannel = new MenuButton("");
    private final ArrayList<ChannelMenu> checkMenuItemsList = new ArrayList<>();

    private final ProgData progData;

    public FilmFilterControllerTextFilter() {
        super();
        progData = ProgData.getInstance();

        setPadding(new Insets(15, 15, 15, 15));
        setSpacing(20);

        // Sender, Thema, ..
        initSenderFilter();
        initStringFilter();
        addFilter();
    }

    private void initSenderFilter() {
        mbChannel.getStyleClass().add("channel-menu");
        mbChannel.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(mbChannel, Priority.ALWAYS);
        initSenderMenu();
        progData.storedFilters.getActFilterSettings().channelProperty().addListener((observable, oldValue, newValue) -> {
            initSenderMenu();
        });
        progData.worker.getAllChannelList().addListener((ListChangeListener<String>) c -> initSenderMenu());


        cboChannel.setMaxWidth(Double.MAX_VALUE);
        cboChannel.setVisibleRowCount(25);
        cboChannel.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!cboChannel.isVisible()) {
                // dann ist das andere Sendermenü aktiv
                return;
            }

            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    progData.worker.createThemeList("");
                } else {
                    cbxTheme.getSelectionModel().select("");
                    progData.worker.createThemeList(newValue);
                }
            }
        });
        cboChannel.setItems(progData.worker.getAllChannelList());

        mbChannel.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelProperty());
        cboChannel.valueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelProperty());
        progData.storedFilters.getActFilterSettings().channelExactProperty().addListener(l -> {
            progData.storedFilters.getActFilterSettings().channelProperty().setValue("");
        });
    }

    private void initSenderMenu() {
        mbChannel.getItems().clear();
        checkMenuItemsList.clear();

        List<String> senderArr = new ArrayList<>();
        String sender = progData.storedFilters.getActFilterSettings().channelProperty().get();
        if (sender != null) {
            if (sender.contains(",")) {
                senderArr.addAll(Arrays.asList(sender.replace(" ", "").toLowerCase().split(",")));
            } else {
                senderArr.add(sender.toLowerCase());
            }
            senderArr.stream().forEach(s -> s = s.trim());
        }

        MenuItem mi = new MenuItem("");
        mi.setOnAction(a -> clearMenuText());
        mbChannel.getItems().add(mi);

        Label sizeLabel = new Label();

        for (String s : progData.worker.getAllChannelList()) {
            if (s.isEmpty()) {
                continue;
            }
            CheckBox miCheck = new CheckBox();
            Label miLabel = new Label(s);
            miLabel.setStyle("-fx-border-color: red;");
            miLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(miLabel, Priority.ALWAYS);

            miLabel.minWidthProperty().bindBidirectional(sizeLabel.minWidthProperty());
            miLabel.prefWidthProperty().bindBidirectional(sizeLabel.prefWidthProperty());

            ChannelMenu channelMenu = new ChannelMenu(s, miCheck, miLabel);

            if (senderArr.contains(s.toLowerCase())) {
                miCheck.setSelected(true);
            }
            miCheck.setOnAction(a -> setMenuText());

//            miLabel.setOnMouseClicked(a -> {
//                setMenuButtonText(channelMenu);
//                mbChannel.hide();
//            });

            HBox hBox = new HBox(10);
            hBox.setMaxWidth(Double.MAX_VALUE);
            hBox.setStyle("-fx-border-color: green;");
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(miCheck, miLabel);

            Button mb = new Button("Test");
            mb.setOnAction(a -> {
                setMenuButtonText(channelMenu);
                mbChannel.hide();
            });
            hBox.getChildren().add(mb);

            checkMenuItemsList.add(channelMenu);

            CustomMenuItem cmi = new CustomMenuItem(hBox);
            cmi.setOnAction(a -> {
                setMenuButtonText(channelMenu);
                mbChannel.hide();
            });
            cmi.setStyle("-fx-border-color: blue;");
            mbChannel.getItems().add(cmi);
        }
    }

    private void clearMenuText() {
        for (ChannelMenu cmi : checkMenuItemsList) {
            cmi.getCheckBox().setSelected(false);
        }
        mbChannel.setText("");
        progData.worker.createThemeList("");
    }

    private void setMenuText() {
        String text = "";
        for (ChannelMenu cmi : checkMenuItemsList) {
            if (cmi.getCheckBox().isSelected()) {
                text = text + (text.isEmpty() ? "" : ", ") + cmi.getName();
            }
        }
        mbChannel.setText(text);
        progData.worker.createThemeList(text);
    }

    private void setMenuButtonText(ChannelMenu cmi) {
        for (ChannelMenu cm : checkMenuItemsList) {
            cm.getCheckBox().setSelected(false);
        }

        cmi.getCheckBox().setSelected(true);
        setMenuText();
    }

    private void initStringFilter() {
        cbxTheme.editableProperty().bind(progData.storedFilters.getActFilterSettings().themeExactProperty().not());
        cbxTheme.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbxTheme.setVisibleRowCount(25);
        cbxTheme.valueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeProperty());
        cbxTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                progData.storedFilters.getActFilterSettings().setTheme(newValue);
            }
        });
        cbxTheme.setItems(progData.worker.getThemeForChannelList());

        txtThemeTitle.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeTitleProperty());
        txtTitle.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().titleProperty());
        txtSomewhere.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().somewhereProperty());
        txtUrl.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().urlProperty());
    }

    private void addFilter() {
        VBox vBox = new VBox(10);
        addChannel(vBox);
        addTxt("Thema", cbxTheme, vBox, progData.storedFilters.getActFilterSettings().themeVisProperty());
        addTxt("Thema oder Titel", txtThemeTitle, vBox, progData.storedFilters.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", txtTitle, vBox, progData.storedFilters.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", txtSomewhere, vBox, progData.storedFilters.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", txtUrl, vBox, progData.storedFilters.getActFilterSettings().urlVisProperty());

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(10);
        vBox.getChildren().add(sp);

        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().channelVisProperty()
                .or(progData.storedFilters.getActFilterSettings().themeVisProperty()
                        .or(progData.storedFilters.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.storedFilters.getActFilterSettings().titleVisProperty()
                                        .or(progData.storedFilters.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.storedFilters.getActFilterSettings().urlVisProperty())
                                        )
                                )
                        )
                ));
        vBox.managedProperty().bind(vBox.visibleProperty());

        sp.visibleProperty().bind(vBox.visibleProperty());
        sp.managedProperty().bind(vBox.visibleProperty());


        getChildren().add(vBox);
    }

    private void addTxt(String txt, Control control, VBox vBoxComplete, BooleanProperty booleanProperty) {
        VBox vBox = new VBox();
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);

        vBox.visibleProperty().bind(booleanProperty);
        vBox.managedProperty().bind(booleanProperty);
    }

    private void addChannel(VBox vBoxComplete) {
        VBox vBox = new VBox();
        Label label = new Label("Sender");
        vBox.getChildren().addAll(label, mbChannel, cboChannel);
        vBoxComplete.getChildren().add(vBox);

        mbChannel.visibleProperty().bind(progData.storedFilters.getActFilterSettings().channelExactProperty().not());
        mbChannel.managedProperty().bind(progData.storedFilters.getActFilterSettings().channelExactProperty().not());
        cboChannel.visibleProperty().bind(progData.storedFilters.getActFilterSettings().channelExactProperty());
        cboChannel.managedProperty().bind(progData.storedFilters.getActFilterSettings().channelExactProperty());

        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().channelVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().channelVisProperty());
    }

    private class ChannelMenu {
        private String name = "";
        private CheckBox checkBox;
        private Label button;

        ChannelMenu(String name, CheckBox checkBox, Label button) {
            this.name = name;
            this.checkBox = checkBox;
            this.button = button;
        }

        public String getName() {
            return name;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public Label getButton() {
            return button;
        }
    }

}
