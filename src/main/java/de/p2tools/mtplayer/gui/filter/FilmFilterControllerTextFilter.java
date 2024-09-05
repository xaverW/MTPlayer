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
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import de.p2tools.mtplayer.gui.filter.helper.PCboThemeExact;
import de.p2tools.p2lib.guitools.P2MenuButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
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
        mbChannel = new P2MenuButton(progData.filterWorker.getActFilterSettings().channelProperty(), ThemeListFactory.allChannelList);

        final BooleanSupplier supplierReportReturn = () -> {
            progData.filterWorker.getActFilterSettings().reportFilterReturn();
            return true;
        };

        cboThemeExact = new PCboThemeExact(progData, progData.filterWorker.getActFilterSettings().exactThemeProperty());

        cboTheme = new PCboString(progData.stringFilterLists.getFilterListTheme(),
                progData.filterWorker.getActFilterSettings().themeProperty(), supplierReportReturn);
        cboThemeTitle = new PCboString(progData.stringFilterLists.getFilterListThemeTitle(),
                progData.filterWorker.getActFilterSettings().themeTitleProperty(), supplierReportReturn);
        cboTitle = new PCboString(progData.stringFilterLists.getFilterListTitel(),
                progData.filterWorker.getActFilterSettings().titleProperty(), supplierReportReturn);
        cboSomewhere = new PCboString(progData.stringFilterLists.getFilterListSomewhere(),
                progData.filterWorker.getActFilterSettings().somewhereProperty(), supplierReportReturn);
        cboUrl = new PCboString(progData.stringFilterLists.getFilterListUrl(),
                progData.filterWorker.getActFilterSettings().urlProperty(), supplierReportReturn);

        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);
        addFilter();
    }

    private void addFilter() {
        addTxt("Sender", mbChannel, this, progData.filterWorker.getActFilterSettings().channelVisProperty());

        BooleanProperty b = new SimpleBooleanProperty();
        b.bind(progData.filterWorker.getActFilterSettings().themeVisProperty()
                .and(progData.filterWorker.getActFilterSettings().themeIsExactProperty().not())
        );
        addTxt("Thema", cboTheme, this, b);

        b = new SimpleBooleanProperty();
        b.bind(progData.filterWorker.getActFilterSettings().themeVisProperty()
                .and(progData.filterWorker.getActFilterSettings().themeIsExactProperty())
        );
        addTxt("Thema exakt", cboThemeExact, this, b);

        addTxt("Thema oder Titel", cboThemeTitle, this, progData.filterWorker.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", cboTitle, this, progData.filterWorker.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", cboSomewhere, this, progData.filterWorker.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", cboUrl, this, progData.filterWorker.getActFilterSettings().urlVisProperty());

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(0);
        sp.setMaxHeight(1);
        this.getChildren().add(sp);

        this.visibleProperty().bind(progData.filterWorker.getActFilterSettings().channelVisProperty()
                .or(progData.filterWorker.getActFilterSettings().themeVisProperty()
                        .or(progData.filterWorker.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.filterWorker.getActFilterSettings().titleVisProperty()
                                        .or(progData.filterWorker.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.filterWorker.getActFilterSettings().urlVisProperty())
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
