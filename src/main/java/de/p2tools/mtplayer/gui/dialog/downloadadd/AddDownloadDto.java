/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.dialog.downloadadd;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.guitools.P2MultiLineLabel;
import de.p2tools.p2lib.guitools.P2TimePicker;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class AddDownloadDto {

    public boolean addNewDownloads = true;

    public InitSetDataDownload initSetDataDownload;
    public InitPathName initPathName;
    public InitResolutionButton initResolutionButton;
    public InitSubTitle initSubTitle;
    public InitProgramCall initProgramCall;
    public InitUrl initUrl;
    public InitStartTimeDownload initStartTimeDownload;

    public ProgData progData;
    public AddDownloadData[] addDownloadData;

    public IntegerProperty actFilmIsShown = new SimpleIntegerProperty(0);
//    public final String filterResolution = "";

    public final Label lblFree = new Label("4M noch frei");
    public final Label lblFilm = new Label("Film:");
    public final Label lblFilmTitle = new Label("ARD: Tatort, ..");
    public final Label lblFilmDateTime = new Label("2023, ...");
    public final Button btnPrev = new Button("<");
    public final Button btnNext = new Button(">");
    public final Label lblSum = new Label("");
    public Button btnAll = new Button("Für alle\nändern");

    public CheckBox chkSetAll = new CheckBox();
    public CheckBox chkResolutionAll = new CheckBox();
    public CheckBox chkPathAll = new CheckBox();
    public CheckBox chkSubTitleAll = new CheckBox();
    public CheckBox chkInfoAll = new CheckBox();
    public CheckBox chkStartTimeAll = new CheckBox();

    // SetData
    public SetData setDataStart; // nur für den Start zur init
    public final Text textSet = DownloadAddDialogFactory.getText("Set:");
    public final ComboBox<SetData> cboSetData = new ComboBox<>();

    // URL
    public final P2Hyperlink p2HyperlinkUrlFilm = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL);
    public final P2Hyperlink p2HyperlinkUrlDownload = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL);

    // Programmaufruf
    public final Label lblProgramIsDownload = new Label("Direkter Download");
    public final P2MultiLineLabel textAreaProg = new P2MultiLineLabel();
    public final TextArea textAreaCallArray = new TextArea();
    public final VBox vBoxProgramCall = new VBox(P2LibConst.PADDING_VBOX);
    public final Button btnProgramCallHelp = P2Button.helpButton("Den Programmaufruf ändern", HelpText.EDIT_DOWNLOAD_WITH_PROG);
    public final Button btnProgramCallReset = new Button("");

    // Path / Name
    public final ComboBox<String> cboPath = new ComboBox<>();
    public final Button btnDest = new Button(); // Pfad auswählen
    public final Button btnPropose = new Button(); // Pfad vorschlagen
    public final Button btnClean = new Button(); // Liste der Pfade löschen
    public final TextField txtName = new TextField();

    // info / subTitle
    public final CheckBox chkInfo = new CheckBox("Infodatei anlegen: \"Filmname.txt\"");
    public final CheckBox chkSubtitle = new CheckBox("Untertitel speichern: \"Filmname.xxx\"");

    // resolution
    public final RadioButton rbHd = new RadioButton("HD");
    public final RadioButton rbHigh = new RadioButton("Hoch");
    public final RadioButton rbSmall = new RadioButton("Klein");

    // Startzeit
    public final P2TimePicker p2TimePicker = new P2TimePicker();
    public final RadioButton rbStartNotYet = new RadioButton("noch nicht");
    public final RadioButton rbStartNow = new RadioButton("sofort");
    public final RadioButton rbStartAtTime = new RadioButton("um: ");

    public AddDownloadDto(ProgData progData, SetData setDataStart, ArrayList<FilmDataMTP> filmsToDownloadList) {
        // einen neuen Download anlegen
        this.progData = progData;
        this.setDataStart = setDataStart;

        addDownloadData = InitDownloadAddArray.initDownloadInfoArrayFilm(filmsToDownloadList, this);
        initSetDataDownload = new InitSetDataDownload(this);
        initPathName = new InitPathName(this);
        initResolutionButton = new InitResolutionButton(this);
        initSubTitle = new InitSubTitle(this);
        initProgramCall = new InitProgramCall(this);
        initUrl = new InitUrl(this);
        initStartTimeDownload = new InitStartTimeDownload(this);
    }

    public AddDownloadDto(ProgData progData, ArrayList<DownloadData> downloadDataArrayList) {
        // bestehende Downloads ändern
        this.progData = progData;
        this.setDataStart = null;
        this.addNewDownloads = false;

        addDownloadData = InitDownloadAddArray.initDownloadInfoArrayDownload(downloadDataArrayList, this);
        initSetDataDownload = new InitSetDataDownload(this);
        initPathName = new InitPathName(this);
        initResolutionButton = new InitResolutionButton(this);
        initSubTitle = new InitSubTitle(this);
        initProgramCall = new InitProgramCall(this);
        initUrl = new InitUrl(this);
        initStartTimeDownload = new InitStartTimeDownload(this);
    }

    public AddDownloadData getAct() {
        return addDownloadData[actFilmIsShown.getValue()];
    }

    public void updateAct() {
        final int nr = actFilmIsShown.getValue() + 1;
        lblSum.setText("Film " + nr + " von " + addDownloadData.length + " Filmen");

        if (actFilmIsShown.getValue() == 0) {
            btnPrev.setDisable(true);
            btnNext.setDisable(false);
        } else if (actFilmIsShown.getValue() == addDownloadData.length - 1) {
            btnPrev.setDisable(false);
            btnNext.setDisable(true);
        } else {
            btnPrev.setDisable(false);
            btnNext.setDisable(false);
        }

        lblFilmTitle.setText(addDownloadData[actFilmIsShown.getValue()].download.getChannel()
                + "  -  " + addDownloadData[actFilmIsShown.getValue()].download.getTitle());
        lblFilmDateTime.setText("Datum: " + addDownloadData[actFilmIsShown.getValue()].download.getFilmDate()
                + "       Zeit: " + addDownloadData[actFilmIsShown.getValue()].download.getFilmTime()
                + "       Dauer [min]: " + addDownloadData[actFilmIsShown.getValue()].download.getDurationMinute());

        initSetDataDownload.makeAct();
        initPathName.makeAct();
        initResolutionButton.makeAct();
        initSubTitle.makeAct();
        initProgramCall.makeAct();
        initUrl.makeAct();
        initStartTimeDownload.makeAct();
    }
}
