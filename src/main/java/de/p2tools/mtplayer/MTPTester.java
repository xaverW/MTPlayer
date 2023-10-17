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
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadErrorDialogController;
import de.p2tools.p2lib.dialogs.ProgInfoDialog;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPane;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilter.FilmFilterCheck;
import de.p2tools.p2lib.mtfilter.Filter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.function.Predicate;

public class MTPTester {
    private final ProgInfoDialog progInfoDialog;
    private final ProgData progData;
    private final TextArea textArea = new TextArea();
    private String text = "";
    private final P2MaskerPane maskerPane = new P2MaskerPane();
    private boolean ard = false;

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

            maskerPane.switchOffMasker();
            maskerPane.setButtonText("Abbrechen");
            maskerPane.getButton().setOnAction(a -> close());

            final StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(gridPane, maskerPane);
            progInfoDialog.getVBoxCont().getChildren().addAll(stackPane);

            final Text text = new Text("Debugtools");
            text.setFont(Font.font(null, FontWeight.BOLD, 15));

            int row = 0;
            gridPane.add(new Label(), 0, ++row);

            final CheckBox chkFilter = new CheckBox("Filtern");
            final CheckBox chkSelect = new CheckBox("Select");
            final Button btnTable = new Button("Table");

            gridPane.add(new Label(), 0, ++row);
            gridPane.add(btnTable, 0, ++row);
            gridPane.add(chkFilter, 1, row);
            gridPane.add(chkSelect, 2, row);

            Button btnError = new Button("Fehler");
            btnError.setOnAction(a -> {
                Platform.runLater(() -> new DownloadErrorDialogController(new DownloadData(), "so ein Mist"));
            });
            gridPane.add(btnError, 0, ++row);

            btnTable.setOnAction(a -> {
                TableView<FilmDataMTP> tableView = ProgData.getInstance().filmGuiController.tableView;
                if (chkFilter.isSelected()) {
                    ard = !ard;
                    if (ard) {
                        progData.filmListFiltered.filteredListSetPred(getPredicate("ard"));
                    } else {
                        progData.filmListFiltered.filteredListSetPred(getPredicate("zdf"));
                    }
                }

                if (chkSelect.isSelected()) {
                    int i = tableView.getItems().size();
                    i = (int) (Math.random() * (i + 1));
                    System.out.println(i);
                    tableView.getSelectionModel().clearAndSelect(i);
                    tableView.scrollTo(i);
                }
            });
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
