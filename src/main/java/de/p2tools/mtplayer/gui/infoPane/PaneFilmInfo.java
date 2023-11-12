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

package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.mtdownload.DownloadSizeData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PaneFilmInfo extends VBox {
    private final SplitPane splitPane = new SplitPane();
    private final VBox vBoxLeft = new VBox();

    private final TextArea textArea = new TextArea();
    private final Button btnReset = new Button("@");
    private final Label lblTheme = new Label("");
    private final Label lblTitle = new Label("");
    private final HBox hBoxUrl = new HBox(10);
    private final Label lblUrl = new Label("zur Website: ");

    private final Label lblDate = new Label();
    private final Label lblTime = new Label();
    private final Label lblDuration = new Label();
    private final Label lblSize = new Label();
    private final Label lblAbo = new Label();

    private FilmDataMTP film = null;
    private DownloadData downloadData = null;
    private final ChangeListener<String> changeListener;
    private final ChangeListener<DownloadSizeData> sizeChangeListener;

    private String oldDescription = "";

    public PaneFilmInfo(DoubleProperty dividerProp) {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(textArea, btnReset);
        StackPane.setAlignment(btnReset, Pos.BOTTOM_RIGHT);
        stackPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        this.changeListener = (observable, oldValue, newValue) -> setFilmDescription();
        this.sizeChangeListener = (u, o, n) -> setSize(true);

        btnReset.setOnAction(a -> resetFilmDescription());
        btnReset.setTooltip(new Tooltip("Beschreibung zurücksetzen"));
        btnReset.setVisible(false);

        lblTheme.setFont(Font.font(null, FontWeight.BOLD, -1));
        hBoxUrl.setAlignment(Pos.CENTER_LEFT);
        lblUrl.setMinWidth(Region.USE_PREF_SIZE);

        textArea.setWrapText(true);
        textArea.setPrefRowCount(4);

        VBox v = new VBox();
        v.setSpacing(0);
        v.getChildren().addAll(lblTheme, lblTitle);
        vBoxLeft.setSpacing(2);
        vBoxLeft.setPadding(new Insets(P2LibConst.DIST_EDGE));
        vBoxLeft.getChildren().addAll(v, stackPane, hBoxUrl);

        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("extra-pane-info");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(), P2ColumnConstraints.getCcComputedSizeAndHgrow());

        int row = 0;
        gridPane.add(new Label("Datum: "), 0, row);
        gridPane.add(lblDate, 1, row);
        gridPane.add(new Label("Zeit: "), 0, ++row);
        gridPane.add(lblTime, 1, row);
        gridPane.add(new Label("Dauer: "), 0, ++row);
        gridPane.add(lblDuration, 1, row);
        gridPane.add(new Label("Größe: "), 0, ++row);
        gridPane.add(lblSize, 1, row);
        gridPane.add(new Label("Abo: "), 0, ++row);
        gridPane.add(lblAbo, 1, row);

        splitPane.getItems().addAll(vBoxLeft, gridPane);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(dividerProp);
        SplitPane.setResizableWithParent(vBoxLeft, Boolean.FALSE);
        SplitPane.setResizableWithParent(gridPane, Boolean.FALSE);

        setSpacing(0);
        setPadding(new Insets(0));
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        getChildren().add(splitPane);
    }

