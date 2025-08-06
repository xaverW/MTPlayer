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

package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.controller.data.abo.AboSearchDownloadsFactory;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AboAddDialogController extends P2DialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final Button btnApply = new Button("_Anwenden");
    private final Button btnCancel = new Button("_Abbrechen");

    private final ProgData progData;
    private final AddAboDto addAboDto;

    public AboAddDialogController(ProgData progData, AboData abo) {
        // hier wird ein neues Abo angelegt!
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo anlegen", false, false, DECO.NO_BORDER, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(progData, true, abo);
        init(true);
    }

    public AboAddDialogController(ProgData progData, List<AboData> aboList) {
        // hier werden bestehende Abos geändert
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo ändern", false, false, DECO.NO_BORDER, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(progData, false, aboList);
        init(true);
    }

    public AboAddDialogController(ProgData progData, FilmFilter filmFilter, AboData abo) {
        // hier wird ein bestehendes Abo an den Filter angepasst
        super(progData.primaryStage, ProgConfig.ABO_DIALOG_EDIT_SIZE,
                "Abo anlegen", false, false, DECO.NO_BORDER, true);

        this.progData = progData;
        this.addAboDto = new AddAboDto(progData, false, abo);

        final String channel = filmFilter.isChannelVis() ? filmFilter.getChannel() : "";
        final String theme = filmFilter.isThemeVis() ? filmFilter.getResTheme() : "";
        final boolean themeExact = filmFilter.isThemeIsExact();
        final String title = filmFilter.isTitleVis() ? filmFilter.getTitle() : "";
        final String themeTitle = filmFilter.isThemeTitleVis() ? filmFilter.getThemeTitle() : "";
        final String somewhere = filmFilter.isSomewhereVis() ? filmFilter.getSomewhere() : "";
        final int timeRange = filmFilter.isTimeRangeVis() ? filmFilter.getTimeRange() : FilterCheck.FILTER_ALL_OR_MIN;
        final int minDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMinDur() : FilterCheck.FILTER_ALL_OR_MIN;
        final int maxDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMaxDur() : FilterCheck.FILTER_DURATION_MAX_MINUTE;

        addAboDto.getAct().abo.setChannel(channel);
        addAboDto.getAct().abo.setTheme(theme);
        addAboDto.getAct().abo.setThemeExact(themeExact);
        addAboDto.getAct().abo.setTitle(title);
        addAboDto.getAct().abo.setThemeTitle(themeTitle);
        addAboDto.getAct().abo.setSomewhere(somewhere);
        addAboDto.getAct().abo.setTimeRange(timeRange);
        addAboDto.getAct().abo.setMinDurationMinute(minDuration);
        addAboDto.getAct().abo.setMaxDurationMinute(maxDuration);

        init(true);
    }

    @Override
    public void close() {
        btnOk.disableProperty().unbind();
        btnApply.disableProperty().unbind();
        super.close();
    }

    @Override
    public void make() {
        if (progData.setDataList.getSetDataListAbo().isEmpty()) {
            // Satz mit x, war wohl nix
            Platform.runLater(() -> {
                super.close();
                new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.ABO);
            });
            return;
        }
        if (addAboDto.aboList.isEmpty()) {
            // Satz mit x, war wohl nix
            Platform.runLater(super::close);
            return;
        }

        initGui();
        initButton();
        addAboDto.updateAct();
    }

    private void initGui() {
        boolean size = addAboDto.addAboData.length > 1;
        // Top
        VBox vBoxCont = getVBoxCont();

        if (size) {
            // wenns nur einen Download gibt, macht dann keinen Sinn
            final HBox hBoxTop = new HBox();
            hBoxTop.getStyleClass().add("downloadDialog");
            hBoxTop.setSpacing(20);
            hBoxTop.setAlignment(Pos.CENTER);
            hBoxTop.setPadding(new Insets(5));
            hBoxTop.getChildren().addAll(addAboDto.btnPrev, addAboDto.lblSum, addAboDto.btnNext);
            vBoxCont.getChildren().add(hBoxTop);
        }

        Font font = Font.font(null, FontWeight.BOLD, -1);
        addAboDto.btnAll.setFont(font);
        addAboDto.btnAll.setWrapText(true);
        addAboDto.btnAll.setMinHeight(Region.USE_PREF_SIZE);

        HBox hBoxBtn = new HBox(P2LibConst.SPACING_HBOX);
        hBoxBtn.setAlignment(Pos.CENTER_RIGHT);
        hBoxBtn.getChildren().addAll(DownloadAddDialogFactory.getText(AboFieldNames.ABO_NO + ":"), addAboDto.lblAboNo,
                P2GuiTools.getHDistance(20),
                DownloadAddDialogFactory.getText(AboFieldNames.ABO_HIT + ":"), addAboDto.lblHit,
                P2GuiTools.getHBoxGrower(),
                addAboDto.btnAll);
        vBoxCont.getChildren().add(hBoxBtn);

        vBoxCont.getChildren().add(getTabPane());
        vBoxCont.getChildren().add(ProgData.busy.getBusyHbox(Busy.BUSY_SRC.ABO_DIALOG));

        // Letztes Abo, Angelegt
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(DownloadAddDialogFactory.getText(AboFieldNames.ABO_DATE_LAST_ABO + ":"), addAboDto.lblLastAbo,
                P2GuiTools.getHBoxGrower(),
                DownloadAddDialogFactory.getText(AboFieldNames.ABO_GEN_DATE + ":"), addAboDto.lblGenDate);
        vBoxCont.getChildren().add(hBox);

        addOkCancelApplyButtons(btnOk, btnCancel, btnApply);
        addHlpButton(P2Button.helpButton(getStage(), "Abo", HelpText.ABO_SEARCH));
    }

    private TabPane getTabPane() {
        AboAddDialogGuiAbo aboAddDialogGuiAbo = new AboAddDialogGuiAbo(progData, getStage(), addAboDto);
        AboAddDialogGuiSearch aboAddDialogGuiSearch = new AboAddDialogGuiSearch(progData, getStage(), addAboDto);
        AboAddDialogGuiPath aboAddDialogGuiPath = new AboAddDialogGuiPath(progData, getStage(), addAboDto);

        Tab tabAbo = new Tab("Abo");
        tabAbo.setClosable(false);
        tabAbo.setContent(aboAddDialogGuiAbo);

        Tab tabSearch = new Tab("Suche");
        tabSearch.setClosable(false);
        tabSearch.setContent(aboAddDialogGuiSearch);

        Tab tabPath = new Tab("Pfad");
        tabPath.setClosable(false);
        tabPath.setContent(aboAddDialogGuiPath);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tabAbo, tabSearch, tabPath);
        tabPane.tabMinWidthProperty().bind(tabPane.widthProperty().divide(4));
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // ScrollPane wird sonst nicht angezeigt??
        aboAddDialogGuiPath.heightProperty().addListener((u, o, n) -> tabPane.setMinHeight((double) n));
        return tabPane;
    }

    private void initButton() {
        setMaskerPane();
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            setMaskerPane();
        });
        addAboDto.btnPrev.setOnAction(event -> {
            addAboDto.actAboIsShown.setValue(addAboDto.actAboIsShown.getValue() - 1);
            addAboDto.updateAct();
        });
        addAboDto.btnNext.setOnAction(event -> {
            addAboDto.actAboIsShown.setValue(addAboDto.actAboIsShown.getValue() + 1);
            addAboDto.updateAct();
        });

        btnOk.disableProperty().bind(AboSearchDownloadsFactory.alreadyRunning);
        btnOk.setOnAction(a -> {
            if (check()) {
                apply(true);
                close();
            }
        });
        btnApply.disableProperty().bind(AboSearchDownloadsFactory.alreadyRunning);
        btnApply.setOnAction(a -> {
            if (check()) {
                apply(false);
            }
        });

        btnCancel.setOnAction(a -> {
            close();
        });
        btnOk.requestFocus();
    }

    private void setMaskerPane() {
        if (progData.maskerPane.isVisible()) {
            this.setMaskerVisible(true);
        } else {
            this.setMaskerVisible(false);
        }
    }

    private boolean check() {
        AboData abo = null;
        for (AddAboData addAboData : addAboDto.addAboData) {
            if ((abo = AboFactory.aboExistsAlready(addAboData.abo, true)) != null) {
                break;
            }
        }
        if (abo != null) {
            // dann gibts das Abo schon
            if (P2Alert.showAlert_yes_no(getStage(), "Fehler", "Abo anlegen",
                    "Ein Abo mit den Einstellungen existiert bereits, es " +
                            "findet die gleichen (oder mehr) Filme:" +
                            "\n\n" +
                            ("Abo-Nr: " + abo.getNo() + "\n") +
                            ("Quelle: " + AboFactory.getSourceText(abo) + "\n") +
                            (abo.getChannel().isEmpty() ? "" : "Sender: " + abo.getChannel() + "\n") +
                            (abo.getTheme().isEmpty() ? "" : "Thema: " + abo.getTheme() + "\n") +
                            (abo.getThemeTitle().isEmpty() ? "" : "Thema-Titel: " + abo.getThemeTitle() + "\n") +
                            (abo.getTitle().isEmpty() ? "" : "Titel: " + abo.getTitle() + "\n") +
                            (abo.getSomewhere().isEmpty() ? "" : "Irgendwo: " + abo.getSomewhere()) +
                            "\n\n" +
                            (addAboDto.isNewAbo ? "Trotzdem anlegen?" : "Trotzdem aktualisieren?")).equals(P2Alert.BUTTON.NO)) {
                return false;
            }
        }

        boolean empty = false;
        for (AddAboData addAboData : addAboDto.addAboData) {
            abo = addAboData.abo;
            if (abo.isEmpty()) {
                empty = true;
                break;
            }
        }
        if (empty) {
            // dann ists leer
            P2Alert.showErrorAlert(getStage(), "Fehler", "Abo anlegen",
                    "Abo-Nr: " + abo.getNo() + "\n" +
                            "Das Abo ist \"leer\", es enthält keine Filter.");
            return false;
        }

        return true;
    }

    private void apply(boolean fromOk) {
        if (addAboDto.isNewAbo) {
            // dann soll ein neues angelegt werden
            addNewAbos();
        } else {
            // dann bestehende anpassen
            updateAboList();
        }

        // als Vorgabe merken
        ProgConfig.ABO_MINUTE_MIN_SIZE.setValue(addAboDto.getAct().abo.getMinDurationMinute());
        ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(addAboDto.getAct().abo.getMaxDurationMinute());

        // da nicht modal!!
        AboSearchDownloadsFactory.searchFromDialog(fromOk);

        // und jetzt noch die Einstellungen speichern
        ProgSave.saveAll(false);
    }

    private void addNewAbos() {
        // dann erst mal die ORG anlegen und diese dann einpflegen
        for (AddAboData addAboData : addAboDto.addAboData) {
            addAboData.aboOrg.copyToMe(addAboData.abo);
            progData.aboList.addAbo(addAboData.aboOrg);
            addAboData.abo.setNo(addAboData.aboOrg.getNo()); // die wird beim Einfügen neu gesetzt
        }

        // ab jetzt ists ein Update!!
        addAboDto.isNewAbo = false;
    }

    private void updateAboList() {
        for (AddAboData addAboData : addAboDto.addAboData) {
            addAboData.aboOrg.copyToMe(addAboData.abo);
        }
    }
}
