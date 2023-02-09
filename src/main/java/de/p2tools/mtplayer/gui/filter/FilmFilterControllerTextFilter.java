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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.mtFilter.FilterCheckRegEx;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
    private final ComboBox<String> cboThemeTitle = new ComboBox();
    private final ComboBox<String> cboTitle = new ComboBox();
    private final ComboBox<String> cboSomewhere = new ComboBox();
    private final ComboBox<String> cboUrl = new ComboBox();

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
        mbChannel.getStyleClass().add("cbo-menu");
        mbChannel.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(mbChannel, Priority.ALWAYS);

        initChannelMenu();
        progData.actFilmFilterWorker.getActFilterSettings().channelProperty().addListener((observable, oldValue, newValue) -> {
            initChannelMenu();
        });
        progData.worker.getAllChannelList().addListener((ListChangeListener<String>) c -> initChannelMenu());
        mbChannel.textProperty().bindBidirectional(progData.actFilmFilterWorker.getActFilterSettings().channelProperty());
    }

    private void initChannelMenu() {
        mbChannel.getItems().clear();
        menuItemsList.clear();

        List<String> channelFilterList = new ArrayList<>();
        String channelFilter = progData.actFilmFilterWorker.getActFilterSettings().channelProperty().get();
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
        btnAll.getStyleClass().add("cbo-menu-button");
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
            btnChannel.getStyleClass().add("cbo-menu-button");
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
        //Theme
        //https://bugs.openjdk.java.net/browse/JDK-8116061
//        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
//            progData.storedFilters.getActFilterSettings().setThemeExact(true);
//        }
//        ProgConfig.SYSTEM_FILTER_RETURN.addListener((u, o, n) -> {
//            if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
//                progData.storedFilters.getActFilterSettings().setThemeExact(true);
//            } else {
//
//            }
//        });

        cboTheme.editableProperty().bind(progData.actFilmFilterWorker.getActFilterSettings().themeExactProperty().not());
        cboTheme.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cboTheme.setVisibleRowCount(25);
        cboTheme.setItems(progData.worker.getThemeForChannelList());
        cboTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.actFilmFilterWorker.getActFilterSettings().themeExactProperty().getValue()) {
                progData.actFilmFilterWorker.getActFilterSettings().setTheme(cboTheme.valueProperty().getValue());
            }
        });
        cboTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            progData.actFilmFilterWorker.getActFilterSettings().setTheme(cboTheme.getEditor().getText());
        });
        cboTheme.getEditor().setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                progData.actFilmFilterWorker.getActFilterSettings().reportFilterReturn();
            }
        });
        progData.actFilmFilterWorker.getActFilterSettings().themeProperty().addListener((observable, oldValue, newValue) -> {
            cboTheme.valueProperty().setValue(progData.actFilmFilterWorker.getActFilterSettings().getTheme());
        });
        cboTheme.valueProperty().setValue(progData.actFilmFilterWorker.getActFilterSettings().getTheme());
        progData.worker.getThemeForChannelList().addListener((ListChangeListener<String>) c -> {
            cboTheme.valueProperty().setValue(progData.actFilmFilterWorker.getActFilterSettings().getTheme());
        });


        //ThemeTitle
//        cboThemeTitle.setEditable(true);
//        cboThemeTitle.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//        cboThemeTitle.setVisibleRowCount(25);
//        cboThemeTitle.setItems(progData.storedFilters.getLastThemaTitleFilter());
//        cboThemeTitle.getEditor().textProperty().addListener((u, o, n) -> {
//            progData.storedFilters.getActFilterSettings().setThemeTitle(cboThemeTitle.getEditor().getText());
//        });
//        cboThemeTitle.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                progData.storedFilters.getActFilterSettings().reportFilterReturn();
//            }
//        });
//        progData.storedFilters.getActFilterSettings().themeTitleProperty().addListener((u, o, n) -> {
//            cboThemeTitle.valueProperty().setValue(progData.storedFilters.getActFilterSettings().getThemeTitle());
//        });
//        cboThemeTitle.getEditor().setText(progData.storedFilters.getActFilterSettings().getThemeTitle());

        addTextFilter(cboThemeTitle, progData.actFilmFilterWorker.getLastThemaTitleFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().themeTitleProperty());

        //Title
//        txtTitle.textProperty().addListener((u, o, n) -> {
//            progData.storedFilters.getActFilterSettings().setTitle(txtTitle.getText());
//        });
//        txtTitle.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                progData.storedFilters.getActFilterSettings().reportFilterReturn();
//            }
//
//        });
//        progData.storedFilters.getActFilterSettings().titleProperty().addListener((u, o, n) -> {
//            if (!txtTitle.getText().equals(progData.storedFilters.getActFilterSettings().getTitle())) {
//                txtTitle.setText(progData.storedFilters.getActFilterSettings().getTitle());
//            }
//        });
//        txtTitle.setText(progData.storedFilters.getActFilterSettings().getTitle());

        addTextFilter(cboTitle, progData.actFilmFilterWorker.getLastTitleFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().titleProperty());


        //Somewhere
