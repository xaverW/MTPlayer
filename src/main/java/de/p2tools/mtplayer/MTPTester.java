/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.dialog.QuitDialogController;
import de.p2tools.p2lib.dialogs.ProgInfoDialog;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPaneIndeterminate;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilter.FilmFilterCheck;
import de.p2tools.p2lib.mtfilter.Filter;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.function.Predicate;

public class MTPTester {
    private final ProgInfoDialog progInfoDialog;
    private final ProgData progData;
    private final P2MaskerPaneIndeterminate maskerPane = new P2MaskerPaneIndeterminate();

    public MTPTester(final ProgData progData) {
        this.progData = progData;
        this.progInfoDialog = new ProgInfoDialog(false);
        addProgTest();
    }

    public void showDialog() {
        progInfoDialog.showDialog();
    }

    private void addProgTest() {
        if (progInfoDialog != null) {

            final GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(10));
            gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow());

            final StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(gridPane);
            progInfoDialog.getVBoxCont().getChildren().addAll(stackPane, maskerPane);

            final Text text = new Text("DebugTools");
            text.setFont(Font.font(null, FontWeight.BOLD, 15));

            int row = 0;
            Button btnAddBlack = new Button("5_000 Black");
            btnAddBlack.setOnAction(a -> {
                progData.blackList.clearList();
                for (int i = 0; i < 5_000; ++i) {
                    BlackData bl = new BlackData();
                    bl.setThemeTitle(i + "");
                    progData.blackList.add(bl);
                }
            });
            gridPane.add(btnAddBlack, 0, ++row);

            Button btnBlack = new Button("Gen-Black");
            btnBlack.setOnAction(a -> {
                BlacklistFilterFactory.markFilmBlackThread(true);
            });
            gridPane.add(btnBlack, 0, ++row);


            Button btnQuitt = new Button("Quitt, waiting false");
            btnQuitt.setOnAction(a -> {
                new QuitDialogController(false);
            });
            gridPane.add(btnQuitt, 0, ++row);
        }
    }

    public void close() {
        maskerPane.switchOffMasker();
    }

    private static Predicate<FilmData> getPredicate(String channel) {
        Filter fChannel = new Filter(channel, true);
        Predicate<FilmData> predicate = film -> true;
        predicate = predicate.and(f -> FilmFilterCheck.checkMatchChannelSmart(fChannel, f));
        return predicate;
    }
}
