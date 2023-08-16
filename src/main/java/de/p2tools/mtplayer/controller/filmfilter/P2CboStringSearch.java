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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.filter.P2CboSearcher;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class P2CboStringSearch extends ComboBox<P2CboSearcher> {
    final int MAX_FILTER_HISTORY = 15;
    private final StringProperty strSearchProperty;

    private final ProgData progData;

    public P2CboStringSearch(ProgData progData, StringProperty strSearchProperty) {
        this.progData = progData;
        this.strSearchProperty = strSearchProperty;
        setEditable(true);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setVisibleRowCount(MAX_FILTER_HISTORY);
        for (int i = 0; i < 15; ++i) {
            getItems().add(new P2CboSearcher());
        }
        init();
    }

    public void addSearchStrings(List<String> list) {
        List<P2CboSearcher> sList = new ArrayList<>();
        list.forEach(s -> sList.add(new P2CboSearcher(s)));
        getItems().setAll(sList);
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
                        strSearchProperty.setValue(getSelectionModel().getSelectedItem().value);
                        progData.filmFilterWorker.getActFilterSettings().reportFilterReturn();
                    }
                }
        );
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                progData.filmFilterWorker.getActFilterSettings().reportFilterReturn();
            }
        });

        strSearchProperty.addListener((u, o, n) -> {
            if (isEditable()) {
                getEditor().setText(strSearchProperty.getValue());
            } else {
            }
        });
        getEditor().setText(strSearchProperty.getValue());
    }

    private boolean stop = false;

    private synchronized void addLastFilter() {
        final String filterStr = getEditor().getText();

        if (filterStr == null || filterStr.isEmpty()) {
            System.out.println("=====> null/empty");
            return;
        }

        for (int i = getItems().size() - 2; i >= 1; --i) {
            getItems().get(i + 1).value = getItems().get(i).value;
        }
        getItems().get(1).value = filterStr;

        stop = false;
    }


//    private synchronized void addLastFilter_() {
//        if (stop) {
//            System.out.println("==========");
//            System.out.println(" stop ");
//            System.out.println("==========");
//            return;
//        }
//        stop = true;
//        final String filterStr = strSearchProperty.getValueSafe();
//
//        if (filterStr == null || filterStr.isEmpty()) {
//            System.out.println("=====> null/empty");
//            stop = false;
//            return;
//        }
//
//        final ObservableList<String> filterList = FXCollections.observableArrayList();
//        filterList.addAll(getItems());
//
//        if (filterList.size() <= 1) {
//            if (filterList.stream().noneMatch(string -> string.equals(filterStr))) {
//                // dann gibts schon mal keine doppelte
//                filterList.add(1, filterStr);
//                setItems(filterList);
//            }
//            stop = false;
//            return;
//        }
//
//        System.out.println("==Filter== " + ++filter);
//        if (filterStr.contains(filterList.get(1))) {
//            // aktueller Filter enthält den letzten Filter: also weiter getippt
//            filterList.remove(1);
//            filterList.add(1, filterStr);
//        } else {
//            filterList.add(1, filterStr);
//        }
//        while (filterList.size() >= MAX_FILTER_HISTORY) {
//            filterList.remove(filterList.size() - 1); // den letzten entfernen
//        }
//        setItems(filterList);
//        stop = false;
//    }

    int filter = 0;


}
