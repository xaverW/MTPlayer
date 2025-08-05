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

package de.p2tools.mtplayer.gui.filter.audio;

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

public class AudioFilterControllerTextFilter extends VBox {

    private final P2MenuButton mbChannel;
    private final PCboThemeExact cboThemeExact;
    private final PCboString cboTheme;
    private final PCboString cboThemeTitle;
    private final PCboString cboTitle;
    private final PCboString cboSomewhere;
    private final PCboString cboUrl;

    private final ProgData progData;

    public AudioFilterControllerTextFilter() {
        super();
        progData = ProgData.getInstance();
        mbChannel = new P2MenuButton(progData.filterWorkerAudio.getActFilterSettings().channelProperty(), ThemeListFactory.allChannelListAudio);

        final BooleanSupplier supplierReportReturn = () -> {
            progData.filterWorkerAudio.getActFilterSettings().reportFilterReturn();
            return true;
        };

        cboThemeExact = new PCboThemeExact(true, progData, progData.filterWorkerAudio.getActFilterSettings().exactThemeProperty());

        cboTheme = new PCboString(progData.stringFilterLists.getFilterListAudioTheme(),
                progData.filterWorkerAudio.getActFilterSettings().themeProperty(), supplierReportReturn);
        cboThemeTitle = new PCboString(progData.stringFilterLists.getFilterListAudioThemeTitle(),
                progData.filterWorkerAudio.getActFilterSettings().themeTitleProperty(), supplierReportReturn);
        cboTitle = new PCboString(progData.stringFilterLists.getFilterListAudioTitel(),
                progData.filterWorkerAudio.getActFilterSettings().titleProperty(), supplierReportReturn);
        cboSomewhere = new PCboString(progData.stringFilterLists.getFilterListAudioSomewhere(),
                progData.filterWorkerAudio.getActFilterSettings().somewhereProperty(), supplierReportReturn);
        cboUrl = new PCboString(progData.stringFilterLists.getFilterListAudioUrl(),
                progData.filterWorkerAudio.getActFilterSettings().urlProperty(), supplierReportReturn);

        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);
        addFilter();
    }

    private void addFilter() {
        addTxt("Sender", mbChannel, this, progData.filterWorkerAudio.getActFilterSettings().channelVisProperty());

        BooleanProperty b = new SimpleBooleanProperty();
        b.bind(progData.filterWorkerAudio.getActFilterSettings().themeVisProperty()
                .and(progData.filterWorkerAudio.getActFilterSettings().themeIsExactProperty().not())
        );
        addTxt("Thema", cboTheme, this, b);

        b = new SimpleBooleanProperty();
        b.bind(progData.filterWorkerAudio.getActFilterSettings().themeVisProperty()
                .and(progData.filterWorkerAudio.getActFilterSettings().themeIsExactProperty())
        );
        addTxt("Thema exakt", cboThemeExact, this, b);

        addTxt("Thema oder Titel", cboThemeTitle, this, progData.filterWorkerAudio.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", cboTitle, this, progData.filterWorkerAudio.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", cboSomewhere, this, progData.filterWorkerAudio.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", cboUrl, this, progData.filterWorkerAudio.getActFilterSettings().urlVisProperty());

        this.visibleProperty().bind(progData.filterWorkerAudio.getActFilterSettings().channelVisProperty()
                .or(progData.filterWorkerAudio.getActFilterSettings().themeVisProperty()
                        .or(progData.filterWorkerAudio.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.filterWorkerAudio.getActFilterSettings().titleVisProperty()
                                        .or(progData.filterWorkerAudio.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.filterWorkerAudio.getActFilterSettings().urlVisProperty())
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
