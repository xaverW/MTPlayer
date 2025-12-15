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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.function.BooleanSupplier;

public class FastFilter extends HBox {
    private final Button btnSearch = new Button();
    private final Button btnSelectWhere = new Button();
    private final Button btnClear = new Button();
    private final PCboString cboSearch;
    private final Label lblTitel = new Label("");

    private final ProgData progData;
    private final boolean audio;

    public FastFilter(boolean audio) {
        this.progData = ProgData.getInstance();
        this.audio = audio;

        final BooleanSupplier booleanSupplier = () -> {
            if (audio) {
                progData.filterWorkerAudio.getFastFilterSettings().reportFilterReturn();
            } else {
                progData.filterWorkerFilm.getFastFilterSettings().reportFilterReturn();
            }
            return true;
        };
        if (audio) {
            cboSearch = new PCboString(progData.stringFilterLists.getFilterListFastFilterAudio(),
                    progData.filterWorkerAudio.getFastFilterSettings().filterTermProperty(), booleanSupplier);
        } else {
            cboSearch = new PCboString(progData.stringFilterLists.getFilterListFastFilterFilm(),
                    progData.filterWorkerFilm.getFastFilterSettings().filterTermProperty(), booleanSupplier);
        }
        make();
    }

    private void make() {
        if (audio) {
            ProgConfig.FAST_AUDIO_SEARCH_ON.addListener((u, o, n) -> setFastSearchOnOff(true));
            ProgConfig.FAST_AUDIO_SEARCH_WHERE.addListener((u, o, n) -> setLblText());
            ProgConfig.AUDIO__FILTER_IS_SHOWING.addListener((observable, oldValue, newValue) -> {
                if (ProgConfig.AUDIO__FILTER_IS_SHOWING.getValue() && ProgConfig.FAST_AUDIO_SEARCH_ON.getValue()) {
                    // dann das Suchfeld wieder ausschalten
                    ProgConfig.FAST_AUDIO_SEARCH_ON.setValue(false);
                }
            });
        } else {
            ProgConfig.FAST_FILM_SEARCH_ON.addListener((u, o, n) -> setFastSearchOnOff(true));
            ProgConfig.FAST_FILM_SEARCH_WHERE.addListener((u, o, n) -> setLblText());
            ProgConfig.FILM__FILTER_IS_SHOWING.addListener((observable, oldValue, newValue) -> {
                if (ProgConfig.FILM__FILTER_IS_SHOWING.getValue() && ProgConfig.FAST_FILM_SEARCH_ON.getValue()) {
                    // dann das Suchfeld wieder ausschalten
                    ProgConfig.FAST_FILM_SEARCH_ON.setValue(false);
                }
            });
        }

        initText();
        initListener();
        setLblText();
        setFastSearchOnOff(false);
    }

    private void initText() {
        HBox hBoxTitle = new HBox(10);
        hBoxTitle.setAlignment(Pos.CENTER_LEFT);
        hBoxTitle.setPadding(new Insets(0));
        hBoxTitle.getChildren().addAll(btnSelectWhere, lblTitel);

        HBox hBoxCbo = new HBox(5);
        hBoxCbo.setAlignment(Pos.CENTER_RIGHT);
        hBoxCbo.setPadding(new Insets(0));
        hBoxCbo.getChildren().addAll(cboSearch, btnClear);
        HBox.setHgrow(cboSearch, Priority.ALWAYS);

        VBox vBox = new VBox(3);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setPadding(new Insets(0));
        vBox.getChildren().addAll(hBoxTitle, hBoxCbo);
        if (audio) {
            vBox.visibleProperty().bind(ProgConfig.FAST_AUDIO_SEARCH_ON);
            vBox.managedProperty().bind(ProgConfig.FAST_AUDIO_SEARCH_ON);
        } else {
            vBox.visibleProperty().bind(ProgConfig.FAST_FILM_SEARCH_ON);
            vBox.managedProperty().bind(ProgConfig.FAST_FILM_SEARCH_ON);
        }

        btnSearch.setGraphic(PIconFactory.PICON.BTN_SEARCH.getFontIcon());
        btnSearch.getStyleClass().addAll("pFuncBtn");
        btnSelectWhere.setGraphic(PIconFactory.PICON.BTN_UP_DOWN_H.getFontIcon());
        btnSelectWhere.getStyleClass().addAll("selectButton");
        btnClear.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        lblTitel.setFont(new Font(10));

        setAlignment(Pos.CENTER_RIGHT);
        setPadding(new Insets(1, 2, 1, 2));
        setSpacing(10);
        getChildren().addAll(vBox, btnSearch);
    }

