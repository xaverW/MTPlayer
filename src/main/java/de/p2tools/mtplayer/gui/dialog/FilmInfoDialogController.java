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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataXml;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

public class FilmInfoDialogController extends P2DialogExtra {
    private static FilmInfoDialogController instance;

    private final Text[] textTitle = new Text[FilmDataXml.MAX_ELEM];
    private final Label[] lblCont = new Label[FilmDataXml.MAX_ELEM];
    private final TextArea textArea = new TextArea();

    private final GridPane gridPane = new GridPane();
    private final Button btnOk = new Button("_Ok");

    private final FontIcon ivHD = PIconFactory.PICON.ICON_BOOLEAN_ON.getFontIcon();
    private final FontIcon ivUT = PIconFactory.PICON.ICON_BOOLEAN_ON.getFontIcon();
    private final FontIcon ivNew = PIconFactory.PICON.ICON_BOOLEAN_ON.getFontIcon();

    private final P2Hyperlink p2HyperlinkUrlSmall = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL);
    private final P2Hyperlink p2HyperlinkUrlHeight = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL);
    private final P2Hyperlink p2HyperlinkUrlHd = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL);

    private final P2Hyperlink p2HyperlinkWebsite = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL);

    private FilmInfoDialogController() {
        super(ProgData.getInstance().primaryStage, ProgConfig.FILM_INFO_DIALOG_SIZE,
                "Filminfos", false, true, true, DECO.BORDER_SMALL, true);

        init(false);
    }

    public void showFilmInfo() {
        showDialog();
    }

    public void setFilm(FilmDataMTP film) {
        Platform.runLater(() -> {
            //braucht es aktuell (noch) nicht: Platform ....
            for (int i = 0; i < FilmDataXml.MAX_ELEM; ++i) {
                if (film == null) {
                    lblCont[i].setText("");
                    textArea.setText("");
//                    ivHD.setImage(null);
//                    ivUT.setImage(null);
//                    ivNew.setImage(null);
                    p2HyperlinkUrlSmall.setUrl("");
                    p2HyperlinkUrlHeight.setUrl("");
                    p2HyperlinkUrlHd.setUrl("");
                    p2HyperlinkWebsite.setUrl("");
                } else {
                    switch (i) {
                        case FilmDataXml.FILM_NR:
                            lblCont[i].setText(film.getNo() + "");
                            break;
                        case FilmDataXml.FILM_DURATION:
                            lblCont[i].setText(film.getDurationMinute() + "");
                            break;
                        case FilmDataXml.FILM_URL:

                            p2HyperlinkUrlSmall.setUrl(film.isSmall() ? film.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL) : "");
                            p2HyperlinkUrlHeight.setUrl(film.arr[FilmDataXml.FILM_URL]);
                            p2HyperlinkUrlHd.setUrl(film.isHd() ? film.getUrlForResolution(FilmDataMTP.RESOLUTION_HD) : "");
                            break;
                        case FilmDataXml.FILM_WEBSITE:
                            p2HyperlinkWebsite.setUrl(film.arr[FilmDataXml.FILM_WEBSITE]);
                            break;
                        case FilmDataXml.FILM_DESCRIPTION:
                            textArea.setText(film.arr[i]);
                            break;
                        case FilmDataXml.FILM_HD:
                            if (film.isHd()) {
                                ivHD.setVisible(true);
                            } else {
                                ivHD.setVisible(false);
                            }
                            break;
                        case FilmDataXml.FILM_UT:
                            if (film.isUt()) {
                                ivUT.setVisible(true);
                            } else {
                                ivUT.setVisible(false);
                            }
                            break;
                        case FilmDataXml.FILM_NEW:
                            if (film.isNewFilm()) {
                                ivNew.setVisible(true);
                            } else {
                                ivNew.setVisible(false);
                            }
                            break;

                        default:
                            lblCont[i].setText(film.arr[i]);
                    }
                }
            }
        });
    }

    @Override
    public void updateCss() {
        super.updateCss();
        for (int i = 0; i < FilmDataXml.MAX_ELEM; ++i) {
            if (textTitle[i] == null) {
                return;
            }
            textTitle[i].getStyleClass().add("downloadGuiMediaText");
        }
    }

    @Override
    public void make() {
        this.getMaskerPane().visibleProperty().bind(ProgData.getInstance().maskerPane.visibleProperty());

        TitledPane tpUrl;
        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());
        getVBoxCont().getChildren().add(gridPane);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5));
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());

        int row = 0;
        for (int i = 0; i < FilmDataXml.MAX_ELEM; ++i) {
            textTitle[i] = new Text(FilmDataXml.COLUMN_NAMES[i] + ":");
            textTitle[i].setFont(Font.font(null, FontWeight.BOLD, -1));
            textTitle[i].getStyleClass().add("downloadGuiMediaText");
            lblCont[i] = new Label("");
            lblCont[i].setWrapText(true);

            switch (i) {
                case FilmDataXml.FILM_DATE_LONG:
                case FilmDataXml.FILM_PLAY:
                case FilmDataXml.FILM_RECORD:
                case FilmDataXml.FILM_URL_HD:
                case FilmDataXml.FILM_URL_HISTORY:
                case FilmDataXml.FILM_URL_SMALL:
                case FilmDataXml.FILM_URL_SUBTITLE:
                    // bis hier nicht anzeigen
                    break;

                case FilmDataXml.FILM_HD:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivHD, 1, row++);
                    break;
                case FilmDataXml.FILM_UT:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivUT, 1, row++);
                    break;
                case FilmDataXml.FILM_NEW:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivNew, 1, row++);
                    break;
                case FilmDataXml.FILM_URL:
                    p2HyperlinkUrlSmall.setWrapText(true);
                    p2HyperlinkUrlHeight.setWrapText(true);
                    p2HyperlinkUrlHd.setWrapText(true);

                    tpUrl = new TitledPane("", new HBox());
                    tpUrl.expandedProperty().bindBidirectional(ProgConfig.FILM_INFO_DIALOG_SHOW_URL);
                    final var gUrl = new GridPane();
                    gUrl.setHgap(10);
                    gUrl.setVgap(0);
