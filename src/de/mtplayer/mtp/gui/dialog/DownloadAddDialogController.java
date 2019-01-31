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

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mLib.tools.FileNameUtils;
import de.mtplayer.mLib.tools.SizeTools;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import de.mtplayer.mtp.controller.data.download.DownloadTools;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.tools.PStringUtils;
import de.p2tools.p2Lib.tools.PSystemUtils;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DownloadAddDialogController extends PDialogExtra {

    private VBox vBoxCont;
    private HBox hBoxOk;

    private final VBox vBoxAllDownloads = new VBox();
    private final HBox hBoxTop = new HBox();
    private final HBox hBoxAll = new HBox();
    private final HBox hBoxSize = new HBox();

    private final Button btnPrev = new Button("<");
    private final Button btnNext = new Button(">");
    private final Label lblSum = new Label("");

    private final CheckBox chkAll = new CheckBox("Änderungen auf alle Filme anwenden");
    private final Label lblSet = new Label("Set:");
    private final ComboBox<String> cbSet = new ComboBox<>();
    private final ComboBox<String> cbPath = new ComboBox<>();
    private final Button btnDest = new Button("Pfad");
    private final Button btnPropose = new Button("Vorschlag");
    private final Button btnOk = new Button("Ok");
    private final Button btnCancel = new Button("Abbrechen");
    private final TextField txtName = new TextField();
    private final CheckBox cbxStart = new CheckBox("Download sofort starten");
    private final CheckBox cbxInfo = new CheckBox("Infodatei anlegen: \"Filmname.txt\"");
    private final CheckBox cbxSubtitle = new CheckBox("Untertitel speichern: \"Filmname.xxx\"");

    private final RadioButton rbHd = new RadioButton("HD");
    private final RadioButton rbHigh = new RadioButton("Hoch");
    private final RadioButton rbSmall = new RadioButton("Klein");
    private final ToggleGroup group = new ToggleGroup();

    private final Label lblFree = new Label("4M noch frei");
    private final Label lblFilm = new Label("Film:");
    private final Label lblFilmTitle = new Label("ARD: Tatort, ..");
    private final GridPane gridPane = new GridPane();

    private final String textHd = "HD";
    private final String textHeight = "hohe Auflösung";
    private final String textLow = "niedrige Auflösung";

    private final ProgData progData;
    final private SetList setList;
    private SetData setData;
    private String filterResolution;
    final String[] storedPath = ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");

    private static final String SEPARATOR = File.separator;
    private static final String FORMATTER_ddMMyyyy_str = "yyyyMMdd";
    private static final FastDateFormat FORMATTER_ddMMyyyy = FastDateFormat.getInstance(FORMATTER_ddMMyyyy_str);

    private boolean ok = false;
    private int actFilmIsShown = 0;
    private ArrayList<Film> filmsToDownloadList;
    private DownInfo downInfo[];

    private class DownInfo {
        private String fileSize_HD = "";
        private String fileSize_high = "";
        private String fileSize_small = "";

        private String resolution = Film.RESOLUTION_HD;
        private boolean info, subtitle, subDisable = false;

        private Film film;
        private Download download;
        private SetData psetData;

        private String path = "";
        private String name = "";


        private void setResolution(String resolution) {
            if (chkAll.isSelected()) {

                Arrays.stream(downInfo).forEach(d -> {
                    if (resolution.equals(Film.RESOLUTION_HD) && d.film.isHd()) {
                        d.resolution = Film.RESOLUTION_HD;

                    } else if (resolution.equals(Film.RESOLUTION_SMALL) && film.isSmall()) {
                        d.resolution = Film.RESOLUTION_SMALL;

                    } else {
                        d.resolution = Film.RESOLUTION_NORMAL;
                    }
                });

            } else {
                this.resolution = resolution;
            }
        }

        private void setInfo(boolean info) {
            if (chkAll.isSelected()) {
                Arrays.stream(downInfo).forEach(d -> d.info = info);
            } else {
                this.info = info;
            }
        }

        private void setSubtitle(boolean subtitle) {
            if (chkAll.isSelected()) {
                Arrays.stream(downInfo).forEach(d -> {
                    if (!d.subDisable) {
                        d.subtitle = subtitle;
                    }
                });
            } else {
                this.subtitle = subtitle;
            }
        }

        private void setName(String name) {
            this.name = name;
        }

        private void setPath(String path) {
            if (chkAll.isSelected()) {
                Arrays.stream(downInfo).forEach(d -> d.path = path);
            } else {
                this.path = path;
            }
        }

    }

    public DownloadAddDialogController(ProgData progData, ArrayList<Film> filmsToDownloadList, SetData setData, String filterResolution) {
        super(filmsToDownloadList.size() > 1 ? ProgConfig.DOWNLOAD_DIALOG_ADD_MORE_SIZE.getStringProperty() :
                        ProgConfig.DOWNLOAD_DIALOG_ADD_SIZE.getStringProperty(),
                "Download anlegen", true);

        this.progData = progData;
        this.filmsToDownloadList = filmsToDownloadList;
        this.setData = setData;
        this.filterResolution = filterResolution;
        this.setList = progData.setList.getListSave();

        vBoxCont = getVboxCont();
        hBoxOk = getHboxOk();

        init(getvBoxDialog(), true);
    }

    @Override
    public void make() {
        initCont();

        if (progData.setList.getListSave().isEmpty()) {
            // Satz mit x, war wohl nix
            ok = false;
            quit();
            return;
        }
        if (setData == null) {
            setData = progData.setList.getListSave().get(0);
        }

        if (filmsToDownloadList.size() == 0) {
            // Satz mit x, war wohl nix
            ok = false;
            quit();
            return;

        } else if (filmsToDownloadList.size() == 1) {
            hBoxTop.setVisible(false);
            hBoxTop.setManaged(false);
            hBoxAll.setVisible(false);
            hBoxAll.setManaged(false);
        }

        if (setList.size() == 1) {
            // macht dann keinen Sinn
            lblSet.setVisible(false);
            lblSet.setManaged(false);
            cbSet.setVisible(false);
            cbSet.setManaged(false);
        } else {
            cbSet.getItems().addAll(setList.getPsetNameList());
            cbSet.getSelectionModel().select(setData.getName());
            cbSet.setOnAction(a -> makePsetChange());
        }

        initArrays();
        initButton();
        initPathAndName();
        initResolutionButton();
        initCheckBox();

        changeFilmNr();
    }

    private void initCont() {
        vBoxAllDownloads.setStyle("-fx-background-color: gainsboro;");
        hBoxSize.setStyle("-fx-background-color: gainsboro;");
        lblFilm.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setStyle("-fx-font-weight: bold;");

        // Top
        hBoxTop.setSpacing(20);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setPadding(new Insets(10));
        hBoxTop.getChildren().addAll(btnPrev, lblSum, btnNext);

        hBoxAll.setAlignment(Pos.CENTER);
        hBoxAll.setPadding(new Insets(10));
        hBoxAll.getChildren().add(chkAll);
        vBoxAllDownloads.getChildren().addAll(hBoxTop, hBoxAll);


        // Gridpane
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        int row = 0;
        gridPane.add(lblFilm, 0, row);
        gridPane.add(lblFilmTitle, 1, row);

        gridPane.add(lblSet, 0, ++row);
        cbSet.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(cbSet, 1, row, 3, 1);

        gridPane.add(new Label("Auflösung:"), 0, ++row);
        hBoxSize.setSpacing(20);
        hBoxSize.setPadding(new Insets(10, 5, 10, 5));
        hBoxSize.getChildren().addAll(rbHd, rbHigh, rbSmall);
        gridPane.add(hBoxSize, 1, row, 3, 1);

        gridPane.add(new Label("Dateiname:"), 0, ++row);
        gridPane.add(txtName, 1, row, 3, 1);

        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        cbPath.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(cbPath, 1, row);
        gridPane.add(btnDest, 2, row);
        gridPane.add(btnPropose, 3, row);

        HBox hBox2 = new HBox();
        hBox2.getChildren().add(lblFree);
        hBox2.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(hBox2, 1, ++row, 3, 1);

        gridPane.add(cbxSubtitle, 1, ++row);
        gridPane.add(cbxInfo, 1, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        vBoxCont.setSpacing(20);
        vBoxCont.getChildren().addAll(vBoxAllDownloads, gridPane);


        // Bottom
        HBox hBox = new HBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        hBox.getChildren().add(cbxStart);
        hBoxOk.getChildren().addAll(hBox, btnOk, btnCancel);
    }


    private void initArrays() {
        final int anz = filmsToDownloadList.size();
        downInfo = new DownInfo[anz];

        String aktPath = "";
        if (storedPath.length > 0) {
            aktPath = storedPath[0];
        }

        for (int i = 0; i < anz; ++i) {
            downInfo[i] = new DownInfo();
            downInfo[i].psetData = setData;
            downInfo[i].film = filmsToDownloadList.get(i);
            downInfo[i].download = new Download(setData, downInfo[i].film, DownloadConstants.SRC_DOWNLOAD,
                    null, "", aktPath, "");

            downInfo[i].path = downInfo[i].download.getDestPath();
            downInfo[i].name = downInfo[i].download.getDestFileName();

            if (i < ProgConst.DOWNLOAD_DIALOG_LOAD_MAX_FILESIZE_FROM_WEB) {
                downInfo[i].fileSize_HD = downInfo[i].film.isHd() ?
                        FilmTools.getSizeFromWeb(downInfo[i].film, downInfo[i].film.getUrlForResolution(Film.RESOLUTION_HD)) : "";
                downInfo[i].fileSize_high = FilmTools.getSizeFromWeb(downInfo[i].film,
                        downInfo[i].film.getUrlForResolution(Film.RESOLUTION_NORMAL));
                downInfo[i].fileSize_small = downInfo[i].film.isSmall() ?
                        FilmTools.getSizeFromWeb(downInfo[i].film, downInfo[i].film.getUrlForResolution(Film.RESOLUTION_SMALL)) : "";

            } else {
                // filesize->wenn die Liste länger als ~10 ist, dauert das viel zu lang
                downInfo[i].fileSize_HD = "";
                downInfo[i].fileSize_high = FilmTools.getSizeFromWeb(downInfo[i].film,
                        downInfo[i].film.getUrlForResolution(Film.RESOLUTION_NORMAL));
                downInfo[i].fileSize_small = "";
            }


            downInfo[i].info = downInfo[i].psetData.isInfoFile();

            if (downInfo[i].film.getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                downInfo[i].subDisable = true;
                downInfo[i].subtitle = false;
            } else {
                downInfo[i].subDisable = false;
                downInfo[i].subtitle = downInfo[i].psetData.isSubtitle();
            }

            // die Werte passend zum Film setzen
            if ((filterResolution.equals(Film.RESOLUTION_HD) || downInfo[i].psetData.getResolution().equals(Film.RESOLUTION_HD))
                    && downInfo[i].film.isHd()) {

                //Dann wurde im Filter oder Set HD ausgewählt und wird voreingestellt
                downInfo[i].resolution = Film.RESOLUTION_HD;

            } else if (downInfo[i].psetData.getResolution().equals(Film.RESOLUTION_SMALL)
                    && downInfo[i].film.isSmall()) {
                downInfo[i].resolution = Film.RESOLUTION_SMALL;

            } else {
                downInfo[i].resolution = Film.RESOLUTION_NORMAL;
            }


        }
    }

    private void initButton() {
        btnDest.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnDest.setText("");
        btnDest.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        btnDest.setOnAction(event -> getDestination());

        btnPropose.setGraphic(new ProgIcons().ICON_BUTTON_PROPOSE);
        btnPropose.setText("");
        btnPropose.setTooltip(new Tooltip("Einen Pfad zum Speichern vorschlagen lassen."));
        btnPropose.setOnAction(event -> proposeDestination());


        btnPrev.setOnAction(event -> {
            --actFilmIsShown;
            changeFilmNr();
        });
        btnNext.setOnAction(event -> {
            ++actFilmIsShown;
            changeFilmNr();
        });

        btnOk.setOnAction(event -> {
            if (check()) {
                quit();
            }
        });
        btnCancel.setOnAction(event -> {
            ok = false;
            quit();
        });

    }

    private void initPathAndName() {
        // gespeicherte Pfade eintragen
        cbPath.setEditable(true);
        cbPath.getItems().addAll(storedPath);

        if (downInfo[actFilmIsShown].path.isEmpty()) {
            cbPath.getSelectionModel().selectFirst();
            downInfo[actFilmIsShown].setPath(cbPath.getSelectionModel().getSelectedItem());
        } else {
            cbPath.getSelectionModel().select(downInfo[actFilmIsShown].path);
        }

        cbPath.valueProperty().addListener((observable, oldValue, newValue) -> {
            final String s = cbPath.getSelectionModel().getSelectedItem();
            downInfo[actFilmIsShown].setPath(s);

            calculateAndCheckDiskSpace();
        });

        txtName.setText(downInfo[actFilmIsShown].name);
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            downInfo[actFilmIsShown].setName(txtName.getText());

            if (!txtName.getText().equals(FileNameUtils.checkFileName(txtName.getText(), false /* pfad */))) {
                txtName.setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtName.setStyle("");
            }
        });

        txtName.disableProperty().bind(chkAll.selectedProperty());
    }

    private void initResolutionButton() {
        rbHd.setToggleGroup(group);
        rbHigh.setToggleGroup(group);
        rbSmall.setToggleGroup(group);

        // und jetzt für den aktuellen Film das GUI setzen
        makeResolutionButtons();

        rbHd.setOnAction(a -> downInfo[actFilmIsShown].setResolution(Film.RESOLUTION_HD));
        rbHigh.setOnAction(a -> downInfo[actFilmIsShown].setResolution(Film.RESOLUTION_NORMAL));
        rbSmall.setOnAction(a -> downInfo[actFilmIsShown].setResolution(Film.RESOLUTION_SMALL));
    }

    private void initCheckBox() {
        // und jetzt noch die Listener anhängen
        cbxStart.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD.getBooleanProperty());

        cbxSubtitle.setOnAction(event -> downInfo[actFilmIsShown].setSubtitle(cbxSubtitle.isSelected()));
        cbxInfo.setOnAction(event -> downInfo[actFilmIsShown].setInfo(cbxInfo.isSelected()));
    }

    private void changeFilmNr() {
        final int nr = actFilmIsShown + 1;
        lblSum.setText("Film " + nr + " von " + filmsToDownloadList.size() + " Filmen");

        if (actFilmIsShown == 0) {
            btnPrev.setDisable(true);
            btnNext.setDisable(false);
        } else if (actFilmIsShown == filmsToDownloadList.size() - 1) {
            btnPrev.setDisable(false);
            btnNext.setDisable(true);
        } else {
            btnPrev.setDisable(false);
            btnNext.setDisable(false);
        }

        lblFilmTitle.setText(downInfo[actFilmIsShown].film.getChannel() + "  -  " + downInfo[actFilmIsShown].film.getTitle());
        makeResolutionButtons();
        makeCheckBox();
        makeFilmName();
        calculateAndCheckDiskSpace();
    }


    private void makeResolutionButtons() {
        rbHd.setDisable(!downInfo[actFilmIsShown].film.isHd());
        rbSmall.setDisable(!downInfo[actFilmIsShown].film.isSmall());

        switch (downInfo[actFilmIsShown].resolution) {
            case Film.RESOLUTION_HD:
                rbHd.setSelected(true);
                break;
            case Film.RESOLUTION_SMALL:
                rbSmall.setSelected(true);
                break;
            case Film.RESOLUTION_NORMAL:
            default:
                rbHigh.setSelected(true);
                break;
        }

        if (!rbHd.isDisable() && !downInfo[actFilmIsShown].fileSize_HD.isEmpty()) {
            rbHd.setText(textHd + "   [ " + downInfo[actFilmIsShown].fileSize_HD + " MB ]");
        } else {
            rbHd.setText(textHd);
        }

        if (!downInfo[actFilmIsShown].fileSize_high.isEmpty()) {
            rbHigh.setText(textHeight + "   [ " + downInfo[actFilmIsShown].fileSize_high + " MB ]");
        } else {
            rbHigh.setText(textHeight);
        }

        if (!rbSmall.isDisable() && !downInfo[actFilmIsShown].fileSize_small.isEmpty()) {
            rbSmall.setText(textLow + "   [ " + downInfo[actFilmIsShown].fileSize_small + " MB ]");
        } else {
            rbSmall.setText(textLow);
        }
    }

    private void makeCheckBox() {
        cbxInfo.setSelected(downInfo[actFilmIsShown].info);
        cbxSubtitle.setDisable(downInfo[actFilmIsShown].subDisable);
        cbxSubtitle.setSelected(downInfo[actFilmIsShown].subtitle);
    }

    private void makeFilmName() {
        txtName.setText(downInfo[actFilmIsShown].name);
        cbPath.getSelectionModel().select(downInfo[actFilmIsShown].path);
    }

    private void makePsetChange() {
        if (chkAll.isSelected()) {
            Arrays.stream(downInfo).forEach(d -> makePsetChange(d));
        } else {
            makePsetChange(downInfo[actFilmIsShown]);
        }

        changeFilmNr();
    }

    private void makePsetChange(DownInfo downInfo) {
        SetData psetData = setList.get(cbSet.getSelectionModel().getSelectedIndex());

        downInfo.psetData = psetData;
        downInfo.download = new Download(psetData, downInfo.film, DownloadConstants.SRC_DOWNLOAD, null, "", "", Film.RESOLUTION_NORMAL);
        downInfo.path = downInfo.download.getDestPath();
        downInfo.name = downInfo.download.getDestFileName();
        downInfo.info = downInfo.psetData.isInfoFile();

        if (downInfo.film.getUrlSubtitle().isEmpty()) {
            // dann gibts keinen Subtitle
            downInfo.subDisable = true;
            downInfo.subtitle = false;
        } else {
            downInfo.subDisable = false;
            downInfo.subtitle = downInfo.psetData.isSubtitle();
        }

        // die Werte passend zum Film setzen
        if (downInfo.psetData.getResolution().equals(Film.RESOLUTION_HD)
                && downInfo.film.isHd()) {
            downInfo.resolution = Film.RESOLUTION_HD;

        } else if (downInfo.psetData.getResolution().equals(Film.RESOLUTION_SMALL)
                && downInfo.film.isSmall()) {
            downInfo.resolution = Film.RESOLUTION_SMALL;

        } else {
            downInfo.resolution = Film.RESOLUTION_NORMAL;
        }

    }


    private boolean check() {
        ok = false;

        for (DownInfo d : downInfo) {

            if (d.download == null) {
                PAlert.showErrorAlert("Fehlerhafter Download!", "Fehlerhafter Download!",
                        "Download konnte nicht erstellt werden.");

            } else if (d.path.isEmpty() || d.name.isEmpty()) {
                PAlert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                        "Pfad oder Name ist leer.");

            } else {
                if (!d.path.substring(d.path.length() - 1).equals(File.separator)) {
                    d.path += File.separator;
                }
                if (SetsPrograms.checkPathWritable(d.path)) {
                    ok = true;
                } else {
                    PAlert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                            "Pfad ist nicht beschreibbar.");
                }
            }
        }
        return ok;
    }

    private void quit() {

        if (!ok) {
            close();
            return;
        }

        saveComboPath(cbPath);
        List<Download> list = new ArrayList<>();

        for (DownInfo d : downInfo) {
            // jetzt wird mit den angegebenen Pfaden gearbeitet
            Download download = new Download(d.psetData,
                    d.film,
                    DownloadConstants.SRC_DOWNLOAD,
                    null,
                    d.name,
                    d.path,
                    d.resolution);

            download.setSizeDownloadFromWeb(getFilmSize(d));
            download.setInfoFile(d.info);
            download.setSubtitle(d.subtitle);

            list.add(download);
        }

        progData.downloadList.addWithNr(list);
        if (cbxStart.isSelected()) {
            // und evtl. auch gleich starten
            progData.downloadList.startDownloads(list);
        }

        close();
    }

    private String getFilmSize(DownInfo downInfo) {
        switch (downInfo.resolution) {
            case Film.RESOLUTION_HD:
                return downInfo.fileSize_HD;

            case Film.RESOLUTION_SMALL:
                return downInfo.fileSize_small;

            case Film.RESOLUTION_NORMAL:
            default:
                return downInfo.fileSize_high;

        }
    }


    private void saveComboPath(ComboBox<String> jcb) {
        final ArrayList<String> path1 = new ArrayList<>(jcb.getItems());

        // und die eingestellten Downloadpafade an den Anfang zu stellen
        for (DownInfo d : downInfo) {
            if (path1.contains(d.path)) {
                path1.remove(d.path);
            }
            path1.add(0, d.path);
        }

        // jetzt alle gesammelten Pfade speichern
        final ArrayList<String> path2 = new ArrayList<>();
        path1.stream().forEach(s1 -> {
            // um doppelte auszusortieren
            final String s2 = StringUtils.removeEnd(s1, SEPARATOR);
            if (!path2.contains(s1) && !path2.contains(s2)) {
                path2.add(s2);
            }
        });

        String savePath = PStringUtils.appendList(path2, "<>", true, false);
        ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.setValue(savePath);
    }

    /**
     * Get the free disk space for a selected path.
     *
     * @return Free disk space in bytes.
     */
    private long getFreeDiskSpace(final String strPath) {
        long usableSpace = 0;
        if (!strPath.isEmpty()) {
            try {
                Path path = Paths.get(strPath);
                if (!Files.exists(path)) {
                    path = path.getParent();
                }
                final FileStore fileStore = Files.getFileStore(path);
                usableSpace = fileStore.getUsableSpace();
            } catch (final Exception ignore) {
            }
        }
        return usableSpace;
    }

    /**
     * Calculate free disk space on volume and checkIfExists if the movies can be safely downloaded.
     */
    private void calculateAndCheckDiskSpace() {
        String path = cbPath.getSelectionModel().getSelectedItem();
        if (path == null || path.isEmpty()) {
            return;
        }
        try {

            String noSize = "";
            String sizeFree = "";

            long usableSpace = getFreeDiskSpace(path);
            if (usableSpace > 0) {
                sizeFree = SizeTools.humanReadableByteCount(usableSpace, true);
            }

            // jetzt noch prüfen, obs auf die Platte passt
            usableSpace /= 1_000_000;
            if (usableSpace <= 0) {
                lblFree.setText("");
            } else {

                int size;
                if (!downInfo[actFilmIsShown].fileSize_HD.isEmpty()) {
                    size = Integer.parseInt(downInfo[actFilmIsShown].fileSize_HD);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für HD";

                    }
                }
                if (!downInfo[actFilmIsShown].fileSize_high.isEmpty()) {
                    size = Integer.parseInt(downInfo[actFilmIsShown].fileSize_high);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für \"hoch\"";
                    }
                }
                if (!downInfo[actFilmIsShown].fileSize_small.isEmpty()) {
                    size = Integer.parseInt(downInfo[actFilmIsShown].fileSize_small);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für \"klein\"";
                    }
                }

                if (noSize.isEmpty()) {
                    lblFree.setText(" [ noch frei: " + sizeFree + " ]");
                } else {
                    lblFree.setText(" [ noch frei: " + sizeFree + noSize + " ]");
                }

            }

        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getDestination() {
        DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, cbPath);
    }

    private void proposeDestination() {
        String stdPath, actPath;
        actPath = cbPath.getSelectionModel().getSelectedItem();

        if (setData.getDestPath().isEmpty()) {
            stdPath = PSystemUtils.getStandardDownloadPath();
        } else {
            stdPath = setData.getDestPath();
        }

        actPath = getNextName(stdPath, actPath, downInfo[actFilmIsShown].download.getTheme());
        if (!cbPath.getItems().contains(actPath)) {
            cbPath.getItems().add(actPath);
        }
        cbPath.getSelectionModel().select(actPath);
    }


    private String getNextName(String stdPath, String actDownPath, String theme) {
        String ret = actDownPath;

        theme = DownloadTools.replaceEmptyFileName(theme,
                false /* pfad */,
                Boolean.parseBoolean(ProgConfig.SYSTEM_USE_REPLACETABLE.get()),
                Boolean.parseBoolean(ProgConfig.SYSTEM_ONLY_ASCII.get()));

        if (actDownPath.endsWith(SEPARATOR)) {
            ret = actDownPath.substring(0, actDownPath.length() - SEPARATOR.length());
        }

        try {
            final String date = FORMATTER_ddMMyyyy.format(new Date());
            final boolean isDate = getTime(ret, FORMATTER_ddMMyyyy);
            final boolean isTheme = ret.endsWith(theme) && !theme.isEmpty();
            final boolean isStandard = actDownPath.equals(stdPath);

            if (isStandard) {
                Path path = Paths.get(stdPath, (theme.isEmpty() ? date : theme));
                ret = path.toString();

            } else if (isTheme) {
                Path path = Paths.get(stdPath, date);
                ret = path.toString();

            } else if (isDate) {
                Path path = Paths.get(stdPath);
                ret = path.toString();

            } else {
                Path path = Paths.get(stdPath);
                ret = path.toString();

            }

        } catch (Exception ex) {
            PLog.errorLog(978451203, ex);
            ret = stdPath;
        }
        return ret;
    }

    private boolean getTime(String name, FastDateFormat format) {
        String ret = "";
        Date d;
        try {
            ret = name.substring(name.lastIndexOf(SEPARATOR) + 1);
            d = new Date(format.parse(ret).getTime());
        } catch (Exception ignore) {
            d = null;
        }

        if (d != null && format.getPattern().length() == ret.length()) {
            return true;
        }

        return false;
    }

}
