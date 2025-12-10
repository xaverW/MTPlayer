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
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BookmarkDelDialog extends P2DialogExtra {

    private final ProgData progData;
    private final Label lblAge = new Label();
    private final Slider slAge = new Slider();
    private final P2ToggleSwitch tglAll = new P2ToggleSwitch("Alle löschen:");
    private final P2ToggleSwitch tglShown = new P2ToggleSwitch("Gesehene:");
    private final P2ToggleSwitch tglAge = new P2ToggleSwitch("Mit Alter:");
    private final Label lblCount = new Label("");
    private final ChangeListener<Boolean> tglChangeListener;
    private final ChangeListener<Number> slChangeListener;
    private boolean isOk = false;

    public BookmarkDelDialog(ProgData progData, Stage stage) {
        super(stage, ProgConfig.BOOKMARK_DIALOG_DEL_SIZE, "Bookmarks löschen",
                true, true, true, DECO.BORDER_SMALL);
        this.progData = progData;

        tglChangeListener = (o, u, n) -> {
            setCount();
        };
        slChangeListener = (o, u, n) -> {
            writeDelAge();
            setCount();
        };

        init(true);
    }

    @Override
    public void make() {
        slAge.setMin(1);
        slAge.setMax(50);
        slAge.setShowTickLabels(false);
        slAge.setMajorTickUnit(100);
        slAge.setBlockIncrement(5);


        final GridPane gridPane1 = new GridPane();
        gridPane1.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane1.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane1.setPadding(new Insets(P2LibConst.PADDING));
        gridPane1.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());

        int row1 = 0;
        gridPane1.add(tglAll, 0, row1, 2, 1);
        gridPane1.add(new Label("   "), 0, ++row1);

        final GridPane gridPane2 = new GridPane();
        gridPane2.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane2.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane2.setPadding(new Insets(P2LibConst.PADDING));
        gridPane2.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        int row2 = 0;
        gridPane2.add(tglShown, 0, row2, 3, 1);
        ++row2;
        gridPane2.add(tglAge, 0, row2, 3, 1);
        gridPane2.add(new Label(""), 0, ++row2);
        gridPane2.add(slAge, 1, row2);
        gridPane2.add(lblAge, 2, row2);
        GridPane.setHalignment(lblAge, HPos.RIGHT);

        Label lbl = new Label("100 Tage");
        lbl.setVisible(false);
        gridPane2.add(lbl, 2, row2);

        gridPane1.add(gridPane2, 1, row1);
        gridPane2.disableProperty().bind(tglAll.selectedProperty());
        getVBoxCont().getChildren().add(gridPane1);
        getVBoxCont().setPadding(new Insets(P2LibConst.PADDING));


        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(lblCount);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        getVBoxCont().getChildren().addAll(P2GuiTools.getVBoxGrower(), hBox);

        slAge.disableProperty().bind(tglAge.selectedProperty().not());
        lblAge.disableProperty().bind(tglAge.selectedProperty().not());

        tglAll.selectedProperty().bindBidirectional(ProgConfig.BOOKMARK_DEL_ALL);
        tglShown.selectedProperty().bindBidirectional(ProgConfig.BOOKMARK_DEL_SHOWN);
        tglAge.selectedProperty().bindBidirectional(ProgConfig.BOOKMARK_DEL_OLD);
        slAge.valueProperty().bindBidirectional(ProgConfig.BOOKMARK_DEL_OLD_COUNT_DAYS);

        tglAll.selectedProperty().addListener(tglChangeListener);
        tglShown.selectedProperty().addListener(tglChangeListener);
        tglAge.selectedProperty().addListener(tglChangeListener);
        slAge.valueProperty().addListener(slChangeListener);

        writeDelAge();
        setCount();


        Button btnOk = new Button("Ok");
        Button btnCancel = new Button("Abbrechen");
        btnOk.setOnAction(a -> {
            quit(true);
        });
        btnOk.disableProperty().bind(
                (tglAll.selectedProperty().not())
                        .and(tglShown.selectedProperty().not())
                        .and(tglAge.selectedProperty().not())
        );

        btnCancel.setOnAction(a -> {
            quit(false);
        });
        Button btnHelp = P2Button.helpButton(getStage(), "Bookmarks löschen",
                "Hier kann vorgegeben werden, welche Bookmarks gelöscht werden sollen." +
                        "\n\n" +
                        "* Alle löschen:\n" +
                        "   Es werden alle Bookmarks gelöscht" +
                        "\n" +
                        "* Gesehene:\n" +
                        "   Bookmarks für Filme die schon gesehen wurden" +
                        "\n" +
                        "* Ohne Film in der Liste:\n" +
                        "   Bookmarks, deren Film nicht mehr in der Filmliste enthalten ist" +
                        "\n" +
                        "* Mit Alter:\n" +
                        "   Bookmarks deren Datum \"Angelegt\" älter ist als vorgegeben");
        addHlpButton(btnHelp);
        addOkCancelButtons(btnOk, btnCancel);
    }

    private void setCount() {
        int count = BookmarkFactory.deleteFromDialog(getStage(), true);
        lblCount.setText("Anzahl zum Löschen: " + count);
    }

    private void writeDelAge() {
        lblAge.setText(ProgConfig.BOOKMARK_DEL_OLD_COUNT_DAYS.get() + " Tage");
    }

    private void quit(boolean ok) {
        tglAll.selectedProperty().removeListener(tglChangeListener);
        tglShown.selectedProperty().removeListener(tglChangeListener);
        tglAge.selectedProperty().removeListener(tglChangeListener);
        slAge.valueProperty().removeListener(slChangeListener);

        tglAll.selectedProperty().unbindBidirectional(ProgConfig.BOOKMARK_DEL_ALL);
        tglShown.selectedProperty().unbindBidirectional(ProgConfig.BOOKMARK_DEL_SHOWN);
        tglAge.selectedProperty().unbindBidirectional(ProgConfig.BOOKMARK_DEL_OLD);
        slAge.valueProperty().unbindBidirectional(ProgConfig.BOOKMARK_DEL_OLD_COUNT_DAYS);

        isOk = ok;
        close();
    }

    public boolean isOk() {
        return isOk;
    }
}
