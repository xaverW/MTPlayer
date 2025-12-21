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

package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.tools.log.P2Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class DownloadErrorDialogController extends P2DialogExtra {

    private final HBox hBoxTitle;
    private final VBox vBoxCont;
    private final Label lblHeader = new Label("Downloadfehler");
    private final Button btnOk = new Button("_Ok");
    private final Button btnErrorStream = new Button("Programmausgabe");
    private final Button btnLogFile = new Button("Log öffnen");
    private final CheckBox chkShowNot = new CheckBox("Nicht mehr anzeigen");
    private final CheckBox chkTime = new CheckBox("Automatisch ausblenden");

    private final Label lblFilmTitle = new Label("ARD: Tatort, ..");
    private final TextArea txtUrl = new TextArea();
    private final TextArea txtCont = new TextArea();
    private final Label lblTime = new Label("");
    private final FontIcon fontIcon = PIconFactory.PICON.ATTENTION.getFontIcon();
    private final GridPane gridPane = new GridPane();

    private final Timeline timeline = new Timeline();
    private Integer timeSeconds = ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getValue();

    private final DownloadData download;

    public DownloadErrorDialogController(DownloadData download) {
        super(ProgData.getInstance().primaryStage, ProgConfig.DOWNLOAD__ERROR_DIALOG_SIZE,
                "Fehler", false, true, true);

        this.download = download;

        hBoxTitle = getHBoxTitle();
        vBoxCont = getVBoxCont();

        init(true);
    }

    @Override
    public void make() {
        initCont();

        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(download.getTitle());

        txtUrl.setEditable(false);
        txtUrl.setWrapText(true);
        String txt = "Url: \n" + download.getUrl() + "\n" +
                "Datei: \n" + download.getDestPathFile();
        txtUrl.setText(txt);

        txtCont.setEditable(false);
        txtCont.setWrapText(true);
        txtCont.setText("Fehler:" + "\n" + download.getStartDownloadDto().getErrorMsg());

        btnOk.setOnAction(event -> {
            stopCounter();
            quit();
        });
        btnErrorStream.setOnAction(a -> new DownloadErrorStreamDialogController(download.getStartDownloadDto().getErrorStream()));
        btnLogFile.setOnAction(a -> P2Logger.openLogFile());

        btnErrorStream.setVisible(!download.getStartDownloadDto().getErrStreamList().isEmpty());
        btnErrorStream.setManaged(btnErrorStream.isVisible());

//        fontIcon.setImage( PIconFactory.PICON.ATTENTION.getFontIcon());
        chkTime.setSelected(ProgConfig.DOWNLOAD_DIALOG_ERROR_TIME.get());
        chkTime.setOnAction(a -> {
            ProgConfig.DOWNLOAD_DIALOG_ERROR_TIME.setValue(chkTime.isSelected());
            if (chkTime.isSelected()) {
                startCounter();
            } else {
                stopCounter();
            }
        });
        chkShowNot.setOnAction(a -> ProgConfig.DOWNLOAD_DIALOG_ERROR_SHOW.setValue(!chkShowNot.isSelected()));

        //start the countdown...
        lblTime.setText("");
        lblTime.visibleProperty().bind(ProgConfig.DOWNLOAD_DIALOG_ERROR_TIME);
        initCounter();
    }

    private void initCont() {
        hBoxTitle.getChildren().add(lblHeader);

        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        int row = 0;
        gridPane.add(new Label("Film:"), 0, row);
        gridPane.add(lblFilmTitle, 1, row);

        gridPane.add(txtUrl, 0, ++row, 2, 1);
        gridPane.add(txtCont, 0, ++row, 2, 1);
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(fontIcon, gridPane);
        HBox.setHgrow(gridPane, Priority.ALWAYS);

        vBoxCont.setPadding(new Insets(5));
        vBoxCont.getChildren().add(hBox);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        addOkButton(btnOk);
        Button btnHelp = PIconFactory.getHelpButton(getStage(), "Download Fehler", HelpText.CONFIG_DOWNOAD_ERROR);
        addHlpButton(btnHelp);

        getHboxLeft().getChildren().addAll(btnErrorStream, btnLogFile, P2GuiTools.getHBoxGrower());
        getHBoxOverButtons().setAlignment(Pos.CENTER_RIGHT);
        getHBoxOverButtons().getChildren().addAll(chkTime, lblTime, P2GuiTools.getHBoxGrower(), chkShowNot);
    }

    private class CountdownAction implements EventHandler {
        @Override
        public void handle(Event event) {
            timeSeconds--;
            if (timeSeconds > 0) {
                lblTime.setText(timeSeconds + "");
            } else {
                stopCounter();
                quit();
            }
        }
    }

    private void initCounter() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new CountdownAction()));
        if (ProgConfig.DOWNLOAD_DIALOG_ERROR_TIME.get()) {
            startCounter();
        }
    }

    private void startCounter() {
        timeSeconds = ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getValue();
        timeline.playFromStart();
    }

    private void stopCounter() {
        timeline.stop();
    }

    private void quit() {
        close();
    }
}
