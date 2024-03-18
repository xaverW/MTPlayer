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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.LiveSearchZdf;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadErrorDialogController;
import de.p2tools.p2lib.dialogs.ProgInfoDialog;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPaneIndeterminate;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilter.FilmFilterCheck;
import de.p2tools.p2lib.mtfilter.Filter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Predicate;

public class MTPTester {
    private final ProgInfoDialog progInfoDialog;
    private final ProgData progData;
    private final TextArea textArea = new TextArea();
    private String text = "";
    private boolean ard = false;
    private final P2MaskerPaneIndeterminate maskerPane = new P2MaskerPaneIndeterminate();
    private final WaitTask waitTask = new WaitTask();

    public MTPTester(final ProgData progData) {
        this.progData = progData;
        this.progInfoDialog = new ProgInfoDialog(false);

        maskerPane.switchOffMasker();
        maskerPane.setButtonText("Abbrechen");
        maskerPane.setTxtBtnHorizontal(false);
        maskerPane.getButton().setOnAction(a -> closeMaskerPane());


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

            final Text text = new Text("Debugtools");
            text.setFont(Font.font(null, FontWeight.BOLD, 15));

            int row = 0;
            gridPane.add(new Label(), 0, ++row);

            final Button btnTable = new Button("Table");
            final CheckBox chkFilter = new CheckBox("Filtern");
            final CheckBox chkSelect = new CheckBox("Select");

            gridPane.add(new Label(), 0, ++row);
            gridPane.add(btnTable, 0, ++row);
            gridPane.add(chkFilter, 1, row);
            gridPane.add(chkSelect, 2, row);


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

            Button btnError = new Button("Fehler");
            btnError.setOnAction(a -> {
                Platform.runLater(() -> new DownloadErrorDialogController(new DownloadData()));
            });
            gridPane.add(btnError, 0, ++row);

            Button btnZdf = new Button("Zdf");
            btnZdf.setOnAction(a -> {
                final JsonInfoDto jsonInfoDto = new JsonInfoDto();

                jsonInfoDto.init();
                jsonInfoDto.setPageNo(0);
                jsonInfoDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH.getValue());

                List<FilmDataMTP> list = LiveSearchZdf.loadLive(jsonInfoDto);
                Platform.runLater(() -> {
                    list.forEach(ProgData.getInstance().liveFilmFilterWorker.getLiveFilmList()::importFilmOnlyWithNr);
                });
            });
            gridPane.add(btnZdf, 0, ++row);

            Button btnWait = new Button("Warten");
            btnWait.setOnAction(a -> {
                maskerPane.setMaskerVisible(true,
                        true,
                        true);
                maskerPane.setMaskerText("Test");

                Thread th = new Thread(waitTask);
                th.setName("startWaiting");
                th.start();
            });
            gridPane.add(btnWait, 0, ++row);

        }
    }

    public void closeMaskerPane() {
        if (waitTask.isRunning()) {
            waitTask.cancel();
        }
        maskerPane.switchOffMasker();
    }

    static int i = 100;

    private static class WaitTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            while (i > 0) {
                --i;
                try {
                    Thread.sleep(500);
                } catch (Exception ignore) {
                }
            }

            return null;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return super.cancel(mayInterruptIfRunning);
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
