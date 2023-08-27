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
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.dialogs.ProgInfoDialog;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.pmask.PMaskerPane;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmFactory;
import de.p2tools.p2lib.mtfilter.FilmFilterCheck;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.tools.duration.PDuration;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.function.Predicate;

public class MTPTester {
    private final ProgInfoDialog progInfoDialog;
    private final ProgData progData;
    private final TextArea textArea = new TextArea();
    private String text = "";
    private final PMaskerPane maskerPane = new PMaskerPane();
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
            gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

            maskerPane.switchOffMasker();
            maskerPane.setButtonText("Abbrechen");
            maskerPane.getButton().setOnAction(a -> close());

            final StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(gridPane, maskerPane);
            progInfoDialog.getVBoxCont().getChildren().addAll(stackPane);

            final Text text = new Text("Debugtools");
            text.setFont(Font.font(null, FontWeight.BOLD, 15));

//            Button btnMarkFilm = new Button("Diakrit");
//            btnMarkFilm.setMaxWidth(Double.MAX_VALUE);
//            btnMarkFilm.setOnAction(a -> check());
//
//            Button btnMarkBlack = new Button("MarkBlack");
//            btnMarkBlack.setMaxWidth(Double.MAX_VALUE);
//            btnMarkBlack.setOnAction(a -> new Thread(() ->
//                    BlacklistFilterFactory.markFilmBlack(true)).start());


            int row = 0;
//            gridPane.add(text, 0, row, 2, 1);
//            gridPane.add(btnMarkFilm, 0, ++row);
//            gridPane.add(btnMarkBlack, 0, ++row);
//
//            gridPane.add(textArea, 0, ++row, 2, 1);

//            Button btnShutDown = new Button("ShutDownDialog");
//            btnShutDown.setOnAction(a -> new QuitDialogController(false));
//            gridPane.add(new Label(), 0, ++row);
//            gridPane.add(btnShutDown, 0, ++row);
//
//            Button btnCloseDialog = new Button("Alle Dialoge schließen");
//            btnCloseDialog.setOnAction(a -> PDialogExtra.closeAllDialog());
//            gridPane.add(new Label(), 0, ++row);
//            gridPane.add(btnCloseDialog, 0, ++row);
//
//            gridPane.add(new Label(), 0, ++row);
//            PToggleSwitch tglDownloading = new PToggleSwitch("ProgData.FILMLIST_IS_DOWNLOADING");
//            tglDownloading.selectedProperty().bindBidirectional(ProgData.FILMLIST_IS_DOWNLOADING);
//            gridPane.add(tglDownloading, 0, ++row);
//
//            gridPane.add(new Label(), 0, ++row);
//            Button btnPropose = new Button("Film vorschlagen");
//            gridPane.add(btnPropose, 0, ++row);
//            btnPropose.setOnAction(a -> {
//                new ProposeDialogController(progData, ProgConfig.PROPOSE_DIALOG_CONTROLLER_SIZE);
//            });

//            gridPane.add(new Label(), 0, ++row);
//            Button btnMTNotify1 = new Button("p2Notification");
//            gridPane.add(btnMTNotify1, 0, ++row);
//            btnMTNotify1.setOnAction(a -> {
//                P2Notification.addNotification("Download beendet",
//                        "text fjksdladf \n jfksalödfj \n jfksdalöjf \n jfksdalöfj ",
//                        false);
//            });
//
//
//            gridPane.add(new Label(), 0, ++row);
//            Button btnMTNotify2 = new Button("p2Notification");
//            gridPane.add(btnMTNotify2, 0, ++row);
//            btnMTNotify2.setOnAction(a -> {
//                P2Notification.addNotification("Download beendet",
//                        "text fjksdladf",
//                        true);
//            });
//
//            gridPane.add(new Label(), 0, ++row);
//            Button btnNotify = new Button("controlFx");
//            gridPane.add(btnNotify, 0, ++row);
//            btnNotify.setOnAction(a -> {
//                Notifications.create()
//                        .title("Title Text")
//                        .text("Hello World 0!")
//                        .threshold(0, Notifications.create().title("Collapsed Notification"))
//                        .darkStyle()
//                        .showWarning();
//
//            });
//
//            gridPane.add(new Label(), 0, ++row);
//            Button btnTryNotify = new Button("AWS Tray");
//            gridPane.add(btnTryNotify, 0, ++row);
//            btnTryNotify.setOnAction(a -> {
//                tray();
//            });


            final CheckBox chkFilter = new CheckBox("Filtern");
            final CheckBox chkSelect = new CheckBox("Select");
            final Button btnTable = new Button("Table");

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
        }
    }

    private void tray() {
        try {
            //Obtain only one instance of the SystemTray object
            SystemTray tray = SystemTray.getSystemTray();

            // If you want to create an icon in the system tray to preview
            Image image = Toolkit.getDefaultToolkit().createImage("some-icon.png");
            //Alternative (if the icon is on the classpath):
            //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

            TrayIcon trayIcon = new TrayIcon(image, "Java AWT Tray Demo");
            //Let the system resize the image if needed
            trayIcon.setImageAutoSize(true);
            //Set tooltip text for the tray icon
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);

            // Display info notification:
            trayIcon.displayMessage("Hello, World", "Java Notification Demo", TrayIcon.MessageType.INFO);
            // Error:
            // trayIcon.displayMessage("Hello, World", "Java Notification Demo", MessageType.ERROR);
            // Warning:
            // trayIcon.displayMessage("Hello, World", "Java Notification Demo", MessageType.WARNING);
        } catch (Exception ex) {
            System.err.print(ex);
        }
    }

    String test = "äöü ń ǹ ň ñ ṅ ņ ṇ ṋ    ( ç/č/c => c; a/á/à/â/ă/ȁ/å/ā/ã => a aber ä => ä )";

    private void check() {
        PDuration.counterStart("MTPTester diakritische Zeichen");
        ProgConfig.SYSTEM_REMOVE_DIACRITICS.setValue(ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue());
        if (ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue()) {
            FilmFactory.flattenDiacritic(progData.filmList);
        }
        PDuration.counterStop("MTPTester diakritische Zeichen");
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