//        txtSomewhere.textProperty().addListener((u, o, n) -> {
//            progData.storedFilters.getActFilterSettings().setSomewhere(txtSomewhere.getText());
//        });
//        txtSomewhere.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                progData.storedFilters.getActFilterSettings().reportFilterReturn();
//            }
//        });
//        progData.storedFilters.getActFilterSettings().somewhereProperty().addListener((u, o, n) -> {
//            if (!txtSomewhere.getText().equals(progData.storedFilters.getActFilterSettings().getSomewhere())) {
//                txtSomewhere.setText(progData.storedFilters.getActFilterSettings().getSomewhere());
//            }
//        });
//        txtSomewhere.setText(progData.storedFilters.getActFilterSettings().getSomewhere());

        addTextFilter(cboSomewhere, progData.actFilmFilterWorker.getLastSomewhereFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().somewhereProperty());


        //URL
//        txtUrl.textProperty().addListener((u, o, n) -> {
//            progData.storedFilters.getActFilterSettings().setUrl(txtUrl.getText());
//        });
//        txtUrl.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                progData.storedFilters.getActFilterSettings().reportFilterReturn();
//            }
//        });
//        progData.storedFilters.getActFilterSettings().urlProperty().addListener((u, o, n) -> {
//            if (!txtUrl.getText().equals(progData.storedFilters.getActFilterSettings().getUrl())) {
//                txtUrl.setText(progData.storedFilters.getActFilterSettings().getUrl());
//            }
//        });
//        txtUrl.setText(progData.storedFilters.getActFilterSettings().getUrl());
        addTextFilter(cboUrl, progData.actFilmFilterWorker.getLastUrlFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().urlProperty());

        FilterCheckRegEx fTh = new FilterCheckRegEx(cboTheme.getEditor());
        cboTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fTh.checkPattern());
        FilterCheckRegEx fTT = new FilterCheckRegEx(cboThemeTitle.getEditor());
        cboThemeTitle.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fTT.checkPattern());
        FilterCheckRegEx fT = new FilterCheckRegEx(cboTitle.getEditor());
        cboTitle.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fT.checkPattern());
        FilterCheckRegEx fS = new FilterCheckRegEx(cboSomewhere.getEditor());
        cboSomewhere.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fS.checkPattern());
        FilterCheckRegEx fU = new FilterCheckRegEx(cboUrl.getEditor());
        cboUrl.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fU.checkPattern());
    }


    boolean klick = false;
    int count = 0;

    private void addTextFilter(ComboBox<String> cbo, ObservableList<String> items, StringProperty strProp) {
        cbo.setEditable(true);
        cbo.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbo.setVisibleRowCount(15);
        cbo.setItems(items);
        cbo.getEditor().setText(strProp.getValue());

        cbo.getEditor().textProperty().addListener((u, o, n) -> {
            if (strProp.getValueSafe().equals(cbo.getEditor().getText())) {
                return;
            }
            strProp.setValue(cbo.getEditor().getText());
        });
        cbo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (cbo.getSelectionModel().getSelectedIndex() >= 0) {
                        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                            //dann wird erst nach "RETURN" gestartet
                            progData.actFilmFilterWorker.getActFilterSettings().reportFilterReturn();
                        }
                    }
                }
        );

        cbo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                progData.actFilmFilterWorker.getActFilterSettings().reportFilterReturn();
            }
        });
        strProp.addListener((u, o, n) -> cbo.valueProperty().setValue(strProp.getValueSafe()));
    }

    private void addFilter() {
        addTxt("Sender", mbChannel, this, progData.actFilmFilterWorker.getActFilterSettings().channelVisProperty());
        addTxt("Thema", cboTheme, this, progData.actFilmFilterWorker.getActFilterSettings().themeVisProperty());
        addTxt("Thema oder Titel", cboThemeTitle, this, progData.actFilmFilterWorker.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", cboTitle, this, progData.actFilmFilterWorker.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", cboSomewhere, this, progData.actFilmFilterWorker.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", cboUrl, this, progData.actFilmFilterWorker.getActFilterSettings().urlVisProperty());

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(0);
        sp.setMaxHeight(1);
        this.getChildren().add(sp);

        this.visibleProperty().bind(progData.actFilmFilterWorker.getActFilterSettings().channelVisProperty()
                .or(progData.actFilmFilterWorker.getActFilterSettings().themeVisProperty()
                        .or(progData.actFilmFilterWorker.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.actFilmFilterWorker.getActFilterSettings().titleVisProperty()
                                        .or(progData.actFilmFilterWorker.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.actFilmFilterWorker.getActFilterSettings().urlVisProperty())
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
