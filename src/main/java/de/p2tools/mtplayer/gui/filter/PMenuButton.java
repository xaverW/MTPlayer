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


package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgData;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PMenuButton extends MenuButton {

    private final ArrayList<MenuItemClass> menuItemsList = new ArrayList<>();
    private final StringProperty filter;
    private final ObservableList<String> allFilterList;

    public PMenuButton(StringProperty filter, ObservableList<String> allFilterList) {
        this.filter = filter;
        this.allFilterList = allFilterList;
        initMenuButton();
    }

    public PMenuButton(StringProperty filter, ObservableList<String> allFilterList, boolean minWidth) {
        this.filter = filter;
        this.allFilterList = allFilterList;
        initMenuButton();
        if (minWidth) {
            setMaxWidth(-1);
            setMinWidth(200);
        }
    }

    private void initMenuButton() {
        getStyleClass().add("cbo-menu");
        setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(this, Priority.ALWAYS);

        updateMenuButton();
        filter.addListener((observable, oldValue, newValue) -> {
            updateMenuButton();
        });
        allFilterList.addListener((ListChangeListener<String>) c ->
                updateMenuButton());
        textProperty().bindBidirectional(filter);
    }

    public void updateMenuButton() {
        getItems().clear();
        menuItemsList.clear();

        List<String> filterList = new ArrayList<>();
        if (filter.getValueSafe() != null) {
            if (filter.getValueSafe().contains(",")) {
                filterList.addAll(Arrays.asList(filter.getValueSafe().replace(" ", "").toLowerCase().split(",")));
            } else {
                filterList.add(filter.getValueSafe().toLowerCase());
            }
            filterList.forEach(s -> s = s.trim());
        }

        CheckBox miCheckAll = new CheckBox();
        miCheckAll.setVisible(false);

        Button btnClear = new Button("Auswahl löschen");
        btnClear.getStyleClass().add("cbo-menu-button");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.minWidthProperty().bind(widthProperty().add(-50));
        btnClear.setOnAction(e -> {
            clearMenuText();
            hide();
        });

        HBox hBoxAll = new HBox(5);
        hBoxAll.setAlignment(Pos.CENTER_LEFT);
        hBoxAll.getChildren().addAll(miCheckAll, btnClear);

        CustomMenuItem cmiAll = new CustomMenuItem(hBoxAll);
        cmiAll.getStyleClass().add("cbo-menu-item");
        getItems().add(cmiAll);

        for (String s : allFilterList) {
            if (s.isEmpty()) {
                continue;
            }

            CheckBox miCheck = new CheckBox();
            if (filterList.contains(s.toLowerCase())) {
                miCheck.setSelected(true);
            }
            miCheck.setOnAction(a -> setMenuText());

            MenuItemClass menuItemClass = new MenuItemClass(s, miCheck);
            menuItemsList.add(menuItemClass);

            Button btnChannel = new Button(s);
            btnChannel.getStyleClass().add("cbo-menu-button");
            btnChannel.setMaxWidth(Double.MAX_VALUE);
            btnChannel.minWidthProperty().bind(widthProperty().add(-50));
            btnChannel.setOnAction(e -> {
                setCheckBoxAndMenuText(menuItemClass);
                hide();
            });

            HBox hBox = new HBox(5);
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(miCheck, btnChannel);

            CustomMenuItem cmi = new CustomMenuItem(hBox);
            cmi.getStyleClass().add("cbo-menu-item");
            getItems().add(cmi);
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
        setText("");
        ProgData.getInstance().worker.createThemeList("");
    }

    private void setMenuText() {
        StringBuilder text = new StringBuilder();
        for (MenuItemClass cmi : menuItemsList) {
            if (cmi.getCheckBox().isSelected()) {
                text.append((text.isEmpty()) ? "" : ", ").append(cmi.getText());
            }
        }
        setText(text.toString());
        ProgData.getInstance().worker.createThemeList(text.toString());
    }

    private static class MenuItemClass {
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
