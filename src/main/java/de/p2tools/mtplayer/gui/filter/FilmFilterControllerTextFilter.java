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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filmfilter.P2CboStringSearch;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class FilmFilterControllerTextFilter extends VBox {

    private final PMenuButton mbChannel;
    private final P2CboStringSearch cboTheme;
    private final P2CboStringSearch cboThemeTitle;
    private final P2CboStringSearch cboTitle;
    private final P2CboStringSearch cboSomewhere;
    private final P2CboStringSearch cboUrl;

    private final ProgData progData;

    public FilmFilterControllerTextFilter() {
        super();
        progData = ProgData.getInstance();
        mbChannel = new PMenuButton(progData.filmFilterWorker.getActFilterSettings().channelProperty(),
                progData.worker.getAllChannelList());

        this.cboTheme = new P2CboStringSearch(progData, progData.filmFilterWorker.getActFilterSettings().themeProperty());
        this.cboThemeTitle = new P2CboStringSearch(progData, progData.filmFilterWorker.getActFilterSettings().themeTitleProperty());
        this.cboTitle = new P2CboStringSearch(progData, progData.filmFilterWorker.getActFilterSettings().titleProperty());
        this.cboSomewhere = new P2CboStringSearch(progData, progData.filmFilterWorker.getActFilterSettings().somewhereProperty());
        this.cboUrl = new P2CboStringSearch(progData, progData.filmFilterWorker.getActFilterSettings().urlProperty());

        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);
        initCboThemeExactFilter();
        addFilter();
    }

    private void initCboThemeExactFilter() {
        // Theme: ein Extra für "EXACT"
        progData.filmFilterWorker.getActFilterSettings().themeExactProperty().addListener((observable, oldValue, newValue) -> {
            addCboThemeItem();
        });
        addCboThemeItem();
        cboTheme.editableProperty().bind(progData.filmFilterWorker.getActFilterSettings().themeExactProperty().not());
        cboTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.filmFilterWorker.getActFilterSettings().themeExactProperty().getValue()) {
                //progData.filmFilterWorker.getActFilterSettings().setTheme(cboTheme.valueProperty().getValue());
                progData.filmFilterWorker.getActFilterSettings().setTheme(cboTheme.getEditor().getText());
            }
        });
        progData.worker.getThemeForChannelList().addListener((ListChangeListener<String>) c -> {
            progData.filmFilterWorker.getActFilterSettings().switchFilterOff(true);
            // cboTheme.valueProperty().setValue(progData.filmFilterWorker.getActFilterSettings().getTheme());
            cboTheme.addSearchStrings(progData.worker.getThemeForChannelList());
            cboTheme.valueProperty().setValue(new P2CboSearcher(progData.filmFilterWorker.getActFilterSettings().getTheme()));
            progData.filmFilterWorker.getActFilterSettings().switchFilterOff(false);
        });
    }

    private void addCboThemeItem() {
        if (progData.filmFilterWorker.getActFilterSettings().isThemeExact()) {
            // dann die channels eintragen
            // cboTheme.setItems(progData.worker.getThemeForChannelList());
            // cboTheme.valueProperty().setValue(progData.filmFilterWorker.getActFilterSettings().getTheme());
            cboTheme.addSearchStrings(progData.worker.getThemeForChannelList());
            cboTheme.valueProperty().setValue(new P2CboSearcher(progData.filmFilterWorker.getActFilterSettings().getTheme()));
        } else {
            // dann freie Suche
            cboTheme.getItems().clear();
        }
    }

    private void addFilter() {
        addTxt("Sender", mbChannel, this, progData.filmFilterWorker.getActFilterSettings().channelVisProperty());
        addTxt("Thema", cboTheme, this, progData.filmFilterWorker.getActFilterSettings().themeVisProperty());
        addTxt("Thema oder Titel", cboThemeTitle, this, progData.filmFilterWorker.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", cboTitle, this, progData.filmFilterWorker.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", cboSomewhere, this, progData.filmFilterWorker.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", cboUrl, this, progData.filmFilterWorker.getActFilterSettings().urlVisProperty());

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(0);
        sp.setMaxHeight(1);
        this.getChildren().add(sp);

        this.visibleProperty().bind(progData.filmFilterWorker.getActFilterSettings().channelVisProperty()
                .or(progData.filmFilterWorker.getActFilterSettings().themeVisProperty()
                        .or(progData.filmFilterWorker.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.filmFilterWorker.getActFilterSettings().titleVisProperty()
                                        .or(progData.filmFilterWorker.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.filmFilterWorker.getActFilterSettings().urlVisProperty())
                                        )
                                )
                        )
                ));
        this.managedProperty().bind(this.visibleProperty());
        sp.visibleProperty().bind(this.visibleProperty());
        sp.managedProperty().bind(this.visibleProperty());
    }

    private void addTxt(String txt, Node control, VBox vBoxComplete, BooleanProperty booleanProperty) {
        VBox vBox = new VBox(2);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);

        vBox.visibleProperty().bind(booleanProperty);
        vBox.managedProperty().bind(booleanProperty);
    }
}
