/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.mtplayer.controller;


import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

public class ProgInfoDialog extends P2DialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final Button btnGc = new Button("_Speicher aufräumen");
    private final Button btnDuration = new Button("_Laufzeiten ausgeben");
    private final ProgressBar progressBar = new ProgressBar();
    private final Label lblMemInfo = new Label("");
    private final TabPane tabPane = new TabPane();
    private final VBox toolBox = new VBox();
    private final TextArea textArea = new TextArea();

    private final Runtime rt = Runtime.getRuntime();
    private static final int MEGABYTE = 1000 * 1000;
    final String line = "<==/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\==>";


    public ProgInfoDialog(boolean showDialog) {
        super(P2LibConst.primaryStage, null, "Speicherverbrauch des Programms",
                false, false, false, DECO.NO_BORDER);
        addOkButton(btnOk);
        init(showDialog);
    }

    @Override
    protected void make() {
        btnOk.setOnAction(a -> close());
        btnGc.setOnAction(a -> System.gc());
        btnDuration.setOnAction(a -> {
            List<String> list = P2Duration.getCounter();
            list.add(0, line);
            list.add(line);
            list.add(" ");
            list.add(" ");
            P2Log.sysLog(list);

            textArea.setText("");
            StringBuilder text = new StringBuilder();
            for (String s : P2Duration.getCounter()) {
                text.append("\n").append(s);
            }
            textArea.setText(text.toString());
        });

        ScrollPane sc = new ScrollPane();
        sc.setFitToHeight(true);
        sc.setFitToWidth(true);
        sc.setContent(toolBox);
        Tab tabTools = new Tab("Tools");
        tabTools.setClosable(false);
        tabTools.setContent(sc);

        VBox vBoxMemory = new VBox(P2LibConst.SPACING_VBOX);
        vBoxMemory.setPadding(new Insets(10));
        VBox.setVgrow(textArea, Priority.ALWAYS);

        vBoxMemory.getChildren().addAll(getGrid(), textArea);
        Tab tabMemory = new Tab("Speicher");
        tabMemory.setClosable(false);
        tabMemory.setContent(vBoxMemory);


        tabPane.getTabs().addAll(tabMemory, tabTools);
        getVBoxCont().getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
    }

    public VBox getToolBox() {
        return toolBox;
    }

    private GridPane getGrid() {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        int row = 0;

        //Java
        Text text = new Text("Java Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(text, 0, row, 3, 1);

        gridPane.add(new Label("Version:"), 0, ++row);
        gridPane.add(new Label(System.getProperty("java.version")), 1, row, 2, 1);

        gridPane.add(new Label("Type:"), 0, ++row);
        String strVmType = System.getProperty("java.vm.name");
        strVmType += " (" + System.getProperty("java.vendor") + ")";
        gridPane.add(new Label(strVmType), 1, row, 2, 1);


        //Memory
        gridPane.add(new Label(" "), 0, ++row);
        text = new Text("Speicherverbrauch des Programms");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(text, 0, ++row, 3, 1);

        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMinWidth(10);
        lblMemInfo.setMaxWidth(Region.USE_PREF_SIZE);

        gridPane.add(btnGc, 0, ++row);
        gridPane.add(progressBar, 1, row);
        gridPane.add(lblMemInfo, 2, row);


        //Laufzeiten
        gridPane.add(new Label(" "), 0, ++row);
        text = new Text("Laufzeiten des Programms");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(text, 0, ++row, 3, 1);
        gridPane.add(btnDuration, 0, ++row);


        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        //Update every second...
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000), ae -> getMem()));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setDelay(Duration.seconds(1));
        timeline.play();
        return gridPane;
    }

    private void getMem() {
        final long maxMem;
        if (P2InfoFactory.getOs() == P2InfoFactory.OperatingSystemType.LINUX) {
            maxMem = rt.totalMemory();
        } else {
            maxMem = rt.maxMemory();
        }
        final long totalMemory = rt.totalMemory();
        final long freeMemory = rt.freeMemory();
        final long usedMem = totalMemory - freeMemory;

        final long used = usedMem / MEGABYTE;
        final long total = maxMem / MEGABYTE;

        double usedD = 1.0 * usedMem / totalMemory;
        progressBar.setProgress(usedD);

        final String info = used + " von " + total + "MB";
        lblMemInfo.setText(info);
    }

}