//    int i = 0;

    public void setFilm(FilmDataMTP film) {
//        System.out.println("=====> setFilm(FilmDataMTP film) " +
//                ++i + "  " + (film != null ? film.getTitle() : "null"));
        hBoxUrl.getChildren().clear();
        textArea.textProperty().removeListener(changeListener);
        if (this.downloadData != null) {
            this.downloadData.downloadSizeProperty().removeListener(sizeChangeListener);
        }

        this.film = film;
        this.downloadData = null;

        if (film == null) {
            lblTheme.setText("");
            lblTitle.setText("");
            textArea.clear();
            oldDescription = "";
            btnReset.setVisible(false);

            lblDate.setText("");
            lblTime.setText("");
            lblDuration.setText("");
            lblSize.setText("");
            lblAbo.setText("");
            return;
        }

        lblTheme.setText(film.arr[FilmDataXml.FILM_CHANNEL] + "  -  " + film.arr[FilmDataXml.FILM_THEME]);
        lblTitle.setText(film.arr[FilmDataXml.FILM_TITLE]);

        textArea.setText(film.getDescription());
        oldDescription = film.getDescription();
        btnReset.setVisible(false);
        textArea.textProperty().addListener(changeListener);

        lblDate.setText(film.getDate().get_dd_MM_yyyy());
        lblTime.setText(film.getTime());
        lblDuration.setText(film.getDuration() + " [min]");
        lblSize.setText(film.getFilmSize().toString() + " [MB]");
        lblAbo.setText(film.getAboName());

        if (!film.arr[FilmDataXml.FILM_WEBSITE].isEmpty()) {
            P2Hyperlink hyperlink = new P2Hyperlink(film.arr[FilmDataXml.FILM_WEBSITE],
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
            hBoxUrl.getChildren().addAll(lblUrl, hyperlink);
        }
    }

    public void setFilm(DownloadData downloadData) {
//        System.out.println("=====> setFilm(DownloadData downloadData)" +
//                ++i + "  " + (downloadData != null ? downloadData.getTitle() : "null"));
        hBoxUrl.getChildren().clear();
        textArea.textProperty().removeListener(changeListener);
        if (this.downloadData != null) {
            this.downloadData.downloadSizeProperty().removeListener(sizeChangeListener);
        }

        this.film = null;
        this.downloadData = downloadData;

        if (downloadData == null) {
            lblTheme.setText("");
            lblTitle.setText("");
            textArea.clear();
            oldDescription = "";
            btnReset.setVisible(false);

            lblDate.setText("");
            lblTime.setText("");
            lblDuration.setText("");
            lblSize.setText("");
            lblAbo.setText("");
            return;
        }

        lblTheme.setText(downloadData.getChannel() + "  -  " + downloadData.getTheme());
        lblTitle.setText(downloadData.getTitle());
        lblDate.setText(downloadData.getFilmDate().get_dd_MM_yyyy());
        lblTime.setText(downloadData.getFilmTime());
        lblDuration.setText(downloadData.getDurationMinute() + " [min]");

        setSize(false); // die kann bim Film abweichen: HD, small
        downloadData.downloadSizeProperty().addListener(sizeChangeListener);
        lblAbo.setText(downloadData.getAboName());

        textArea.setText(downloadData.getDescription());
        textArea.setEditable(downloadData.isNotStartedOrFinished());
        oldDescription = downloadData.getDescription();
        btnReset.setVisible(false);
        textArea.textProperty().addListener(changeListener);

        if (!downloadData.getUrlWebsite().isEmpty()) {
            P2Hyperlink hyperlink = new P2Hyperlink(downloadData.getUrlWebsite(),
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
            hBoxUrl.getChildren().addAll(lblUrl, hyperlink);
        }
    }

    private void setSize(boolean async) {
        if (downloadData != null) {
            final String size = downloadData.getDownloadSize().toString();

            if (async) {
                Platform.runLater(() -> {
                    // die kann bim Film abweichen: HD, small
                    // und wird beim Download asynchron gesetzt
                    if (size.isEmpty()) {
                        lblSize.setText("");
                    } else {
                        lblSize.setText(size + " [MB]");
                    }
                });

            } else {
                if (size.isEmpty()) {
                    lblSize.setText("");
                } else {
                    lblSize.setText(size + " [MB]");
                }
            }
        }
    }

    private void setFilmDescription() {
        btnReset.setVisible(true);
        if (film != null) {
            film.setDescription(textArea.getText());
        }
        if (downloadData != null) {
            downloadData.setDescription(textArea.getText());
        }
    }

    private void resetFilmDescription() {
        btnReset.setVisible(false);
        if (film != null) {
            film.setDescription(oldDescription);
            textArea.setText(film.getDescription());
        }
        if (downloadData != null) {
            downloadData.setDescription(oldDescription);
            textArea.setText(downloadData.getDescription());
        }
    }
}