    private void initListener() {
        btnSearch.setOnMouseClicked(mouseEvent -> {
            if (audio) {
                ProgConfig.FAST_AUDIO_SEARCH_ON.setValue(!ProgConfig.FAST_AUDIO_SEARCH_ON.getValue());
                ProgConfig.AUDIO__FILTER_IS_SHOWING.setValue(!ProgConfig.FAST_AUDIO_SEARCH_ON.getValue());
            } else {
                ProgConfig.FAST_FILM_SEARCH_ON.setValue(!ProgConfig.FAST_FILM_SEARCH_ON.getValue());
                ProgConfig.FILM__FILTER_IS_SHOWING.setValue(!ProgConfig.FAST_FILM_SEARCH_ON.getValue());
            }
        });
        btnSelectWhere.setOnMouseClicked(event -> {
            // beim Umschalten des FastFilters
            if (audio) {
                if (ProgConfig.FAST_AUDIO_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_TITLE) {
                    ProgConfig.FAST_AUDIO_SEARCH_WHERE.setValue(ProgConst.SEARCH_FAST_THEME_TITLE);
                } else {
                    ProgConfig.FAST_AUDIO_SEARCH_WHERE.setValue(1 + ProgConfig.FAST_AUDIO_SEARCH_WHERE.getValue());
                }
                ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_AUDIO_CHANGED);

            } else {
                if (ProgConfig.FAST_FILM_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_TITLE) {
                    ProgConfig.FAST_FILM_SEARCH_WHERE.setValue(ProgConst.SEARCH_FAST_THEME_TITLE);
                } else {
                    ProgConfig.FAST_FILM_SEARCH_WHERE.setValue(1 + ProgConfig.FAST_FILM_SEARCH_WHERE.getValue());
                }
                ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_FILM_CHANGED);
            }
        });
        btnClear.setOnAction(a -> {
            if (audio) {
                progData.filterWorkerAudio.getFastFilterSettings().clearFilter();
            } else {
                progData.filterWorkerFilm.getFastFilterSettings().clearFilter();
            }
        });
    }

    private void setFastSearchOnOff(boolean andSearch) {
        if (audio && ProgConfig.FAST_AUDIO_SEARCH_ON.getValue()) {
            getStyleClass().remove("fast-search-off");
            getStyleClass().add("fast-search-on");
        } else if (!audio && ProgConfig.FAST_FILM_SEARCH_ON.getValue()) {
            getStyleClass().remove("fast-search-off");
            getStyleClass().add("fast-search-on");
        } else {
            getStyleClass().remove("fast-search-on");
            getStyleClass().add("fast-search-off");
        }
        if (andSearch) {
            if (audio) {
                ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_AUDIO_CHANGED);
            } else {
                ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_FILM_CHANGED);
            }
        }
    }

    private void setLblText() {
        if (audio) {
            if (ProgConfig.FAST_AUDIO_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_THEME_TITLE) {
                lblTitel.setText("Thema oder Titel");
            } else if (ProgConfig.FAST_AUDIO_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_THEME) {
                lblTitel.setText("Thema");
            } else {
                lblTitel.setText("Titel");
            }
        } else {
            if (ProgConfig.FAST_FILM_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_THEME_TITLE) {
                lblTitel.setText("Thema oder Titel");
            } else if (ProgConfig.FAST_FILM_SEARCH_WHERE.getValue() == ProgConst.SEARCH_FAST_THEME) {
                lblTitel.setText("Thema");
            } else {
                lblTitel.setText("Titel");
            }
        }
    }
}
