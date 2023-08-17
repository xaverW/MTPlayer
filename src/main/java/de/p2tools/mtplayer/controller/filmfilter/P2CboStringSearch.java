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
        for (int i = 0; i < MAX_FILTER_HISTORY; ++i) {
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
            if (!this.isShowing()) {
                // dann ist eine Auswahl aus der Combo
                addLastFilter();
            }
            regEx.checkPattern();
            strSearchProperty.setValue(getEditor().getText());
        });
        getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (getSelectionModel().getSelectedIndex() >= 0) {
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

    private synchronized void addLastFilter() {
        final String filterStr = getEditor().getText();
        if (filterStr == null || filterStr.isEmpty()) {
            System.out.println("=====> null/empty");
            return;
        }

        P2CboSearcher tmp = getItems().get(1);
        if (filterStr.contains(tmp.getValue())) {
            // dann wird der erste damit ersetzt
            tmp.setValue(filterStr);
            return;
        }

        // dann wird jetzt einfach weitergeschaltet
        for (int i = getItems().size() - 2; i >= 1; --i) {
            getItems().get(i + 1).setValue(getItems().get(i).getValue());
        }
        getItems().get(1).setValue(filterStr);
    }
}
