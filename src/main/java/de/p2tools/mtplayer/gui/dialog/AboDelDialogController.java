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
import de.p2tools.mtplayer.controller.config.ProgConfigAskBeforeDelete;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class AboDelDialogController extends P2DialogExtra {

    private final VBox vBoxCont;
    private final Button btnDelDl = new Button("Abo löschen");
    private final Button btnCancel = new Button("Abbrechen");
    private final CheckBox chkAlways = new CheckBox("Nicht mehr fragen");
    private final ObservableList<AboData> foundDownloadList;
    private STATE state;

    public AboDelDialogController(ObservableList<AboData> foundAboList) {
        super(ProgData.getInstance().primaryStage, ProgConfig.ABO_DEL_DIALOG_SIZE, "Abo löschen",
                true, true, true, DECO.BORDER_SMALL);
        this.foundDownloadList = foundAboList;

        vBoxCont = getVBoxCont();
        init(true);
    }

    public STATE getState() {
        return state;
    }

    @Override
    public void make() {
        getHBoxTitle().getChildren().add(new Label("Abo löschen"));
        vBoxCont.setPadding(new Insets(P2LibConst.PADDING));
        vBoxCont.setSpacing(P2LibConst.PADDING_VBOX);


        TableView<AboData> table = new TableView<>();
        final TableColumn<AboData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.prefWidthProperty().bind(table.widthProperty().multiply(30.0 / 100));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("special-column-style");

        final TableColumn<AboData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.prefWidthProperty().bind(table.widthProperty().multiply(30.0 / 100));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("special-column-style");

        final TableColumn<AboData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.prefWidthProperty().bind(table.widthProperty().multiply(30.0 / 100));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("special-column-style");

        table.getColumns().addAll(nameColumn, themeColumn, titleColumn);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setItems(foundDownloadList);
        vBoxCont.getChildren().addAll(table);


        getHBoxOverButtons().setAlignment(Pos.CENTER_RIGHT);
        getHBoxOverButtons().getChildren().addAll(chkAlways);

        btnDelDl.setTooltip(new Tooltip("Das Abo wird gelöscht."));
        btnCancel.setTooltip(new Tooltip("Das Abo wird nicht gelöscht."));

        btnDelDl.setOnAction(event -> {
            state = STATE.STATE_OK;
            if (chkAlways.isSelected()) {
                // dann merken wir uns das
                ProgConfig.ABO_ONLY_STOP.setValue(ProgConfigAskBeforeDelete.ABO_DELETE__DELETE);
            }
            quit();
        });
        btnCancel.setOnAction(a -> {
            // nix
            state = STATE.STATE_CANCEL;
            quit();
        });
        Button btnHelp = PIconFactory.getHelpButton(getStage(),
                "Abo löschen", HelpText.ABO_DELETE_DIALOG);
        addHlpButton(btnHelp);
        addOkCancelButtons(btnDelDl, btnCancel);
    }

    private void quit() {
        close();
    }
}
