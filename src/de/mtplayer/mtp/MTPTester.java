/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.p2tools.p2Lib.dialog.ProgInfoDialog;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MTPTester {
    private final ProgInfoDialog progInfoDialog;
    private final HashSet<String> hashSet = new HashSet<>();
    private final ProgData progData;
    private final TextArea textArea = new TextArea();
    private String text = "";

    public MTPTester(ProgData progData) {
        this.progData = progData;
        this.progInfoDialog = new ProgInfoDialog(false);
        addProgTest();
    }

    public void showDialog() {
        progInfoDialog.showDialog();
    }

    private void addProgTest() {
        if (progInfoDialog != null) {

            final GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(10));
            gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize());

            progInfoDialog.getVboxCont().getChildren().addAll(gridPane);

            Text text = new Text("Debugtools");
            text.setFont(Font.font(null, FontWeight.BOLD, 15));

            Button btnAddToHash = new Button("fillHash");
            btnAddToHash.setMaxWidth(Double.MAX_VALUE);
            btnAddToHash.setOnAction(a -> fillHash(progData.filmlist));

            Button btnCleanHash = new Button("cleanHash");
            btnCleanHash.setMaxWidth(Double.MAX_VALUE);
            btnCleanHash.setOnAction(a -> cleanHash(progData.filmlist));

            Button btnFind = new Button("findAndMark");
            btnFind.setMaxWidth(Double.MAX_VALUE);
            btnFind.setOnAction(a -> findAndMarkNewFilms(progData.filmlist));

            Button btnClear = new Button("clearHash");
            btnClear.setMaxWidth(Double.MAX_VALUE);
            btnClear.setOnAction(a -> clearHash());

            Button btnClearDescription = new Button("Beschreibung löschen");
            btnClearDescription.setMaxWidth(Double.MAX_VALUE);
            btnClearDescription.setOnAction(a -> clearDescription());

            int row = 0;
            gridPane.add(text, 0, row, 2, 1);
            gridPane.add(btnAddToHash, 0, ++row);
            gridPane.add(btnCleanHash, 0, ++row);
            gridPane.add(btnFind, 0, ++row);
            gridPane.add(btnClear, 0, ++row);
            gridPane.add(btnClearDescription, 0, ++row);
            gridPane.add(textArea, 0, ++row, 2, 1);
        }
    }

    private void fillHash(Filmlist filmlist) {
        final List<String> logList = new ArrayList<>();
        logList.add("");
        logList.add("");
        logList.add("");
        logList.add("");
        logList.add(PLog.LILNE3);
        logList.add("fillHash");
        logList.add("Größe Filmliste: " + filmlist.size());
        logList.add("Größe vorher:  " + hashSet.size());

        PDuration.counterStart("fillHash");
        hashSet.addAll(filmlist.stream().map(Film::getUrlHistory).collect(Collectors.toList()));
        PDuration.counterStop("fillHash");

        logList.add("Größe nachher: " + hashSet.size());
        logList.add(PLog.LILNE3);
        PLog.sysLog(logList);
    }

    private void cleanHash(Filmlist filmlist) {
        final List<String> logList = new ArrayList<>();
        logList.add("");
        logList.add(PLog.LILNE3);
        logList.add("Hash bereinigen");
        logList.add("Größe Filmliste: " + filmlist.size());
        logList.add("Größe vorher:  " + hashSet.size());

        PDuration.counterStart("cleanHash");
        filmlist.stream().forEach(film -> hashSet.remove(film.getUrlHistory()));
//        hashSet.removeAll(filmlist.stream().map(Film::getUrlHistory).collect(Collectors.toList()));
        PDuration.counterStop("cleanHash");

        logList.add("Größe nachher: " + hashSet.size());
        logList.add(PLog.LILNE3);
        PLog.sysLog(logList);
    }

    private void findAndMarkNewFilms(Filmlist filmlist) {
        final List<String> logList = new ArrayList<>();
        logList.add("");
        logList.add("");
        logList.add("");
        logList.add("");
        logList.add(PLog.LILNE3);
        logList.add("findAndMarkNewFilms");
        logList.add("Größe Filmliste: " + filmlist.size());
        logList.add("Größe vorher:  " + hashSet.size());

        PDuration.counterStart("findAndMarkNewFilms");
        filmlist.stream() //genauso schnell wie "parallel": ~90ms
                .peek(film -> film.setNewFilm(false))
                .filter(film -> !hashSet.contains(film.getUrlHistory()))
                .forEach(film -> film.setNewFilm(true));
        PDuration.counterStop("findAndMarkNewFilms");

        logList.add("Größe nachher: " + hashSet.size());
        logList.add(PLog.LILNE3);
        PLog.sysLog(logList);
    }

    private void clearHash() {
        final List<String> logList = new ArrayList<>();
        logList.add("");
        logList.add("");
        logList.add("");
        logList.add("");
        logList.add(PLog.LILNE3);
        logList.add("clearHash");
        logList.add("Größe vorher:  " + hashSet.size());

        PDuration.counterStart("clearHash");
        hashSet.clear();
        PDuration.counterStop("clearHash");

        logList.add("Größe nachher: " + hashSet.size());
        logList.add(PLog.LILNE3);
        PLog.sysLog(logList);
    }

    private void clearDescription() {
        final long description = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(50, TimeUnit.DAYS);
        final String DESCRIPTION = "*****";
        int count = 0;
        int countDesc = 0;
        for (Film film : ProgData.getInstance().filmlist) {

            if (!checkDate(film, description)) {
                ++count;
                if (!film.getDescription().isEmpty() && !film.getDescription().equals(DESCRIPTION)) {
                    ++countDesc;
                }
                film.setDescription(DESCRIPTION);
            }
        }

        text += count + " Filme\n";
        text += countDesc + " Beschreibungen gelöscht\n\n";
        textArea.setText(text);


    }

    private boolean checkDate(Film film, long mSeconds) {
        // true wenn der Film jünger ist und angezeigt werden kann!
        try {
            if (film.filmDate.getTime() != 0) {
                if (film.filmDate.getTime() < mSeconds) {
                    return false;
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(951202147, ex);
        }
        return true;
    }
}