//                    gUrl.setGridLinesVisible(true);
                    gUrl.setPadding(new Insets(0));
                    gUrl.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                            P2GridConstraints.getCcComputedSizeAndHgrow());

                    gUrl.add(new Label("Niedrig:"), 0, 0);
                    gUrl.add(p2HyperlinkUrlSmall, 1, 0);
                    gUrl.add(new Label("Hoch:"), 0, 1);
                    gUrl.add(p2HyperlinkUrlHeight, 1, 1);
                    gUrl.add(new Label("HD:"), 0, 2);
                    gUrl.add(p2HyperlinkUrlHd, 1, 2);
                    tpUrl.setContent(gUrl);

                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(tpUrl, 1, row++, 2, 1);
                    break;
                case FilmDataXml.FILM_WEBSITE:
                    p2HyperlinkWebsite.setWrapText(true);
                    tpUrl = new TitledPane("", new HBox());
                    tpUrl.expandedProperty().bindBidirectional(ProgConfig.FILM_INFO_DIALOG_SHOW_WEBSITE_URL);
                    final var gWeb = new GridPane();
                    gWeb.add(textTitle[i], 0, 0);
                    gWeb.add(p2HyperlinkWebsite, 1, 1, 3, 1);
                    tpUrl.setContent(gWeb);

                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(tpUrl, 1, row++, 2, 1);
                    break;
                case FilmDataXml.FILM_DESCRIPTION:
                    textArea.setMaxHeight(Double.MAX_VALUE);
                    textArea.setPrefRowCount(6);
                    textArea.setWrapText(true);
                    textArea.setEditable(false);

                    tpUrl = new TitledPane("", new HBox());
                    tpUrl.expandedProperty().bindBidirectional(ProgConfig.FILM_INFO_DIALOG_SHOW_DESCRIPTION);
                    final var gDescription = new GridPane();
                    gDescription.add(textTitle[i], 0, 0);
                    gDescription.add(textArea, 1, 1, 3, 1);
                    tpUrl.setContent(gDescription);

                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(tpUrl, 1, row++);

                    break;
                default:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(lblCont[i], 1, row++);
                    final int ii = i;
                    lblCont[i].setOnContextMenuRequested(event ->
                            getMenu(lblCont[ii], event));
            }
        }
    }

    private void getMenu(Label lbl, ContextMenuEvent event) {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Kopieren");
        menuItem.setOnAction(a -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(lbl.getText());
            clipboard.setContent(content);
        });
        contextMenu.getItems().addAll(menuItem);
        contextMenu.show(lbl, event.getScreenX(), event.getScreenY());
    }

    public synchronized static FilmInfoDialogController getInstance() {
        if (instance == null) {
            instance = new FilmInfoDialogController();
        }
        return instance;
    }

    public synchronized static FilmInfoDialogController getInstanceAndShow() {
        if (instance == null) {
            instance = new FilmInfoDialogController();
        }

        if (!instance.isShowing()) {
            instance.showDialog();
        }
        instance.getStage().toFront();

        return instance;
    }
}
