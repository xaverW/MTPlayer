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
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MediaDialogController extends PDialogExtra {

    private final TabPane tabPane = new TabPane();
    private final Button btnOk = new Button("_Ok");

    private final ProgData progData = ProgData.getInstance();

    private PaneDialogMedia paneDialogMedia;
    private PaneDialogAbo paneDialogAbo;
    private PaneDialogAbo paneDialogHistory;

    private final MediaDataDto mediaDataDtoMedia;
    private final MediaDataDto mediaDataDtoAbo;
    private final MediaDataDto mediaDataDtoHistory;


    public MediaDialogController(MediaDataDto mediaDataDto) {
        // Aufruf mit Button aus dem Infobereich der Tabelle Filme/Download,
        // also 2x möglich
        super(ProgData.getInstance().primaryStage, ProgConfig.MEDIA_DIALOG_SIZE, "Mediensammlung",
                true, false, DECO.BORDER);

        this.mediaDataDtoMedia = mediaDataDto;
        this.mediaDataDtoAbo = new MediaDataDto();
        this.mediaDataDtoHistory = new MediaDataDto();
        initDto();
        init(true);
    }

    public MediaDialogController(String searchTheme, String searchTitel) {
        // Hauptmenü, Menüpunkt: "Mediensammlung",
        // im Tab Filme/Downloads im Menü/Table Kontextmenü/ShorCut: "Film in Mediensammlung suchen"
        // also 6x möglich
        super(ProgData.getInstance().primaryStage, ProgConfig.MEDIA_DIALOG_SIZE, "Mediensammlung",
                true, false, DECO.BORDER);

        this.mediaDataDtoMedia = new MediaDataDto();
        this.mediaDataDtoAbo = new MediaDataDto();
        this.mediaDataDtoHistory = new MediaDataDto();
        mediaDataDtoMedia.searchTheme = searchTheme;
        mediaDataDtoMedia.searchTitle = searchTitel;
        initDto();
        init(true);
    }

    @Override
    public void make() {
        initPanel();
        initAction();
        paneDialogMedia.filter(mediaDataDtoMedia.searchStringProp.getValueSafe());
        paneDialogAbo.filter(mediaDataDtoAbo.searchStringProp.getValueSafe());
        paneDialogHistory.filter(mediaDataDtoHistory.searchStringProp.getValueSafe());
    }

    @Override
    public void close() {
        paneDialogMedia.close();
        paneDialogAbo.close();
        paneDialogHistory.close();
        super.close();
    }

    private void initDto() {
        mediaDataDtoMedia.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_MEDIA;
        mediaDataDtoMedia.searchStringProp.setValue((mediaDataDtoMedia.searchTheme + " " + mediaDataDtoMedia.searchTitle).trim());

        mediaDataDtoAbo.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_ABO;
        mediaDataDtoAbo.searchTheme = mediaDataDtoMedia.searchTheme;
        mediaDataDtoAbo.searchTitle = mediaDataDtoMedia.searchTitle;
        mediaDataDtoAbo.searchStringProp.setValue(mediaDataDtoMedia.searchStringProp.getValueSafe());

        mediaDataDtoHistory.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_HISTORY;
        mediaDataDtoHistory.searchTheme = mediaDataDtoMedia.searchTheme;
        mediaDataDtoHistory.searchTitle = mediaDataDtoMedia.searchTitle;
        mediaDataDtoHistory.searchStringProp.setValue(mediaDataDtoMedia.searchStringProp.getValueSafe());

        mediaDataDtoMedia.buildSearchFrom = ProgConfig.DIALOG_BUILD_SEARCH_FROM_FOR_MEDIA;
        mediaDataDtoMedia.searchInWhat = ProgConfig.DIALOG_SEARCH_IN_WHAT_FOR_MEDIA;

        mediaDataDtoAbo.buildSearchFrom = ProgConfig.DIALOG_BUILD_SEARCH_FROM_FOR_ABO;
        mediaDataDtoAbo.searchInWhat = ProgConfig.DIALOG_SEARCH_IN_WHAT_FOR_ABO;

        mediaDataDtoHistory.buildSearchFrom = ProgConfig.DIALOG_BUILD_SEARCH_FROM_FOR_HISTORY;
        mediaDataDtoHistory.searchInWhat = ProgConfig.DIALOG_SEARCH_IN_WHAT_FOR_HISTORY;
    }

    private void initPanel() {
        try {
            paneDialogMedia = new PaneDialogMedia(getStage(), mediaDataDtoMedia);
            paneDialogMedia.make();

            paneDialogAbo = new PaneDialogAbo(getStage(), mediaDataDtoAbo);
            paneDialogAbo.make();

            paneDialogHistory = new PaneDialogAbo(getStage(), mediaDataDtoHistory);
            paneDialogHistory.make();

            Tab tabMedia = new Tab("Mediensammlung");
            tabMedia.setTooltip(new Tooltip("Hier wird der Inhalt der Mediensammlung angezeigt"));
            tabMedia.setClosable(false);
            tabMedia.setContent(paneDialogMedia);
            tabPane.getTabs().add(tabMedia);

            Tab tabAbo = new Tab("Erledigte Abos");
            tabAbo.setTooltip(new Tooltip("Hier werden erledigte Abos angezeigt"));
            tabAbo.setClosable(false);
            tabAbo.setContent(paneDialogAbo);
            tabPane.getTabs().add(tabAbo);

            Tab tabHistory = new Tab("History");
            tabHistory.setTooltip(new Tooltip("Hier werden die bereits gesehenen Filme angezeigt"));
            tabHistory.setClosable(false);
            tabHistory.setContent(paneDialogHistory);
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
    }
}
