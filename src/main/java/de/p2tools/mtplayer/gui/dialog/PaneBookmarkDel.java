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
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaneBookmarkDel extends VBox {

    private final ProgData progData;
    private final Slider slAge = new Slider();
    private final Label lblAge = new Label();
    private final ObjectProperty<Stage> stageObjectProperty;
    private final Button btnDelAll = new Button("");
    private final Button btnDelShown = new Button("");
    private final Button btnDelAge = new Button("");
    private final Button btnDelNotInList = new Button("");

    public PaneBookmarkDel(ProgData progData, ObjectProperty<Stage> stageObjectProperty) {
        this.progData = progData;
        this.stageObjectProperty = stageObjectProperty;
        make();
    }

    public void close() {
        slAge.valueProperty().unbindBidirectional(ProgConfig.BOOKMARK_DEL_OLD_COUNT_DAYS);
    }

    private void make() {
        slAge.setMin(1);
        slAge.setMax(50);
        slAge.setShowTickLabels(false);
        slAge.setMajorTickUnit(100);
        slAge.setBlockIncrement(5);

        btnDelAge.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnDelAge.setOnAction(a -> {
            BookmarkFactory.deleteAge(stageObjectProperty.get(), (int) slAge.getValue());
        });

        btnDelAll.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnDelAll.setOnAction(a -> {
            int size = BookmarkFactory.deleteAll(stageObjectProperty.get(), false, true);
            size += BookmarkFactory.deleteAll(stageObjectProperty.get(), false, false);
            if (size <= 0) {
                P2Alert.showInfoAlert(stageObjectProperty.get(), "Löschen", "Bookmarks löschen",
                        "Es sind keine Bookmarks zum Löschen, in der Liste.");
            }
        });

        btnDelNotInList.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnDelNotInList.setOnAction(a -> {
            int size = BookmarkFactory.delNotInList(true);
            size += BookmarkFactory.delNotInList(false);
            if (size <= 0) {
                P2Alert.showInfoAlert(stageObjectProperty.get(), "Löschen", "Bookmarks löschen",
                        "Es sind keine Bookmarks zum Löschen, in der Liste.");
            }
        });

        btnDelShown.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnDelShown.setOnAction(a -> BookmarkFactory.deleteShown(stageObjectProperty.get()));

        Button btnHelp = PIconFactory.getHelpButton(stageObjectProperty,
                "Bookmarks löschen",
                "Hier können Bookmarks gelöscht werden." +
                        "\n\n" +
                        "* Alle löschen:\n" +
                        "Es werden alle Bookmarks gelöscht." +
                        "\n\n" +
                        "* Gesehene löschen:\n" +
                        "Bookmarks für Filme/Audios die schon gesehen wurden, werden gelöscht." +
                        "\n\n" +
                        "* Nicht mehr in der Film/Audio Liste:\n" +
                        "Bookmarks, deren Film/Audio nicht mehr in der Film/Audio Liste enthalten sind, werden gelöscht." +
                        "\n\n" +
                        "* Alte löschen:\n" +
                        "Bookmarks deren Datum \"Angelegt\" gleich oder älter ist als vorgegeben, werde gelöscht.");

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(15);

        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(), P2GridConstraints.getCcPrefSize());

        int row = 0;
        gridPane.add(new Label("Alle löschen"), 0, row);
        gridPane.add(btnDelAll, 2, row);

        gridPane.add(new Label("Gesehene löschen"), 0, ++row);
        gridPane.add(btnDelShown, 2, row);

        gridPane.add(new Label("Nicht mehr in der Film/Audio Liste"), 0, ++row);
        gridPane.add(btnDelNotInList, 2, row);

        gridPane.add(new Label("Alte löschen"), 0, ++row);
        HBox hBox = new HBox(P2LibConst.PADDING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(slAge, lblAge);
        HBox.setHgrow(slAge, Priority.ALWAYS);
        gridPane.add(hBox, 1, row);
        gridPane.add(btnDelAge, 2, row);

        gridPane.add(btnHelp, 2, ++row);

        getChildren().add(gridPane);
        setPadding(new Insets(P2LibConst.PADDING));

        slAge.valueProperty().bindBidirectional(ProgConfig.BOOKMARK_DEL_OLD_COUNT_DAYS);
        slAge.setValue(ProgConfig.BOOKMARK_DEL_OLD_COUNT_DAYS.get());
        lblAge.setText((int) slAge.getValue() + " Tage");
        slAge.valueProperty().addListener((u, o, n) -> lblAge.setText((int) slAge.getValue() + " Tage"));
    }
}
