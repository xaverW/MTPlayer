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
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.controller.mediadb.MediaFactory;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PaneDialog extends ScrollPane {

    Button btnCreateMediaDB = new Button("_Mediensammlung neu aufbauen");
    Button btnPlay = new Button();
    Button btnOpen = new Button();

    Button btnAndOr = new Button();
    final Button btnReset = new Button("");
    final Button btnClean = new Button("");
    TextField txtSearch = new TextField();

    ProgressBar progress = new ProgressBar();
    final RadioButton rbTheme = new RadioButton("Thema");
    final RadioButton rbTitle = new RadioButton("Titel");
    final RadioButton rbTt = new RadioButton("Thema oder Titel");

    Label lblGesamtMedia = new Label();
    Label lblHits = new Label();
    TableView<MediaData> tableMedia = new TableView();
    TableView<HistoryData> tableAbo = new TableView();

    TextField txtTitleMedia = new TextField();
    TextField txtPathMedia = new TextField();

    private final Listener listenerDbStart;
    private final Listener listenerDbStop;

    final String searchStrOrg;
    StringProperty searchStrProp;

    private final boolean media;
    private ProgData progData = ProgData.getInstance();

    ChangeListener sizeListener;
    ListChangeListener<HistoryData> listener;

    public PaneDialog(String searchStrOrg, StringProperty searchStrProp, boolean media) {
        this.searchStrOrg = searchStrOrg;
        this.searchStrProp = searchStrProp;
        this.media = media;

        listenerDbStart = new Listener(Listener.EVENT_MEDIA_DB_START, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB suchen
                txtSearch.setDisable(true);
            }
        };
        listenerDbStop = new Listener(Listener.EVENT_MEDIA_DB_STOP, MediaDialogController.class.getSimpleName()) {
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
        Listener.removeListener(listenerDbStart);
        Listener.removeListener(listenerDbStop);
    }

    private void initPanel() {
        VBox vBoxMedia = new VBox(10);

        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbTheme, rbTitle, rbTt);
        switch (ProgConfig.MEDIA_DIALOG_SEARCH_ABO.get()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEMA:
                rbTheme.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_TITEL:
                rbTitle.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL:
            default:
                rbTt.setSelected(true);
                break;
        }

        btnAndOr.setGraphic(ProgIcons.Icons.ICON_BUTTON_ADD.getImageView());
        btnAndOr.setTooltip(new Tooltip("Die Suchtexte mit \"ODER\" oder \"UND\" verknüpfen"));

        btnReset.setGraphic(ProgIcons.Icons.ICON_BUTTON_RESET.getImageView());
        btnReset.setTooltip(new Tooltip("Suchtext wieder herstellen"));

        btnClean.setGraphic(ProgIcons.Icons.ICON_BUTTON_CLEAN.getImageView());
        btnClean.setTooltip(new Tooltip("Suchtext putzen"));

        GridPane gridPaneSearch = new GridPane();
        gridPaneSearch.setPadding(new Insets(P2LibConst.DIST_EDGE));
        gridPaneSearch.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPaneSearch.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        txtSearch.setPrefWidth(Double.MAX_VALUE);
        GridPane.setHgrow(txtSearch, Priority.ALWAYS);
        gridPaneSearch.getStyleClass().add("extra-pane");

        gridPaneSearch.add(new Label("Suchen: "), 0, 0);
        gridPaneSearch.add(txtSearch, 1, 0, 3, 1);
        gridPaneSearch.add(btnAndOr, 4, 0);
        gridPaneSearch.add(btnReset, 5, 0);
        gridPaneSearch.add(btnClean, 6, 0);

        if (!media) {
            gridPaneSearch.add(rbTheme, 1, 1);
            gridPaneSearch.add(rbTitle, 2, 1);
            gridPaneSearch.add(rbTt, 3, 1);
        }

        HBox hBoxSum = new HBox(P2LibConst.DIST_BUTTON);
        hBoxSum.getChildren().addAll(new Label("Treffer:"), lblHits,
                PGuiTools.getHBoxGrower(), new Label("Anzahl Medien gesamt:"), lblGesamtMedia);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        txtTitleMedia.setEditable(false);
        txtPathMedia.setEditable(false);

        gridPane.add(new Label("Titel:"), 0, 0);
        gridPane.add(txtTitleMedia, 1, 0);
        if (media) gridPane.add(btnPlay, 2, 0);
        gridPane.add(new Label("Pfad:"), 0, 1);
        gridPane.add(txtPathMedia, 1, 1);
        if (media) gridPane.add(btnOpen, 2, 1);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        if (media) {
            HBox hBoxProgress = new HBox();
            hBoxProgress.setSpacing(P2LibConst.DIST_BUTTON);
            progress.setVisible(false);
            progress.setMaxHeight(Double.MAX_VALUE);
            progress.setMaxWidth(Double.MAX_VALUE);
            hBoxProgress.getChildren().addAll(btnCreateMediaDB, progress);
            HBox.setHgrow(progress, Priority.ALWAYS);

            tableMedia.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
            VBox.setVgrow(tableMedia, Priority.ALWAYS);
            vBoxMedia.getChildren().addAll(gridPaneSearch, PGuiTools.getHDistance(10), tableMedia, hBoxSum, gridPane, hBoxProgress);
        } else {
            tableAbo.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
            VBox.setVgrow(tableAbo, Priority.ALWAYS);
            vBoxMedia.getChildren().addAll(gridPaneSearch, PGuiTools.getHDistance(10), tableAbo, hBoxSum, gridPane);
        }

        this.setPadding(new Insets(P2LibConst.DIST_EDGE));
        this.setFitToHeight(true);
        this.setFitToWidth(true);
        this.setContent(vBoxMedia);
    }

    void initTable() {
    }

    void initAction() {
        FilterCheckRegEx fTT = new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            fTT.checkPattern();
            filter();
            searchStrProp.setValue(txtSearch.getText());
        });
        txtSearch.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearch.getSelectedText();
                txtSearch.setText(sel);
            }
        });
        btnReset.setOnAction(a -> txtSearch.setText(searchStrOrg));
        btnClean.setOnAction(a -> txtSearch.setText(MediaFactory.cleanSearchText(searchStrOrg,
                media ? ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO.getValue())));

        Listener.addListener(listenerDbStart);
        Listener.addListener(listenerDbStop);
    }

    public void filter(String searStr) {
        txtSearch.setText(searStr);
        filter();
    }

    void filter() {
    }
}
