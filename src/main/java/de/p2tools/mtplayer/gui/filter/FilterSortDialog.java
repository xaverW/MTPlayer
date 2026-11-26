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
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class FilterSortDialog extends P2DialogExtra {

    private final FilterSortPane filterSortPane;
    private final FilterSwitchPane filterSwitchPane;

    public FilterSortDialog(ProgData progData) {
        super(progData.primaryStage, ProgConfig.DOWNLOAD_FILTER_SORT_DIALOG_SIZE, "Filtereinstellungen",
                false, true, false, DECO.NO_BORDER);

        this.filterSwitchPane = new FilterSwitchPane(getStageProp());
        this.filterSortPane = new FilterSortPane(getStageProp());

        init(true);
    }

    @Override
    public void close() {
        filterSwitchPane.close();
        filterSortPane.close();
        super.close();
    }

    @Override
    public void make() {
        Tab tabSwitch = new Tab("Ein/Ausschlaten");
        tabSwitch.setClosable(false);
        tabSwitch.setContent(filterSwitchPane);

        Tab tabSort = new Tab("Sortierung");
        tabSort.setClosable(false);
        tabSort.setContent(filterSortPane);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tabSwitch, tabSort);

        getVBoxCont().getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        final Button btnHelp = P2IconFactory.getHelpButton(getStage(), "Filtereinstellungen", HelpText.GUI_FILMS_EDIT_FILTER);
        final Button btnOk = new Button("_Ok");
        btnOk.setOnAction(event -> close());
        addOkButton(btnOk);
        addHlpButton(btnHelp);
    }
}
