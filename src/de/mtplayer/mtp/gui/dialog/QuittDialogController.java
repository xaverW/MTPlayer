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


import de.mtplayer.mLib.tools.BigButton;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import org.controlsfx.control.MaskerPane;

public class QuittDialogController extends MTDialog {

    final Daten daten;
    final StackPane stackPane;
    final MaskerPane maskerPane;
    final WaitTask waitTask;
    final VBox vbox;

    boolean canQuitt = false;

    public QuittDialogController(Daten daten) {
        super("", null,
                "Programm beenden", true);

        this.daten = daten;

        stackPane = new StackPane();
        maskerPane = new MaskerPane();
        waitTask = new WaitTask();
        vbox = new VBox();

        init(vbox, true);
    }


    @Override
    public void make() {
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(30);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(25);

        waitTask.setOnSucceeded(event -> {
            canQuitt = true;
            close();
        });

        maskerPane.setVisible(false);

        Label headerLabel = new Label("Es laufen noch Downloads!");
        headerLabel.setStyle("-fx-font-size: 1.5em;");

        BigButton cancelButton = new BigButton(new Icons().ICON_BUTTON_QUITT, "Nicht beenden",
                "");
        cancelButton.setOnAction(e -> close());

        BigButton quittButton = new BigButton(new Icons().ICON_BUTTON_QUITT, "Beenden",
                "Alle Downloads abbrechen und das Programm beenden.");
        quittButton.setOnAction(e -> {
            canQuitt = true;
            close();
        });

        BigButton waitButton = new BigButton(new Icons().ICON_BUTTON_QUITT, "Warten",
                "Alle Downloads abwarten und dann das Programm beenden.");
        waitButton.setOnAction(e -> {
            startWaiting();
        });


        gridPane.add(new Icons().ICON_DIALOG_QUITT, 0, 0, 1, 1);
        gridPane.add(headerLabel, 1, 0);
        gridPane.add(cancelButton, 1, 1);
        gridPane.add(quittButton, 1, 2);
        gridPane.add(waitButton, 1, 3);

        ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
        gridPane.getStyleClass().add("dialog-only-border");

        stackPane.getChildren().addAll(gridPane, maskerPane);
        vbox.getChildren().addAll(stackPane);
    }


    private void startWaiting() {
        maskerPane.setVisible(true);
        new Thread(waitTask).start();
    }

    private class WaitTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            while ((Daten.getInstance().downloadList.countRunningDownloads() > 0) && !isCancelled()) {
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

    public boolean escEvent(KeyEvent keyEvent) {
        if (maskerPane.isVisible()) {
            maskerPane.setVisible(false);
            waitTask.cancel();
            return false;
        }
        return true;
    }

    public void close() {
        if (waitTask.isRunning()) {
            waitTask.cancel();
        }
        super.close();
    }

    public boolean canTerminate() {
        return canQuitt;
    }

}