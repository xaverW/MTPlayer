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


package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.Daten;
import de.p2tools.p2Lib.tools.log.SysMsg;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MsgLogController extends AnchorPane {

    TextArea textArea = new TextArea();
    VBox vBoxCont = new VBox();
    CheckBox cbxScroll = new CheckBox("Autoscroll");
    CheckBox cbxWrap = new CheckBox("Zeilen umbrechen");
    private final int logart;
    private final ObservableList<String> textList;

    public static final int LOG_SYSTEM_MSG = 1;
    public static final int LOG_PLAYER_MSG = 2;

    public MsgLogController(int logart) {
        this.logart = logart;
        if (logart == LOG_SYSTEM_MSG) {
            textList = SysMsg.textSystem;
        } else {
            textList = Daten.getInstance().playerMsg.textProgramm;
        }

        vBoxCont.setSpacing(10);
        this.getChildren().add(vBoxCont);
        AnchorPane.setTopAnchor(vBoxCont, 0.0);
        AnchorPane.setRightAnchor(vBoxCont, 0.0);
        AnchorPane.setBottomAnchor(vBoxCont, 0.0);
        AnchorPane.setLeftAnchor(vBoxCont, 0.0);

        Label lblTitle;
        if (logart == LOG_SYSTEM_MSG) {
            lblTitle = new Label("Log vom Programm");
        } else {
            lblTitle = new Label("Log von externen Programmen");
        }
        HBox hBoxTitle = new HBox(lblTitle);

        hBoxTitle.setAlignment(Pos.CENTER);
        vBoxCont.getChildren().add(hBoxTitle);

        ScrollPane scrollPaneLog = new ScrollPane();
        scrollPaneLog.setFitToHeight(true);
        scrollPaneLog.setFitToWidth(true);
        scrollPaneLog.setContent(textArea);
        VBox.setVgrow(scrollPaneLog, Priority.ALWAYS);

        vBoxCont.getChildren().add(scrollPaneLog);

        setTextToArea();
        textList.addListener((ListChangeListener.Change<? extends String> lcl) ->
                Platform.runLater(() -> {
                    setTextToArea();
                }));

        addBottom();

    }

    private void addBottom() {
        textArea.wrapTextProperty().bind(cbxWrap.selectedProperty());
        cbxScroll.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                scrollTotop();
            }
        });
        Button btnClear = new Button("löschen");
        btnClear.setOnAction(a -> clearLog());
        HBox hBoxBtn = new HBox();
        hBoxBtn.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(hBoxBtn, Priority.ALWAYS);
        hBoxBtn.getChildren().add(btnClear);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));

        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(cbxScroll, cbxWrap, hBoxBtn);
        vBoxCont.getChildren().add(hBox);
    }

    private void scrollTotop() {
        textArea.setScrollTop(Double.MAX_VALUE);
    }

    private void setTextToArea() {
        if (cbxScroll.isSelected()) {
            if (logart == LOG_SYSTEM_MSG) {
                textArea.setText(SysMsg.getText());
            } else {
                textArea.setText(Daten.getInstance().playerMsg.getText());
            }

            textArea.selectPositionCaret(textArea.getLength());
            textArea.deselect();
        } else {
            double scrollPosition = textArea.getScrollTop();

            if (logart == LOG_SYSTEM_MSG) {
                textArea.setText(SysMsg.getText());
            } else {
                textArea.setText(Daten.getInstance().playerMsg.getText());
            }

            textArea.setScrollTop(scrollPosition);
        }
    }

    private void clearLog() {
        if (logart == LOG_SYSTEM_MSG) {
            SysMsg.clearText();
        } else {
            Daten.getInstance().playerMsg.clearText();
        }
    }
}
