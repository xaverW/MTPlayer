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
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class DownloadAddDialogController extends MTDialog {

    @FXML
    private VBox vBoxCont;
    @FXML
    private VBox vBoxAllDownloads;
    @FXML
    private HBox hBoxTop;
    @FXML
    private HBox hBoxAll;
    @FXML
    private HBox hBoxSize;

    @FXML
    private Button btnPrev;
    @FXML
    private Button btnNext;
    @FXML
    private Label lblSum;

    @FXML
    ToggleSwitch tglAll;
    @FXML
    private Label lblSet;
    @FXML
    private ComboBox<String> cbSet;
    @FXML
    private ComboBox<String> cbPath;
    @FXML
    private Button btnDest;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField txtName;
    @FXML
    private CheckBox cbxStart;
    @FXML
    private CheckBox cbxInfo;
    @FXML
    private CheckBox cbxSubtitle;

    //    @FXML
//    private CheckBox cbxPath;
    @FXML
    private RadioButton rbHd;
    @FXML
    private RadioButton rbHigh;
    @FXML
    private RadioButton rbSmall;

    @FXML
    private Label lblFree;
    @FXML
    private Label lblFilm;
    @FXML
    private Label lblFilmTitle;
    @FXML
    private GridPane gridPane;

    private boolean ok = false;

    private boolean nameChanged = false;

    private final String textHd = "HD";
    private final String textHeight = "hohe Auflösung";
    private final String textLow = "niedrige Auflösung";

    private final ToggleGroup group = new ToggleGroup();

    private final ProgData progData;
    final private SetList setList;
    private SetData psetData;
    private String filterResolution;
    final String[] storedPath = ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");


    private int filmNr = 0;
    private ArrayList<Film> films;

    DownInfo downInfo[];

    class DownInfo {
        private String fileSize_HD = "";
        private String fileSize_high = "";
        private String fileSize_small = "";

        private String resolution = FilmXml.RESOLUTION_HD;
        private boolean info, subtitle, subDisable = false;

        private Film film;
        private Download download;
        private SetData psetData;

        private String path = "";
        private String name = "";


        private void setResolution(String resolution) {
            if (tglAll.isSelected()) {
                Arrays.stream(downInfo).forEach(d -> {
                    d.resolution = resolution;

                    switch (resolution) {
                        case FilmXml.RESOLUTION_HD:
                            if (d.fileSize_HD.isEmpty()) {
                                d.resolution = FilmXml.RESOLUTION_NORMAL;
                            }
                            break;
                        case FilmXml.RESOLUTION_SMALL:
                            if (d.fileSize_small.isEmpty()) {
                                d.resolution = FilmXml.RESOLUTION_NORMAL;
                            }
                            break;
                    }

                });
            } else {
                this.resolution = resolution;
            }
        }

        private void setInfo(boolean info) {
            if (tglAll.isSelected()) {
                Arrays.stream(downInfo).forEach(d -> d.info = info);
            } else {
                this.info = info;
            }
        }

        private void setSubtitle(boolean subtitle) {
            if (tglAll.isSelected()) {
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
            if (tglAll.isSelected()) {
                Arrays.stream(downInfo).forEach(d -> d.path = path);
            } else {
                this.path = path;
            }
        }

    }

    public DownloadAddDialogController(ProgData progData, ArrayList<Film> films, SetData psetData, String filterResolution) {
        super("/de/mtplayer/mtp/gui/dialog/DownloadAddDialog.fxml",
                films.size() > 1 ? ProgConfig.DOWNLOAD_DIALOG_ADD_MORE_SIZE : ProgConfig.DOWNLOAD_DIALOG_ADD_SIZE,
                "Download anlegen", true);

        this.progData = progData;
        this.films = films;
        this.psetData = psetData;
        this.filterResolution = filterResolution;

        setList = progData.setList.getListSave();

        init(true);

    }

    @Override
    public void make() {
        vBoxCont.getStyleClass().add("dialog-border");
        vBoxAllDownloads.setStyle("-fx-background-color: gainsboro;");
        hBoxSize.setStyle("-fx-background-color: gainsboro;");

        if (progData.setList.getListSave().isEmpty()) {
            // Satz mit x, war wohl nix
            ok = false;
            initCancel();
            quitt();
            return;
        }
        if (psetData == null) {
            psetData = progData.setList.getListSave().get(0);
        }

        if (films.size() == 0) {
            // Satz mit x, war wohl nix
            ok = false;
            initCancel();
            quitt();
            return;

        } else if (films.size() == 1) {
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
            cbSet.getSelectionModel().select(psetData.getName());
            cbSet.setOnAction(a -> makePsetChange());
        }

        lblFilm.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setStyle("-fx-font-weight: bold;");

        initArrays();
        initButton();
        initPathAndName();
        initResolutionButton();
        initCheckBox();

        changeFilmNr();
    }


    private void changeFilmNr() {
        final int nr = filmNr + 1;
        lblSum.setText("Film " + nr + " von " + films.size() + " Filmen");

        if (filmNr == 0) {
            btnPrev.setDisable(true);
            btnNext.setDisable(false);
        } else if (filmNr == films.size() - 1) {
            btnPrev.setDisable(false);
            btnNext.setDisable(true);
        } else {
            btnPrev.setDisable(false);
            btnNext.setDisable(false);
        }

        lblFilmTitle.setText(downInfo[filmNr].film.getChannel() + "  -  " + downInfo[filmNr].film.getTitle());
        makeResolutionButtons();
        makeCheckBox();
        makeFilmName();
        calculateAndCheckDiskSpace();
    }

    private void makeResolutionButtons() {
        rbHd.setDisable(!downInfo[filmNr].film.isHd());
        rbSmall.setDisable(!downInfo[filmNr].film.isSmall());

        switch (downInfo[filmNr].resolution) {
            case FilmXml.RESOLUTION_HD:
                rbHd.setSelected(true);
                break;
            case FilmXml.RESOLUTION_SMALL:
                rbSmall.setSelected(true);
                break;
            case FilmXml.RESOLUTION_NORMAL:
            default:
                rbHigh.setSelected(true);
                break;
        }

        if (!rbHd.isDisable() && !downInfo[filmNr].fileSize_HD.isEmpty()) {
            rbHd.setText(textHd + "   [ " + downInfo[filmNr].fileSize_HD + " MB ]");
        } else {
            rbHd.setText(textHd);
        }

        if (!downInfo[filmNr].fileSize_high.isEmpty()) {
            rbHigh.setText(textHeight + "   [ " + downInfo[filmNr].fileSize_high + " MB ]");
        } else {
            rbHigh.setText(textHeight);
        }

        if (!rbSmall.isDisable() && !downInfo[filmNr].fileSize_small.isEmpty()) {
            rbSmall.setText(textLow + "   [ " + downInfo[filmNr].fileSize_small + " MB ]");
        } else {
            rbSmall.setText(textLow);
        }
    }

    private void makeCheckBox() {
        cbxInfo.setSelected(downInfo[filmNr].info);
        cbxSubtitle.setDisable(downInfo[filmNr].subDisable);
        cbxSubtitle.setSelected(downInfo[filmNr].subtitle);
    }

    private void makeFilmName() {
        txtName.setText(downInfo[filmNr].name);
        cbPath.getSelectionModel().select(downInfo[filmNr].path);
    }

    private void makePsetChange() {
        if (tglAll.isSelected()) {
            Arrays.stream(downInfo).forEach(d -> makePsetChange(d));
        } else {
            makePsetChange(downInfo[filmNr]);
        }

        changeFilmNr();

    }

    private void makePsetChange(DownInfo downInfo) {
        SetData psetData = setList.get(cbSet.getSelectionModel().getSelectedIndex());

        downInfo.psetData = psetData;

        downInfo.download = new Download(psetData, downInfo.film, DownloadInfos.SRC_DOWNLOAD, null, "", "", FilmXml.RESOLUTION_NORMAL);

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
        if (downInfo.psetData.getResolution().equals(FilmXml.RESOLUTION_HD)
                && downInfo.film.isHd()) {
            downInfo.resolution = FilmXml.RESOLUTION_HD;

        } else if (downInfo.psetData.getResolution().equals(FilmXml.RESOLUTION_SMALL)
                && downInfo.film.isSmall()) {
            downInfo.resolution = FilmXml.RESOLUTION_SMALL;

        } else {
            downInfo.resolution = FilmXml.RESOLUTION_NORMAL;
        }

    }


    private boolean check() {
        ok = false;

        for (DownInfo d : downInfo) {

            if (d.download == null) {
                new MTAlert().showErrorAlert("Fehlerhafter Download!", "Fehlerhafter Download!",
                        "Download konnte nicht erstellt werden.");

            } else if (d.path.isEmpty() || d.name.isEmpty()) {
                new MTAlert().showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                        "Pfad oder Name ist leer.");

            } else {
                if (!d.path.substring(d.path.length() - 1).equals(File.separator)) {
                    d.path += File.separator;
                }
                if (SetsPrograms.checkPathWritable(d.path)) {
                    ok = true;
                } else {
                    new MTAlert().showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                            "Pfad ist nicht beschreibbar.");
                }
            }
        }
        return ok;
    }

    private void quitt() {

        if (!ok) {
            close();
            return;
        }

        saveComboPath(cbPath);
        for (DownInfo d : downInfo) {
            // jetzt wird mit den angegebenen Pfaden gearbeitet
            Download download = new Download(d.psetData,
                    d.film,
                    DownloadInfos.SRC_DOWNLOAD,
                    null,
                    d.name,
                    d.path,
                    d.resolution);

            download.setSizeDownloadFromWeb(getFilmSize(d));
            download.setInfoFile(d.info);
            download.setSubtitle(d.subtitle);

            progData.downloadList.addWithNr(download); // todo -> als Liste starten
            if (cbxStart.isSelected()) {
                // und evtl. auch gleich starten
                progData.downloadList.startDownloads(download);
            }
        }

        close();
    }

    private String getFilmSize(DownInfo downInfo) {
        switch (downInfo.resolution) {
            case FilmXml.RESOLUTION_HD:
                return downInfo.fileSize_HD;

            case FilmXml.RESOLUTION_SMALL:
                return downInfo.fileSize_small;

            case FilmXml.RESOLUTION_NORMAL:
            default:
                return downInfo.fileSize_high;

        }
    }


    private void saveComboPath(ComboBox<String> jcb) {
        final ArrayList<String> path = new ArrayList<>(jcb.getItems());

        for (DownInfo d : downInfo) {
            if (path.contains(d.path)) {
                path.remove(d.path);
            }
            path.add(0, d.path);
        }

        final ArrayList<String> path2 = new ArrayList<>();
        path.stream().forEach(s1 -> {
            // um doppelte auszusortieren
            if (!path2.contains(s1)) {
                path2.add(s1);
            }
        });

        String s = "";
        if (!path2.isEmpty()) {
            s = path2.get(0);
            for (int i = 1; i < ProgConst.MAX_PFADE_DIALOG_DOWNLOAD && i < path2.size(); ++i) {
                if (!path2.get(i).isEmpty()) {
                    s += "<>" + path2.get(i);
                }
            }
        }

        ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.setValue(s);
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
                if (!downInfo[filmNr].fileSize_HD.isEmpty()) {
                    size = Integer.parseInt(downInfo[filmNr].fileSize_HD);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für HD";

                    }
                }
                if (!downInfo[filmNr].fileSize_high.isEmpty()) {
                    size = Integer.parseInt(downInfo[filmNr].fileSize_high);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für \"hoch\"";
                    }
                }
                if (!downInfo[filmNr].fileSize_small.isEmpty()) {
                    size = Integer.parseInt(downInfo[filmNr].fileSize_small);
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

    private void initArrays() {
        final int anz = films.size();
        downInfo = new DownInfo[anz];

        String aktPath = "";
//        if (ProgConfig.SYSTEM_DIALOG_DOWNLOAD__LETZTEN_PFAD_ANZEIGEN.getBool() && storedPath.length > 0) {
//            aktPath = storedPath[0];
//        }

        if (storedPath.length > 0) {
            aktPath = storedPath[0];
        }

        for (int i = 0; i < anz; ++i) {
            downInfo[i] = new DownInfo();

            downInfo[i].psetData = psetData;

            downInfo[i].film = films.get(i);

            downInfo[i].download = new Download(psetData, downInfo[i].film, DownloadInfos.SRC_DOWNLOAD,
                    null, "", aktPath, "");

            downInfo[i].path = downInfo[i].download.getDestPath();
            downInfo[i].name = downInfo[i].download.getDestFileName();

            downInfo[i].fileSize_HD = downInfo[i].film.isHd() ?
                    FilmTools.getSizeFromWeb(downInfo[i].film, downInfo[i].film.getUrlForResolution(FilmXml.RESOLUTION_HD)) : "";

            downInfo[i].fileSize_high = FilmTools.getSizeFromWeb(downInfo[i].film,
                    downInfo[i].film.getUrlForResolution(Film.RESOLUTION_NORMAL));

            downInfo[i].fileSize_small = downInfo[i].film.isSmall() ?
                    FilmTools.getSizeFromWeb(downInfo[i].film, downInfo[i].film.getUrlForResolution(FilmXml.RESOLUTION_SMALL)) : "";

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
            if ((filterResolution.equals(FilmXml.RESOLUTION_HD) || downInfo[i].psetData.getResolution().equals(FilmXml.RESOLUTION_HD))
                    && downInfo[i].film.isHd()) {

                //Dann wurde im Filter oder Set HD ausgewählt und wird voreingestellt
                downInfo[i].resolution = FilmXml.RESOLUTION_HD;

            } else if (downInfo[i].psetData.getResolution().equals(FilmXml.RESOLUTION_SMALL)
                    && downInfo[i].film.isSmall()) {
                downInfo[i].resolution = FilmXml.RESOLUTION_SMALL;

            } else {
                downInfo[i].resolution = FilmXml.RESOLUTION_NORMAL;
            }


        }
    }

    private void initPathAndName() {
        // gespeicherte Pfade eintragen
        cbPath.setEditable(true);
        cbPath.getItems().addAll(storedPath);

        if (downInfo[filmNr].path.isEmpty()) {
            cbPath.getSelectionModel().selectFirst();
            downInfo[filmNr].setPath(cbPath.getSelectionModel().getSelectedItem());
        } else {
            cbPath.getSelectionModel().select(downInfo[filmNr].path);
        }

        cbPath.valueProperty().addListener((observable, oldValue, newValue) -> {
            nameChanged = true;

            final String s = cbPath.getSelectionModel().getSelectedItem();
            downInfo[filmNr].setPath(s);

            calculateAndCheckDiskSpace();
        });

        txtName.setText(downInfo[filmNr].name);
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            nameChanged = true;

            downInfo[filmNr].setName(txtName.getText());

            if (!txtName.getText().equals(FileNameUtils.checkFileName(txtName.getText(), false /* pfad */))) {
                txtName.setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtName.setStyle("");
            }
        });

        txtName.disableProperty().bind(tglAll.selectedProperty());
    }

    private void initResolutionButton() {
        rbHd.setToggleGroup(group);
        rbHigh.setToggleGroup(group);
        rbSmall.setToggleGroup(group);

        // und jetzt für den aktuellen Film das GUI setzen
        makeResolutionButtons();

        rbHd.setOnAction(a -> downInfo[filmNr].setResolution(FilmXml.RESOLUTION_HD));
        rbHigh.setOnAction(a -> downInfo[filmNr].setResolution(FilmXml.RESOLUTION_NORMAL));
        rbSmall.setOnAction(a -> downInfo[filmNr].setResolution(FilmXml.RESOLUTION_SMALL));
    }

    private void initCheckBox() {
        // und jetzt noch die Listener anhängen
        cbxStart.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD.getBooleanProperty());
//        cbxPath.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_DIALOG_DOWNLOAD__LETZTEN_PFAD_ANZEIGEN.getBooleanProperty());

        cbxSubtitle.setOnAction(event -> downInfo[filmNr].setSubtitle(cbxSubtitle.isSelected()));
        cbxInfo.setOnAction(event -> downInfo[filmNr].setInfo(cbxInfo.isSelected()));
    }

    private void initCancel() {
        btnDest.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnDest.setText("");
        btnDest.setDisable(true);

        btnPrev.setDisable(true);
        btnNext.setDisable(true);

        btnOk.setDisable(true);
        btnCancel.setOnAction(event -> {
            ok = false;
            quitt();
        });

    }

    private void initButton() {
        btnDest.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnDest.setText("");
        btnDest.setOnAction(event -> getDestination());

        btnPrev.setOnAction(event -> {
            --filmNr;
            changeFilmNr();
        });
        btnNext.setOnAction(event -> {
            ++filmNr;
            changeFilmNr();
        });

        btnOk.setOnAction(event -> {
            if (check()) {
                quitt();
            }
        });
        btnCancel.setOnAction(event -> {
            ok = false;
            quitt();
        });

    }

    private void getDestination() {
        DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, cbPath);
    }

}
