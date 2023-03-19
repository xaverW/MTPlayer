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
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class FilmFilterControllerTextFilter extends VBox {

    private final ComboBox<String> cboTheme = new ComboBox<>();
    private final PMenuButton mbChannel;
    private final ComboBox<String> cboThemeTitle = new ComboBox();
    private final ComboBox<String> cboTitle = new ComboBox();
    private final ComboBox<String> cboSomewhere = new ComboBox();
    private final ComboBox<String> cboUrl = new ComboBox();

    private final ProgData progData;

    public FilmFilterControllerTextFilter() {
        super();
        progData = ProgData.getInstance();
        mbChannel = new PMenuButton(ProgData.getInstance().actFilmFilterWorker.getActFilterSettings().channelProperty(),
                ProgData.getInstance().worker.getAllChannelList());
        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);
        initStringFilter();
        addFilter();
    }

    private void initStringFilter() {
        //Theme
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

        addTextFilter(cboThemeTitle, progData.actFilmFilterWorker.getLastThemaTitleFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().themeTitleProperty());

        addTextFilter(cboTitle, progData.actFilmFilterWorker.getLastTitleFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().titleProperty());

        addTextFilter(cboSomewhere, progData.actFilmFilterWorker.getLastSomewhereFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().somewhereProperty());

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
}
