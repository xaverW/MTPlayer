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


package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;

public class P2CboStringSearch extends ComboBox<String> {
    final int MAX_FILTER_HISTORY = 15;
    private final StringProperty strSearchProperty;

    private final ProgData progData;

    public P2CboStringSearch(ProgData progData, StringProperty strSearchProperty) {
        this.progData = progData;
        this.strSearchProperty = strSearchProperty;
        setEditable(true);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setVisibleRowCount(MAX_FILTER_HISTORY);
        setItems(FXCollections.observableArrayList(""));
        init();
    }

    private void init() {
        FilterCheckRegEx regEx = new FilterCheckRegEx(getEditor());
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            System.out.println("===> new Value: " + newValue);
            regEx.checkPattern();
            strSearchProperty.setValue(getEditor().getText());
            addLastFilter();
        });
        getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (getSelectionModel().getSelectedIndex() >= 0) {
                        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                            //sonst wird erst nach "RETURN" gestartet
                            addLastFilter();
                            progData.filmFilterWorker.getActFilterSettings().reportFilterReturn();
                        }
                    }
                }
        );
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addLastFilter();
                progData.filmFilterWorker.getActFilterSettings().reportFilterReturn();
            }
        });

        strSearchProperty.addListener((u, o, n) -> valueProperty().setValue(strSearchProperty.getValueSafe()));
        getEditor().setText(strSearchProperty.getValue());
//        getEditor().textProperty().bindBidirectional(strSearchProperty);
    }

    private boolean stop = false;

    private synchronized void addLastFilter() {
        if (stop) {
            System.out.println("==========");
            System.out.println(" stop ");
            System.out.println("==========");
            return;
        }
        stop = true;
        final String filterStr = strSearchProperty.getValueSafe();

        if (filterStr == null || filterStr.isEmpty()) {
            System.out.println("=====> null/empty");
            stop = false;
            return;
        }

        final ObservableList<String> filterList = FXCollections.observableArrayList();
        filterList.addAll(getItems());

        if (filterList.size() <= 1) {
            if (filterList.stream().noneMatch(string -> string.equals(filterStr))) {
                // dann gibts schon mal keine doppelte
                filterList.add(filterStr);
                setItems(filterList);
            }
            stop = false;
            return;
        }

        System.out.println("==Filter== " + ++filter);
        if (filterStr.contains(filterList.get(filterList.size() - 1))) {
            // aktueller Filter enthält den letzten Filter: also weiter getippt
            filterList.remove(filterList.size() - 1);
            filterList.add(filterStr);
        } else {
            filterList.add(filterStr);
        }
        while (filterList.size() >= MAX_FILTER_HISTORY) {
            filterList.remove(1); // den letzten entfernen
        }
        setItems(filterList);
        stop = false;
    }

    int filter = 0;
}
