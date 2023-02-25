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

package de.p2tools.mtplayer.gui.mediadialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MediaDialogController extends PDialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final RadioButton rbMedien = new RadioButton("Mediensammlung");
    private final RadioButton rbAbos = new RadioButton("erledigte Abos");

    private final String searchStrOrg;
    private final StringProperty searchStrProp = new SimpleStringProperty();
    private final ProgData progData = ProgData.getInstance();
    private PaneMedia paneMedia;
    private PaneAbo paneAbo;
    final StackPane stackPane = new StackPane();

    public MediaDialogController(String searchStrOrg) {
        super(ProgData.getInstance().primaryStage, ProgConfig.MEDIA_DIALOG_SIZE, "Mediensammlung",
                true, false, DECO.BORDER);

        this.searchStrOrg = searchStrOrg.trim();
        searchStrProp.setValue(searchStrOrg);

        init(true);
    }

    @Override
    public void make() {
        initPanel();
        initAction();
        setPane();
        filter();
    }

    @Override
    public void close() {
        rbMedien.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA);
        paneAbo.close();
        paneMedia.close();

        progData.erledigteAbos.filterdListSetPredFalse();
        progData.mediaDataList.filterdListSetPredFalse();
        super.close();
    }

    private void initPanel() {
        try {
            final ToggleGroup group = new ToggleGroup();
            rbMedien.setToggleGroup(group);
            rbAbos.setToggleGroup(group);

            HBox hBox = new HBox(P2LibConst.DIST_BUTTON_BLOCK);
            hBox.getChildren().addAll(rbMedien, rbAbos);
            getVBoxCont().getChildren().add(hBox);

            // Stackpane
            paneMedia = new PaneMedia(getStage(), searchStrOrg, searchStrProp);
            paneMedia.make();

            paneAbo = new PaneAbo(getStage(), searchStrOrg, searchStrProp);
            paneAbo.make();

            stackPane.getChildren().addAll(paneMedia, paneAbo);
            VBox.setVgrow(stackPane, Priority.ALWAYS);
            getVBoxCont().getChildren().add(stackPane);

            Button btnHelp = PButton.helpButton(getStage(),
                    "Suche in der Mediensammlung", HelpText.SEARCH_MEDIA_DIALOG);
            addOkButton(btnOk);
            addHlpButton(btnHelp);
        } catch (final Exception ex) {
            PLog.errorLog(951203030, ex);
        }
    }

    private void initAction() {
        btnOk.setOnAction(a -> close());

        rbMedien.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA);
        rbMedien.setOnAction(a -> {
            setPane();
            filter();
        });
        rbAbos.setSelected(!ProgConfig.SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA.get());
        rbAbos.setOnAction(a -> {
            setPane();
            filter();
        });
    }

    private void setPane() {
        if (rbMedien.isSelected()) {
            paneMedia.toFront();
        } else {
            paneAbo.toFront();
        }
    }

    private void filter() {
        if (rbMedien.isSelected()) {
            paneMedia.filter(searchStrProp.getValueSafe());
        } else {
            paneAbo.filter(searchStrProp.getValueSafe());
        }
    }
}
