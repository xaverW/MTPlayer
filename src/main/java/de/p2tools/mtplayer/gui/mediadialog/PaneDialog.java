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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaCleaningFactory;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.gui.DownloadGuiMediaSearch;
import de.p2tools.mtplayer.gui.mediacleaning.MediaCleaningDialogController;
import de.p2tools.mtplayer.gui.tools.MTListener;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PaneDialog extends ScrollPane {

    Text textSearch = new Text();
    Button btnCreateMediaDB = new Button("_Mediensammlung neu aufbauen");
    Button btnPlay = new Button();
    Button btnOpen = new Button();

    final Button btnReset = new Button("");
    final Button btnClean = new Button("");
    final Button btnConfig = new Button();
    final Button btnClear = new Button();

    final Button btnClearList = new Button("_Liste löschen");
    TextField txtSearch = new TextField();

    ProgressBar progress = new ProgressBar();
    private Button btnStopSearching = new Button();

    Label lblGesamtMedia = new Label();
    Label lblHits = new Label();
    TableView<MediaData> tableMedia = new TableView();
    TableView<HistoryData> tableAbo = new TableView();

    TextField txtTitleMedia = new TextField();
    TextField txtPathMedia = new TextField();

    private final MTListener listenerDbStart;
    private final MTListener listenerDbStop;

    private final String searchTitelOrg;
    private final String searchThemeOrg;
    private final StringProperty searchStringProp;

    private final boolean media;
    private final boolean abo;

    private ProgData progData = ProgData.getInstance();

    ChangeListener sizeListener;
    ListChangeListener<HistoryData> listener;

    public PaneDialog(String searchThemeOrg, String searchTitelOrg,
                      StringProperty searchStringProp, boolean media, boolean abo) {
        this.searchThemeOrg = searchThemeOrg;
        this.searchTitelOrg = searchTitelOrg;
        this.searchStringProp = searchStringProp;
        this.media = media;
        this.abo = abo;

        listenerDbStart = new MTListener(MTListener.EVENT_MEDIA_DB_START, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB suchen
                txtSearch.setDisable(true);
            }
        };
        listenerDbStop = new MTListener(MTListener.EVENT_MEDIA_DB_STOP, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB liegt vor
                txtSearch.setDisable(false);
                filter();
            }
        };
    }

    public void make() {
        initPanel();
        initTable();
        initAction();
        filter();
    }

    public void close() {
        MTListener.removeListener(listenerDbStart);
        MTListener.removeListener(listenerDbStop);
    }

    private void initPanel() {
        txtTitleMedia.setEditable(false);
        txtPathMedia.setEditable(false);

        btnReset.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_RESET.getImageView());
        btnReset.setTooltip(new Tooltip("Suchtext wieder herstellen"));

        btnClean.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_CLEAN.getImageView());
        btnClean.setTooltip(new Tooltip("Suchtext putzen"));

        btnConfig.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_EDIT.getImageView());
        btnConfig.setTooltip(new Tooltip("Einstellungen zum Putzen"));

        btnClear.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_STOP.getImageView());
        btnClear.setTooltip(new Tooltip("Das Suchfeld löschen"));
        btnClear.setOnAction(a -> txtSearch.clear());

        // Suchen was
        VBox vLeft = DownloadGuiMediaSearch.getSearchMedia(null);
        VBox vRight = DownloadGuiMediaSearch.getSearchAbo(null, abo);

        ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.addListener((u, o, n) -> filter());
        ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.addListener((u, o, n) -> filter());
        ProgConfig.GUI_MEDIA_BUILD_SEARCH_MEDIA.addListener((u, o, n) -> {
            setSearchString();
        });
        ProgConfig.GUI_MEDIA_BUILD_SEARCH_ABO.addListener((u, o, n) -> {
            setSearchString();
        });

        txtSearch.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(txtSearch, Priority.ALWAYS);

        HBox hBoxSearch = new HBox(P2LibConst.DIST_HBOX);
        hBoxSearch.setPadding(new Insets(0));
        hBoxSearch.setAlignment(Pos.CENTER_RIGHT);
        hBoxSearch.getChildren().addAll(new Label("Suchen: "), txtSearch, btnReset, btnClean, btnConfig, btnClear);
        VBox.setVgrow(hBoxSearch, Priority.ALWAYS);

        if (media) {
            HBox hBoxProgress = new HBox(P2LibConst.DIST_HBOX);
            hBoxProgress.setPadding(new Insets(0));
            hBoxProgress.setAlignment(Pos.CENTER_RIGHT);

            progress.setVisible(false);
            progress.setMaxHeight(Double.MAX_VALUE);
            progress.setMaxWidth(Double.MAX_VALUE);
            btnStopSearching.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_STOP.getImageView());
            btnStopSearching.setOnAction(event -> progData.mediaDataList.setStopSearching(true));
            btnStopSearching.visibleProperty().bind(progData.mediaDataList.searchingProperty());
            hBoxProgress.getChildren().addAll(btnCreateMediaDB, progress, btnStopSearching);
            HBox.setHgrow(progress, Priority.ALWAYS);
            vLeft.getChildren().addAll(hBoxSearch, hBoxProgress);
        } else {
            vRight.getChildren().addAll(hBoxSearch, btnClearList);
        }

        HBox hBoxSum = new HBox(P2LibConst.DIST_BUTTON);
        hBoxSum.getChildren().addAll(new Label("Treffer:"), lblHits,
                P2GuiTools.getHBoxGrower(), new Label("Anzahl Medien gesamt:"), lblGesamtMedia);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        gridPane.add(new Label("Titel:"), 0, 0);
        gridPane.add(txtTitleMedia, 1, 0);
        if (media) gridPane.add(btnPlay, 2, 0);
        gridPane.add(new Label("Pfad:"), 0, 1);
        gridPane.add(txtPathMedia, 1, 1);
        if (media) gridPane.add(btnOpen, 2, 1);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        VBox vBoxMedia = new VBox(P2LibConst.DIST_EDGE);

        if (media) {
            tableMedia.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
            VBox.setVgrow(tableMedia, Priority.ALWAYS);
            vBoxMedia.getChildren().addAll(vLeft, tableMedia, hBoxSum, gridPane);

        } else {
            tableAbo.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
            VBox.setVgrow(tableAbo, Priority.ALWAYS);
            vBoxMedia.getChildren().addAll(vRight, tableAbo, hBoxSum, gridPane);
        }

        this.setPadding(new Insets(P2LibConst.DIST_EDGE));
        this.setFitToHeight(true);
        this.setFitToWidth(true);
        this.setContent(vBoxMedia);
    }

    void initTable() {
    }

    void initAction() {
        FilterCheckRegEx filterCheckRegEx = new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCheckRegEx.checkPattern();
            filter();
            searchStringProp.setValue(txtSearch.getText());
        });
        txtSearch.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearch.getSelectedText();
                txtSearch.setText(sel);
            }
        });
        btnReset.setOnAction(a -> txtSearch.setText(searchThemeOrg + " " + searchTitelOrg));
        btnClean.setOnAction(a -> {
            setSearchString();
            filter();
        });

        btnConfig.setOnAction(a -> {
            new MediaCleaningDialogController(media);
            filter();
        });

        MTListener.addListener(listenerDbStart);
        MTListener.addListener(listenerDbStop);
    }

    void filter(String searStr) {
        txtSearch.setText(searStr);
        filter();
    }

    public void filter() {
    }

    private void setSearchString() {
        txtSearch.setText(MediaCleaningFactory.cleanSearchText(searchThemeOrg, searchTitelOrg, media));
    }
}
