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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.tools.storedFilter.FilterCheckRegEx;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilmFilterControllerTextFilter extends VBox {

    private final ComboBox<String> cboTheme = new ComboBox<>();
    private final MenuButton mbChannel = new MenuButton("");
    private final TextField txtThemeTitle = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtSomewhere = new TextField();
    private final TextField txtUrl = new TextField();

    private final ArrayList<MenuItemClass> menuItemsList = new ArrayList<>();

    private final ProgData progData;

    public FilmFilterControllerTextFilter() {
        super();
        progData = ProgData.getInstance();

        setPadding(new Insets(10, 15, 5, 15));
        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);

        // Sender, Thema, ..
        initSenderFilter();
        initStringFilter();
        addFilter();
    }

    private void initSenderFilter() {
        mbChannel.getStyleClass().add("channel-menu");
        mbChannel.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(mbChannel, Priority.ALWAYS);

        initChannelMenu();
        progData.storedFilters.getActFilterSettings().channelProperty().addListener((observable, oldValue, newValue) -> {
            initChannelMenu();
        });
        progData.worker.getAllChannelList().addListener((ListChangeListener<String>) c -> initChannelMenu());
        mbChannel.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelProperty());
    }

    private void initChannelMenu() {
        mbChannel.getItems().clear();
        menuItemsList.clear();

        List<String> channelFilterList = new ArrayList<>();
        String channelFilter = progData.storedFilters.getActFilterSettings().channelProperty().get();
        if (channelFilter != null) {
            if (channelFilter.contains(",")) {
                channelFilterList.addAll(Arrays.asList(channelFilter.replace(" ", "").toLowerCase().split(",")));
            } else {
                channelFilterList.add(channelFilter.toLowerCase());
            }
            channelFilterList.stream().forEach(s -> s = s.trim());
        }

        CheckBox miCheckAll = new CheckBox();
        miCheckAll.setVisible(false);

        Button btnAll = new Button("Auswahl löschen");
        btnAll.getStyleClass().add("channel-button");
        btnAll.setMaxWidth(Double.MAX_VALUE);
        btnAll.minWidthProperty().bind(mbChannel.widthProperty().add(-50));
        btnAll.setOnAction(e -> {
            clearMenuText();
            mbChannel.hide();
        });

        HBox hBoxAll = new HBox(10);
        hBoxAll.setAlignment(Pos.CENTER_LEFT);
        hBoxAll.getChildren().addAll(miCheckAll, btnAll);

        CustomMenuItem cmiAll = new CustomMenuItem(hBoxAll);
        mbChannel.getItems().add(cmiAll);

        for (String s : progData.worker.getAllChannelList()) {
            if (s.isEmpty()) {
                continue;
            }

            CheckBox miCheck = new CheckBox();
            if (channelFilterList.contains(s.toLowerCase())) {
                miCheck.setSelected(true);
            }
            miCheck.setOnAction(a -> setMenuText());

            MenuItemClass menuItemClass = new MenuItemClass(s, miCheck);
            menuItemsList.add(menuItemClass);

            Button btnChannel = new Button(s);
            btnChannel.getStyleClass().add("channel-button");
            btnChannel.setMaxWidth(Double.MAX_VALUE);
            btnChannel.minWidthProperty().bind(mbChannel.widthProperty().add(-50));
            btnChannel.setOnAction(e -> {
                setCheckBoxAndMenuText(menuItemClass);
                mbChannel.hide();
            });

            HBox hBox = new HBox(10);
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(miCheck, btnChannel);

            CustomMenuItem cmi = new CustomMenuItem(hBox);
            mbChannel.getItems().add(cmi);
        }
    }

    private void setCheckBoxAndMenuText(MenuItemClass cmi) {
        for (MenuItemClass cm : menuItemsList) {
            cm.getCheckBox().setSelected(false);
        }
        cmi.getCheckBox().setSelected(true);
        setMenuText();
    }

    private void clearMenuText() {
        for (MenuItemClass cmi : menuItemsList) {
            cmi.getCheckBox().setSelected(false);
        }
        mbChannel.setText("");
        progData.worker.createThemeList("");
    }

    private void setMenuText() {
        String text = "";
        for (MenuItemClass cmi : menuItemsList) {
            if (cmi.getCheckBox().isSelected()) {
                text = text + (text.isEmpty() ? "" : ", ") + cmi.getText();
            }
        }
        mbChannel.setText(text);
        progData.worker.createThemeList(text);
    }

    private void initStringFilter() {
        cboTheme.editableProperty().bind(progData.storedFilters.getActFilterSettings().themeExactProperty().not());
        cboTheme.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cboTheme.setVisibleRowCount(25);
        cboTheme.setItems(progData.worker.getThemeForChannelList());

        cboTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!progData.storedFilters.getActFilterSettings().themeExactProperty().getValue()) {
                return;
            }

            progData.storedFilters.getActFilterSettings().setTheme(cboTheme.valueProperty().getValue());
        });
        cboTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                progData.storedFilters.getActFilterSettings().setTheme(cboTheme.getEditor().getText());
            }
        });
        cboTheme.getEditor().setOnKeyPressed(event -> {
            if (ProgConfig.SYSTEM_FILTER_RETURN.getValue() && event.getCode() == KeyCode.ENTER) {
                progData.storedFilters.getActFilterSettings().setTheme(cboTheme.getEditor().getText());
            }
        });
        progData.storedFilters.getActFilterSettings().themeProperty().addListener((observable, oldValue, newValue) -> {
                    cboTheme.valueProperty().setValue(progData.storedFilters.getActFilterSettings().getTheme());
                }
        );
        cboTheme.valueProperty().setValue(progData.storedFilters.getActFilterSettings().getTheme());
        progData.worker.getThemeForChannelList().addListener((ListChangeListener<String>) c -> {
            cboTheme.valueProperty().setValue(progData.storedFilters.getActFilterSettings().getTheme());
        });

        txtThemeTitle.textProperty().addListener((u, o, n) -> {
            if (!ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                progData.storedFilters.getActFilterSettings().setThemeTitle(txtThemeTitle.getText());
            }
        });
        txtThemeTitle.setOnKeyPressed(event -> {
            if (ProgConfig.SYSTEM_FILTER_RETURN.getValue() && event.getCode() == KeyCode.ENTER) {
                progData.storedFilters.getActFilterSettings().setThemeTitle(txtThemeTitle.getText());
            }
        });
        progData.storedFilters.getActFilterSettings().themeTitleProperty().addListener((u, o, n) ->
                txtThemeTitle.setText(progData.storedFilters.getActFilterSettings().getThemeTitle())
        );
        txtThemeTitle.setText(progData.storedFilters.getActFilterSettings().getThemeTitle());

        txtTitle.textProperty().addListener((u, o, n) -> {
            if (!ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                progData.storedFilters.getActFilterSettings().setTitle(txtTitle.getText());
            }
        });
        txtTitle.setOnKeyPressed(event -> {
            if (ProgConfig.SYSTEM_FILTER_RETURN.getValue() && event.getCode() == KeyCode.ENTER) {
                progData.storedFilters.getActFilterSettings().setTitle(txtTitle.getText());
            }
        });
        progData.storedFilters.getActFilterSettings().titleProperty().addListener((u, o, n) ->
                txtTitle.setText(progData.storedFilters.getActFilterSettings().getTitle())
        );
        txtTitle.setText(progData.storedFilters.getActFilterSettings().getTitle());

        txtSomewhere.textProperty().addListener((u, o, n) -> {
            if (!ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                progData.storedFilters.getActFilterSettings().setSomewhere(txtSomewhere.getText());
            }
        });
        txtSomewhere.setOnKeyPressed(event -> {
            if (ProgConfig.SYSTEM_FILTER_RETURN.getValue() && event.getCode() == KeyCode.ENTER) {
                progData.storedFilters.getActFilterSettings().setSomewhere(txtSomewhere.getText());
            }
        });
        progData.storedFilters.getActFilterSettings().somewhereProperty().addListener((u, o, n) -> {
                    txtSomewhere.setText(progData.storedFilters.getActFilterSettings().getSomewhere());
                }
        );
        txtSomewhere.setText(progData.storedFilters.getActFilterSettings().getSomewhere());

        txtUrl.textProperty().addListener((u, o, n) -> {
            if (!ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                progData.storedFilters.getActFilterSettings().setUrl(txtUrl.getText());
            }
        });
        txtUrl.setOnKeyPressed(event -> {
            if (ProgConfig.SYSTEM_FILTER_RETURN.getValue() && event.getCode() == KeyCode.ENTER) {
                progData.storedFilters.getActFilterSettings().setUrl(txtUrl.getText());
            }
        });
        progData.storedFilters.getActFilterSettings().urlProperty().addListener((u, o, n) ->
                txtUrl.setText(progData.storedFilters.getActFilterSettings().getUrl())
        );
        txtUrl.setText(progData.storedFilters.getActFilterSettings().getUrl());

        FilterCheckRegEx fTh = new FilterCheckRegEx(cboTheme.getEditor());
        cboTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fTh.checkPattern());
        FilterCheckRegEx fTT = new FilterCheckRegEx(txtThemeTitle);
        txtThemeTitle.textProperty().addListener((observable, oldValue, newValue) -> fTT.checkPattern());
        FilterCheckRegEx fT = new FilterCheckRegEx(txtTitle);
        txtTitle.textProperty().addListener((observable, oldValue, newValue) -> fT.checkPattern());
        FilterCheckRegEx fS = new FilterCheckRegEx(txtSomewhere);
        txtSomewhere.textProperty().addListener((observable, oldValue, newValue) -> fS.checkPattern());
        FilterCheckRegEx fU = new FilterCheckRegEx(txtUrl);
        txtUrl.textProperty().addListener((observable, oldValue, newValue) -> fU.checkPattern());
    }

    private void addFilter() {
        addTxt("Sender", mbChannel, this, progData.storedFilters.getActFilterSettings().channelVisProperty());
        addTxt("Thema", cboTheme, this, progData.storedFilters.getActFilterSettings().themeVisProperty());
        addTxt("Thema oder Titel", txtThemeTitle, this, progData.storedFilters.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", txtTitle, this, progData.storedFilters.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", txtSomewhere, this, progData.storedFilters.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", txtUrl, this, progData.storedFilters.getActFilterSettings().urlVisProperty());

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(0);
        sp.setMaxHeight(1);
        this.getChildren().add(sp);

        this.visibleProperty().bind(progData.storedFilters.getActFilterSettings().channelVisProperty()
                .or(progData.storedFilters.getActFilterSettings().themeVisProperty()
                        .or(progData.storedFilters.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.storedFilters.getActFilterSettings().titleVisProperty()
                                        .or(progData.storedFilters.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.storedFilters.getActFilterSettings().urlVisProperty())
                                        )
                                )
                        )
                ));
        this.managedProperty().bind(this.visibleProperty());
        sp.visibleProperty().bind(this.visibleProperty());
        sp.managedProperty().bind(this.visibleProperty());
    }

    private void addTxt(String txt, Control control, VBox vBoxComplete, BooleanProperty booleanProperty) {
        VBox vBox = new VBox(2);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);

        vBox.visibleProperty().bind(booleanProperty);
        vBox.managedProperty().bind(booleanProperty);
    }

    private class MenuItemClass {

        private final String text;
        private final CheckBox checkBox;

        MenuItemClass(String text, CheckBox checkbox) {
            this.text = text;
            this.checkBox = checkbox;
        }

        public String getText() {
            return text;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }
}
