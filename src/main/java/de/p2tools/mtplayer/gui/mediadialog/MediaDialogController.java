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

package de.p2tools.mtplayer.gui.mediadialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MediaDialogController extends PDialogExtra {

    private TabPane tabPane = new TabPane();
    private Tab tabMedia;
    private Tab tabAbo;
    private Tab tabHistory;
    private final Button btnOk = new Button("_Ok");

    private final String searchTitelOrg;
    private final String searchThemeOrg;

    private final StringProperty searchStringProp = new SimpleStringProperty();
    private final ProgData progData = ProgData.getInstance();

    private PaneDialogMedia paneDialogMedia;
    private PaneDialogAbo paneDialogAbo;
    private PaneDialogAbo paneHistory;

    public MediaDialogController(String searchTheme, String searchTitel, boolean openMedia) {
        super(ProgData.getInstance().primaryStage, ProgConfig.MEDIA_DIALOG_SIZE, "Mediensammlung",
                true, false, DECO.BORDER);

        this.searchThemeOrg = searchTheme.trim();
        this.searchTitelOrg = searchTitel.trim();
        searchStringProp.setValue(searchThemeOrg + " " + searchTitelOrg);
        ProgConfig.SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA.setValue(openMedia);

        init(true);
    }

    public MediaDialogController(String searchTheme, String searchTitel) {
        super(ProgData.getInstance().primaryStage, ProgConfig.MEDIA_DIALOG_SIZE, "Mediensammlung",
                true, false, DECO.BORDER);

        this.searchThemeOrg = searchTheme.trim();
        this.searchTitelOrg = searchTitel.trim();
        searchStringProp.setValue(searchTheme + " " + searchTitel);

        init(true);
    }

    @Override
    public void make() {
        initPanel();
        initAction();
        setPane();
        paneDialogMedia.filter(searchStringProp.getValueSafe());
        paneDialogAbo.filter(searchStringProp.getValueSafe());
        paneHistory.filter(searchStringProp.getValueSafe());
    }

    @Override
    public void close() {
        paneDialogMedia.close();
        paneDialogAbo.close();
        paneHistory.close();

        progData.historyListAbos.filteredListSetPredFalse();
        progData.mediaDataList.filterdListSetPredFalse();
        super.close();
    }

    private void initPanel() {
        try {
            paneDialogMedia = new PaneDialogMedia(getStage(), searchThemeOrg, searchTitelOrg, searchStringProp);
            paneDialogMedia.make();

            paneDialogAbo = new PaneDialogAbo(getStage(), searchThemeOrg, searchTitelOrg, searchStringProp, true);
            paneDialogAbo.make();

            paneHistory = new PaneDialogAbo(getStage(), searchThemeOrg, searchTitelOrg, searchStringProp, false);
            paneHistory.make();

            tabMedia = new Tab("Mediensammlung");
            tabMedia.setTooltip(new Tooltip("Hier wird der Inhalt der Mediensammlung angezeigt"));
            tabMedia.setClosable(false);
            tabMedia.setContent(paneDialogMedia);
            tabPane.getTabs().add(tabMedia);

            tabAbo = new Tab("Erledigte Abos");
            tabAbo.setTooltip(new Tooltip("Hier werden erledigte Abos angezeigt"));
            tabAbo.setClosable(false);
            tabAbo.setContent(paneDialogAbo);
            tabPane.getTabs().add(tabAbo);

            tabHistory = new Tab("History");
            tabHistory.setTooltip(new Tooltip("Hier werden die bereits gesehenen Filme angezeigt"));
            tabHistory.setClosable(false);
            tabHistory.setContent(paneHistory);
            tabPane.getTabs().add(tabHistory);

            getVBoxCont().setPadding(new Insets(0));
            VBox.setVgrow(tabPane, Priority.ALWAYS);
            getVBoxCont().getChildren().add(tabPane);

            Button btnHelp = P2Button.helpButton(getStage(),
                    "Suche in der Mediensammlung", HelpText.SEARCH_MEDIA_DIALOG);
            addOkButton(btnOk);
            addHlpButton(btnHelp);
        } catch (final Exception ex) {
            PLog.errorLog(951203030, ex);
        }
    }

    private void initAction() {
        btnOk.setOnAction(a -> close());

        tabPane.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            ProgConfig.SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA.setValue(tabPane.getSelectionModel().getSelectedItem().equals(tabMedia));
        });
    }

    private void setPane() {
        if (ProgConfig.SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA.getValue()) {
            tabPane.getSelectionModel().select(tabMedia);
        } else {
            tabPane.getSelectionModel().select(tabAbo);
        }
    }
}
