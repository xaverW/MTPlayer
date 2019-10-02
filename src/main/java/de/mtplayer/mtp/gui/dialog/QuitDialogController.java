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
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class QuitDialogController extends PDialog {

    private final StackPane stackPane = new StackPane();
    private final PMaskerPane maskerPane = new PMaskerPane();
    private final WaitTask waitTask = new WaitTask();
    private final VBox vbox = new VBox();

    private boolean canQuit = false;
    private boolean startWithWaiting = false;

    public QuitDialogController(boolean startWithWaiting) {
        super(null, "Programm beenden", true);
        this.startWithWaiting = startWithWaiting;
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
            canQuit = true;
            close();
        });

        maskerPane.setMaskerVisible(false);
        maskerPane.setButtonText("Abbrechen");
        maskerPane.getButton().setOnAction(a -> close());

        Label headerLabel = new Label("Es laufen noch Downloads!");
        headerLabel.setStyle("-fx-font-size: 1.5em;");

        BigButton cancelButton = new BigButton(new ProgIcons().ICON_BUTTON_QUIT,
                "Nicht beenden", "");
        cancelButton.setOnAction(e -> {
            close();
        });

        BigButton quitButton = new BigButton(new ProgIcons().ICON_BUTTON_QUIT,
                "Beenden", "Alle Downloads abbrechen und das Programm beenden.");
        quitButton.setOnAction(e -> {
            canQuit = true;
            close();
        });

        BigButton waitButton = new BigButton(new ProgIcons().ICON_BUTTON_QUIT,
                "Warten", "Alle Downloads abwarten und dann das Programm beenden.");
        waitButton.setOnAction(e -> startWaiting());


        gridPane.add(new ProgIcons().ICON_DIALOG_QUIT, 0, 0, 1, 1);
        gridPane.add(headerLabel, 1, 0);
        gridPane.add(cancelButton, 1, 1);
        gridPane.add(quitButton, 1, 2);
        gridPane.add(waitButton, 1, 3);

        ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
        gridPane.getStyleClass().add("dialog-only-border");

        stackPane.getChildren().addAll(gridPane, maskerPane);
        vbox.getChildren().addAll(stackPane);

        if (startWithWaiting) {
            startWaiting();
        }
    }


    public void startWaiting() {
        maskerPane.setMaskerVisible(true, false, true);
        Thread th = new Thread(waitTask);
        th.setName("startWaiting");
        th.start();
    }

    private class WaitTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            while ((ProgData.getInstance().downloadList.countStartedAndRunningDownloads() > 0) && !isCancelled()) {
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
        if (waitTask.isRunning()) {
            waitTask.cancel();
        }
        maskerPane.switchOffMasker();
        super.close();
    }

    public boolean canTerminate() {
        return canQuit;
    }

}