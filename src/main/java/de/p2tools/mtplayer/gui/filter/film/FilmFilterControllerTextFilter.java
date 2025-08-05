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

package de.p2tools.mtplayer.gui.filter.film;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.filter.FilterController;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import de.p2tools.mtplayer.gui.filter.helper.PCboThemeExact;
import de.p2tools.p2lib.guitools.P2MenuButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.BooleanSupplier;

public class FilmFilterControllerTextFilter extends VBox {

    private final P2MenuButton mbChannel;
    private final PCboThemeExact cboThemeExact;
    private final PCboString cboTheme;
    private final PCboString cboThemeTitle;
    private final PCboString cboTitle;
    private final PCboString cboSomewhere;
    private final PCboString cboUrl;

    private final ProgData progData;

    public FilmFilterControllerTextFilter() {
        super();
        progData = ProgData.getInstance();
        mbChannel = new P2MenuButton(progData.filterWorkerFilm.getActFilterSettings().channelProperty(), ThemeListFactory.allChannelList);

        final BooleanSupplier supplierReportReturn = () -> {
            progData.filterWorkerFilm.getActFilterSettings().reportFilterReturn();
            return true;
        };

        cboThemeExact = new PCboThemeExact(progData, progData.filterWorkerFilm.getActFilterSettings().exactThemeProperty());

        cboTheme = new PCboString(progData.stringFilterLists.getFilterListTheme(),
                progData.filterWorkerFilm.getActFilterSettings().themeProperty(), supplierReportReturn);
        cboThemeTitle = new PCboString(progData.stringFilterLists.getFilterListThemeTitle(),
                progData.filterWorkerFilm.getActFilterSettings().themeTitleProperty(), supplierReportReturn);
        cboTitle = new PCboString(progData.stringFilterLists.getFilterListTitel(),
                progData.filterWorkerFilm.getActFilterSettings().titleProperty(), supplierReportReturn);
        cboSomewhere = new PCboString(progData.stringFilterLists.getFilterListSomewhere(),
                progData.filterWorkerFilm.getActFilterSettings().somewhereProperty(), supplierReportReturn);
        cboUrl = new PCboString(progData.stringFilterLists.getFilterListUrl(),
                progData.filterWorkerFilm.getActFilterSettings().urlProperty(), supplierReportReturn);

        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);
        addFilter();
    }

    private void addFilter() {
        addTxt("Sender", mbChannel, this, progData.filterWorkerFilm.getActFilterSettings().channelVisProperty());

        BooleanProperty b = new SimpleBooleanProperty();
        b.bind(progData.filterWorkerFilm.getActFilterSettings().themeVisProperty()
                .and(progData.filterWorkerFilm.getActFilterSettings().themeIsExactProperty().not())
        );
        addTxt("Thema", cboTheme, this, b);

        b = new SimpleBooleanProperty();
        b.bind(progData.filterWorkerFilm.getActFilterSettings().themeVisProperty()
                .and(progData.filterWorkerFilm.getActFilterSettings().themeIsExactProperty())
        );
        addTxt("Thema exakt", cboThemeExact, this, b);

        addTxt("Thema oder Titel", cboThemeTitle, this, progData.filterWorkerFilm.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", cboTitle, this, progData.filterWorkerFilm.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", cboSomewhere, this, progData.filterWorkerFilm.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", cboUrl, this, progData.filterWorkerFilm.getActFilterSettings().urlVisProperty());

        this.visibleProperty().bind(progData.filterWorkerFilm.getActFilterSettings().channelVisProperty()
                .or(progData.filterWorkerFilm.getActFilterSettings().themeVisProperty()
                        .or(progData.filterWorkerFilm.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.filterWorkerFilm.getActFilterSettings().titleVisProperty()
                                        .or(progData.filterWorkerFilm.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.filterWorkerFilm.getActFilterSettings().urlVisProperty())
                                        )
                                )
                        )
                ));
        this.managedProperty().bind(this.visibleProperty());
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
