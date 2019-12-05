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

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class FilmInfoDialogController extends PDialog {

    private final int FREE = 190;

    private final ScrollPane scrollPane = new ScrollPane();
    private final Text[] textTitle = new Text[FilmXml.MAX_ELEM];
    private final Label[] lblCont = new Label[FilmXml.MAX_ELEM];
    private final TextArea textArea = new TextArea();

    private final GridPane gridPane = new GridPane();
    private final Button btnOk = new Button("Ok");

    private final VBox vBoxDialog = new VBox();
    private final VBox vBoxCont = new VBox();

    private final ImageView ivHD = new ImageView();
    private final ImageView ivUT = new ImageView();
    private final ImageView ivNew = new ImageView();
    private final PToggleSwitch tglUrl = new PToggleSwitch("URL");

    private final PHyperlink pHyperlinkUrl = new PHyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

    private final PHyperlink pHyperlinkWebsite = new PHyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

    BooleanProperty urlProperty = ProgConfig.FILM_INFO_DIALOG_SHOW_URL.getBooleanProperty();

    public FilmInfoDialogController() {
        super(null, ProgConfig.SYSTEM_SIZE_DIALOG_FILMINFO.getStringProperty(),
                "Filminfos", false);

        init(vBoxDialog);
    }


    public void showFilmInfo() {
        showDialog();
    }


    public void setFilm(Film film) {
        Platform.runLater(() -> {

            for (int i = 0; i < FilmXml.MAX_ELEM; ++i) {
                if (film == null) {
                    lblCont[i].setText("");
                    textArea.setText("");
                    ivHD.setImage(null);
                    ivUT.setImage(null);
                    ivNew.setImage(null);
                    pHyperlinkUrl.setUrl("");
                    pHyperlinkWebsite.setUrl("");
                } else {
                    switch (i) {
                        case FilmXml.FILM_NR:
                            lblCont[i].setText(film.getNr() + "");
                            break;
                        case FilmXml.FILM_DURATION:
                            lblCont[i].setText(film.getDurationMinute() + "");
                            break;
                        case FilmXml.FILM_URL:
                            pHyperlinkUrl.setUrl(film.arr[FilmXml.FILM_URL]);
                            break;
                        case FilmXml.FILM_WEBSITE:
                            pHyperlinkWebsite.setUrl(film.arr[FilmXml.FILM_WEBSITE]);
                            break;
                        case FilmXml.FILM_DESCRIPTION:
                            textArea.setText(film.arr[i]);
                            break;
                        case FilmXml.FILM_HD:
                            if (film.isHd()) {
                                ivHD.setImage(new ProgIcons().ICON_DIALOG_EIN_SW);
                            } else {
                                ivHD.setImage(null);
                            }
                            break;
                        case FilmXml.FILM_UT:
                            if (film.isUt()) {
                                ivUT.setImage(new ProgIcons().ICON_DIALOG_EIN_SW);
                            } else {
                                ivUT.setImage(null);
                            }
                            break;
                        case FilmXml.FILM_NEW:
                            if (film.isNewFilm()) {
                                ivNew.setImage(new ProgIcons().ICON_DIALOG_EIN_SW);
                            } else {
                                ivNew.setImage(null);
                            }
                            break;

                        default:
                            lblCont[i].setText(film.arr[i]);
                    }
                }
            }
        });
    }

    private void initDialog() {
        final HBox hBoxUrl = new HBox();
        HBox.setHgrow(hBoxUrl, Priority.ALWAYS);
        hBoxUrl.getChildren().add(tglUrl);

        final HBox hBoxOk = new HBox();
        ButtonBar buttonBar = new ButtonBar();
        ButtonBar.setButtonData(btnOk, ButtonBar.ButtonData.OK_DONE);
        buttonBar.getButtons().add(btnOk);
        hBoxOk.getChildren().addAll(hBoxUrl, buttonBar);


        vBoxDialog.setSpacing(10);
        vBoxDialog.setPadding(new Insets(10));

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBoxCont);


        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStyleClass().add("dialog-filminfo");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        vBox.getChildren().add(scrollPane);

        vBoxDialog.getChildren().addAll(vBox, hBoxOk);
    }

    @Override
    public void make() {
        initDialog();

        tglUrl.setTooltip(new Tooltip("URL anzeigen"));
        tglUrl.selectedProperty().bindBidirectional(urlProperty);
        tglUrl.selectedProperty().addListener((observable, oldValue, newValue) -> setUrl());

//        btnOk.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        btnOk.setOnAction(a -> close());
        vBoxCont.getChildren().add(gridPane);
        VBox.setVgrow(gridPane, Priority.SOMETIMES);

        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setMinHeight(Control.USE_PREF_SIZE);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());


        int row = 0;
        for (int i = 0; i < FilmXml.MAX_ELEM; ++i) {

            textTitle[i] = new Text(FilmXml.COLUMN_NAMES[i] + ":");
            textTitle[i].setFont(Font.font(null, FontWeight.BOLD, -1));
            lblCont[i] = new Label("");
            lblCont[i].setWrapText(true);
            lblCont[i].maxWidthProperty().bind(vBoxDialog.widthProperty().subtract(FREE));

            switch (i) {
                case FilmXml.FILM_DATE_LONG:
                case FilmXml.FILM_PLAY:
                case FilmXml.FILM_RECORD:
                case FilmXml.FILM_URL_AUTH:
                case FilmXml.FILM_URL_HD:
                case FilmXml.FILM_URL_HISTORY:
                case FilmXml.FILM_URL_SMALL:
                case FilmXml.FILM_URL_RTMP:
                case FilmXml.FILM_URL_RTMP_HD:
                case FilmXml.FILM_URL_RTMP_SMALL:
                case FilmXml.FILM_URL_SUBTITLE:
                    // bis hier nicht anzeigen
                    break;

                case FilmXml.FILM_HD:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivHD, 1, row++);
                    gridPane.getRowConstraints().add(new RowConstraints());
                    break;
                case FilmXml.FILM_UT:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivUT, 1, row++);
                    gridPane.getRowConstraints().add(new RowConstraints());
                    break;
                case FilmXml.FILM_NEW:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivNew, 1, row++);
                    gridPane.getRowConstraints().add(new RowConstraints());
                    break;

                case FilmXml.FILM_URL:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(pHyperlinkUrl, 1, row++);
                    gridPane.getRowConstraints().add(new RowConstraints());
                    break;
                case FilmXml.FILM_WEBSITE:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(pHyperlinkWebsite, 1, row++);
                    gridPane.getRowConstraints().add(new RowConstraints());
                    break;

                case FilmXml.FILM_DESCRIPTION:
                    textArea.setMaxHeight(Double.MAX_VALUE);
                    textArea.setPrefRowCount(6);
                    textArea.setWrapText(true);
                    textArea.setEditable(false);
                    textArea.maxWidthProperty().bind(vBoxDialog.widthProperty().subtract(FREE));

                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(textArea, 1, row++);

                    RowConstraints regRow = new RowConstraints();
                    regRow.setVgrow(Priority.ALWAYS);
                    gridPane.getRowConstraints().add(regRow);
                    break;

                default:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(lblCont[i], 1, row++);
                    gridPane.getRowConstraints().add(new RowConstraints());

                    final int ii = i;
                    lblCont[i].setOnContextMenuRequested(event ->
                            getMenu(lblCont[ii].getText()).show(lblCont[ii], event.getScreenX(), event.getScreenY()));
            }
        }

        setUrl();
    }

    private void setUrl() {
        pHyperlinkUrl.setWrapText(true);
        pHyperlinkUrl.maxWidthProperty().bind(vBoxDialog.widthProperty().subtract(FREE));

        pHyperlinkWebsite.setWrapText(true);
        pHyperlinkWebsite.maxWidthProperty().bind(vBoxDialog.widthProperty().subtract(FREE));

        textTitle[FilmXml.FILM_URL].setVisible(urlProperty.get());
        textTitle[FilmXml.FILM_URL].setManaged(urlProperty.get());
        pHyperlinkUrl.setVisible(urlProperty.get());
        pHyperlinkUrl.setManaged(urlProperty.get());
        pHyperlinkUrl.setMinHeight(Region.USE_PREF_SIZE);

        textTitle[FilmXml.FILM_WEBSITE].setVisible(urlProperty.get());
        textTitle[FilmXml.FILM_WEBSITE].setManaged(urlProperty.get());
        pHyperlinkWebsite.setVisible(urlProperty.get());
        pHyperlinkWebsite.setManaged(urlProperty.get());
        pHyperlinkWebsite.setMinHeight(Region.USE_PREF_SIZE);
    }

    private ContextMenu getMenu(String url) {
        final ContextMenu contextMenu = new ContextMenu();

        MenuItem resetTable = new MenuItem("kopieren");
        resetTable.setOnAction(a -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(url);
            clipboard.setContent(content);
        });
        contextMenu.getItems().addAll(resetTable);
        return contextMenu;
    }
}
