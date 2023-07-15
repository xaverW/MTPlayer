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


import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.BigButton;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.guitools.pmask.PMaskerPane;
import de.p2tools.p2lib.mtdownload.HttpDownload;
import de.p2tools.p2lib.tools.PShutDown;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class QuitDialogController extends PDialogExtra {

    private CheckBox chkShutDown = new CheckBox("Rechner anschließend herunterfahren");
    private Label lblSystemCall = new Label("");

    private final StackPane stackPane = new StackPane();
    private final PMaskerPane maskerPane = new PMaskerPane();
    private final WaitTask waitTask = new WaitTask();
    private final boolean startWithWaiting;

    public QuitDialogController(boolean startWithWaiting) {
        super(ProgData.getInstance().primaryStage, null, "Programm beenden",
                true, false, DECO.BORDER);
        this.startWithWaiting = startWithWaiting;
        addButton();
        init(true);
    }

    @Override
    public void make() {
        maskerPane.switchOffMasker();
        maskerPane.setButtonText("Abbrechen");
        maskerPane.setTxtBtnHorizontal(false);
        maskerPane.getButton().setOnAction(a -> closeMaskerPane());

        Label headerLabel = new Label("Es laufen noch Downloads!");
        headerLabel.setStyle("-fx-font-size: 1.5em;");

        // nicht beenden
        BigButton cancelButton = new BigButton(ProgIconsMTPlayer.ICON_BUTTON_QUIT.getImageView(),
                "Nicht beenden", "");
        cancelButton.setOnAction(e -> {
            close();
        });

        // beenden
        BigButton quitButton = new BigButton(ProgIconsMTPlayer.ICON_BUTTON_QUIT.getImageView(),
                "Beenden", "Alle Downloads abbrechen und das Programm beenden.");
        quitButton.setOnAction(e -> {
            ProgQuit.quit();
        });

        // warten, dann beenden
        BigButton waitButton = new BigButton(ProgIconsMTPlayer.ICON_BUTTON_QUIT.getImageView(),
                "Warten", "Alle Downloads abwarten und dann das Programm beenden.");
        waitButton.setOnAction(e -> startWaiting());
        chkShutDown.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SHUT_DOWN_CALL_ON);
        setSystemCallText();
        ProgConfig.SYSTEM_SHUT_DOWN_CALL.addListener((u, o, n) -> {
            setSystemCallText();
        });
        waitTask.setOnSucceeded(event -> {
            if (chkShutDown.isSelected()) {
                ProgQuit.quitShutDown();
            } else {
                ProgQuit.quit();
            }
        });

        final Button btnHelp = PButton.helpButton(getStage(), "Rechner herunterfahren", HelpText.CONFIG_SHUT_DOWN_CALL);
        final Button btnEdit = new Button();
        btnEdit.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_EDIT_FILTER.getImageView());
        btnEdit.setOnAction(a -> new ChangeShutDownCallDialog(getStageProp().getValue()));
        btnEdit.setTooltip(new Tooltip("Systembefehl nach Programmende anpassen"));

        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20));

        int row = 0;
        gridPane.add(ProgIconsMTPlayer.ICON_DIALOG_QUIT.getImageView(), 0, 0, 1, 1);
        gridPane.add(headerLabel, 1, row);
        gridPane.add(cancelButton, 1, ++row);
        gridPane.add(quitButton, 1, ++row);

        gridPane.add(new Label(), 1, ++row);
        gridPane.add(waitButton, 1, ++row);

        VBox vBox = new VBox(1);
        HBox hBox = new HBox(P2LibConst.DIST_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        HBox hBoxCall = new HBox(0);
        hBoxCall.getChildren().addAll(PGuiTools.getVDistance(25), lblSystemCall);
        hBox.getChildren().addAll(chkShutDown, PGuiTools.getHBoxGrower(), btnEdit, btnHelp);
        vBox.getChildren().addAll(hBox, hBoxCall);
        gridPane.add(vBox, 1, ++row);

        stackPane.getChildren().addAll(gridPane, maskerPane);
        getVBoxCont().getChildren().addAll(stackPane/*, vBox*/);
        if (startWithWaiting) {
            startWaiting();
        }
    }

    private void setSystemCallText() {
        if (ProgConfig.SYSTEM_SHUT_DOWN_CALL.getValueSafe().equals(PShutDown.getShutDownCommand())) {
            //dann ist es der normale Systemaufruf zum Herunterfahren
            chkShutDown.setText("Nach Warten und Programmende, Rechner herunterfahren");
            lblSystemCall.setText("");
            lblSystemCall.setVisible(false);
            lblSystemCall.setManaged(false);
        } else {
            chkShutDown.setText("Nach Warten und Programmende, Systembefehl aufrufen");
            lblSystemCall.setVisible(true);
            lblSystemCall.setManaged(true);
            lblSystemCall.setText(ProgConfig.SYSTEM_SHUT_DOWN_CALL.getValueSafe());
        }
    }

    private void addButton() {
        // Wenn es ein Tray gibt, dann ins Tray legen, ansonsten nur minimieren
        Button btnTray = new Button(ProgData.getInstance().progTray.getSystemTray() != null ? "Ausblenden" : "Minimieren");
        btnTray.setOnAction(a -> {
            if (ProgData.getInstance().progTray.getSystemTray() != null) {
                ProgData.getInstance().progTray.closeDialog();
            } else {
                ProgData.getInstance().primaryStage.setIconified(true);
                this.getStage().setIconified(true);
            }
        });
        addAnyButton(btnTray);
    }

    public void startWaiting() {
        maskerPane.setMaskerVisible(true,
                chkShutDown.isSelected() ? true : false,
                true);
        maskerPane.setMaskerText(chkShutDown.isSelected() ? chkShutDown.getText() : "");

        Thread th = new Thread(waitTask);
        th.setName("startWaiting");
        th.start();
    }

    private class WaitTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            while ((ProgData.getInstance().downloadList.countStartedAndRunningDownloads() > 0 ||
                    HttpDownload.downloadRunning > 0) &&
                    !isCancelled()) {

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

    public void closeMaskerPane() {
        if (waitTask.isRunning()) {
            waitTask.cancel();
        }
        maskerPane.switchOffMasker();
    }

    @Override
    public void close() {
        if (waitTask.isRunning()) {
            waitTask.cancel();
        }
        maskerPane.switchOffMasker();
        super.close();
    }
}