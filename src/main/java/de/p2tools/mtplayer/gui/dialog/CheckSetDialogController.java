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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CheckSetDialogController extends P2DialogExtra {

    private final VBox vBoxCont;
    private final CheckBox chkAlways = new CheckBox("Beim Programmstart prüfen");
    private final Button btnOk = new Button("OK");
    private final String text;

    public CheckSetDialogController(String text) {
        super(ProgData.getInstance().primaryStage, ProgConfig.CHECK_SET_DIALOG_SIZE, "Download-Einstellungen prüfen",
                true, false, DECO.NO_BORDER);

        this.text = text;
        vBoxCont = getVBoxCont();
        init(true);
    }


    @Override
    public void make() {
        vBoxCont.setPadding(new Insets(P2LibConst.PADDING));
        vBoxCont.setSpacing(P2LibConst.PADDING_VBOX);

        ScrollPane scroll = new ScrollPane();
        TextArea ta = new TextArea(text);
        ta.setMinHeight(500);
        ta.setEditable(false);
        scroll.setContent(ta);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        vBoxCont.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getHBoxOverButtons().setAlignment(Pos.CENTER_RIGHT);
        getHBoxOverButtons().getChildren().addAll(chkAlways);

        chkAlways.setSelected(ProgConfig.CHECK_SET_PROGRAM_START.get());
        btnOk.setOnAction(event -> {
            ProgConfig.CHECK_SET_PROGRAM_START.setValue(chkAlways.isSelected());
            quit();
        });

        Button btnHelp = P2Button.helpButton(getStage(),
                "Download-Einstellungen prüfen", HelpText.CHECK_SET_PROGRAM_DIALOG);
        addHlpButton(btnHelp);
        addOkButton(btnOk);
    }

    private void quit() {
        close();
    }
}
