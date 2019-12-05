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
import de.mtplayer.mtp.controller.starter.MTNotification;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilterFactory;
import de.p2tools.p2Lib.dialog.ProgInfoDialog;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
    private final PMaskerPane maskerPane = new PMaskerPane();
    private final WaitTask waitTask = new WaitTask();

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

            maskerPane.setMaskerVisible(false);
            maskerPane.setButtonText("Abbrechen");
            maskerPane.getButton().setOnAction(a -> close());

            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(gridPane, maskerPane);
            progInfoDialog.getVboxCont().getChildren().addAll(stackPane);


            // Create the ButtonBar instance
            ButtonBar buttonBar = new ButtonBar();
            Button okButton = new Button("OK");
            ButtonBar.setButtonData(okButton, ButtonBar.ButtonData.OK_DONE);
            Button cButton = new Button("Abbrechen");
            ButtonBar.setButtonData(cButton, ButtonBar.ButtonData.CANCEL_CLOSE);
            buttonBar.getButtons().addAll(okButton, cButton);
            progInfoDialog.getVboxCont().getChildren().add(buttonBar);


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

            Button btnCheck = new Button("Text checken (UTF8)");
            btnCheck.setMaxWidth(Double.MAX_VALUE);
            btnCheck.setOnAction(a -> checkText());

            Button btnRepair = new Button("Text reparieren (UTF8)");
            btnRepair.setMaxWidth(Double.MAX_VALUE);
            btnRepair.setOnAction(a -> repairText());

            Button btnShowActFilter = new Button("aktuellen Filter ausgeben");
            btnShowActFilter.setMaxWidth(Double.MAX_VALUE);
            btnShowActFilter.setOnAction(a -> showFilter());

            Button btnMarkFilmFilterOk = new Button("Filter Ok");
            btnMarkFilmFilterOk.setMaxWidth(Double.MAX_VALUE);
            btnMarkFilmFilterOk.setOnAction(a -> makrFilterOk(true));

            Button btnMarkFilmFilterNotOk = new Button("Filter !Ok");
            btnMarkFilmFilterNotOk.setMaxWidth(Double.MAX_VALUE);
            btnMarkFilmFilterNotOk.setOnAction(a -> makrFilterOk(false));

            Button btnStartWaiting = new Button("start waiting");
            btnStartWaiting.setMaxWidth(Double.MAX_VALUE);
            btnStartWaiting.setOnAction(a -> startWaiting());

            Button btnNotify = new Button("Notify");
            btnNotify.setMaxWidth(Double.MAX_VALUE);
            btnNotify.setOnAction(a -> MTNotification.addNotification(true));

            PToggleSwitch ptgl = new PToggleSwitch("Test");
            ptgl.setAllowIndeterminate(true);

            int row = 0;
            gridPane.add(text, 0, row, 2, 1);
            gridPane.add(btnAddToHash, 0, ++row);
            gridPane.add(btnCleanHash, 1, row);
            gridPane.add(btnFind, 0, ++row);
            gridPane.add(btnClear, 1, row);
            gridPane.add(btnClearDescription, 0, ++row);
            gridPane.add(btnCheck, 0, ++row);
            gridPane.add(btnRepair, 1, row);
            gridPane.add(btnShowActFilter, 0, ++row);
            gridPane.add(btnMarkFilmFilterOk, 1, row);
            gridPane.add(btnMarkFilmFilterNotOk, 1, row);
            gridPane.add(btnStartWaiting, 0, ++row);
            gridPane.add(btnNotify, 0, ++row);

            gridPane.add(ptgl, 0, ++row);
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

    private void checkText() {
        Filmlist filmlist = progData.filmlist;

        int i = 0;
        int countTheme = 0, countTitle = 0, countDescreption = 0;

        try {
            for (Film film : filmlist) {
                ++i;
                if (i % 10_000 == 0) {
                    System.out.println("fertig: " + i);
                }

                if (!cleanUnicode(film.getTitle()).equals(film.getTitle())) {
                    System.out.println(film.getDate() + " Titel:  " + film.getTitle());
                    System.out.println("                   " + cleanUnicode(film.getTitle()));
                    ++countTitle;
                }
                if (!cleanUnicode(film.getTheme()).equals(film.getTheme())) {
                    System.out.println(film.getDate() + " Thema:  " + film.getTheme());
                    System.out.println("                   " + cleanUnicode(film.getTheme()));
                    ++countTheme;
                }
                if (!cleanUnicode(film.getDescription()).equals(film.getDescription())) {
                    ++countDescreption;
                }

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Themen: " + countTheme);
        System.out.println("Titel: " + countTitle);
        System.out.println("Beschreibungen: " + countDescreption);
    }

    private void repairText() {
        Filmlist filmlist = progData.filmlist;

        try {
            for (Film film : filmlist) {
                film.arr[Film.FILM_TITLE] = cleanUnicode(film.getTitle());
                film.arr[Film.FILM_THEME] = cleanUnicode(film.getTheme());
                film.setDescription(cleanUnicode(film.getDescription()));

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showFilter() {
        SelectedFilter sf = progData.storedFilters.getActFilterSettings();
        System.out.println("====================================");
        for (String s : SelectedFilterFactory.printFilter(sf)) {
            System.out.println(s);
        }
        System.out.println("====================================");
    }

    private void makrFilterOk(boolean ok) {
//        progData.mtPlayerController.markFilterOk(ok);
    }

    private static String cleanUnicode(String ret) {
//        final String regEx = "[\\p{Cc}&&[^\n,\r,\t,\\x7F,\\x10,\\x11,\\x12,\\x13,\\x14,\\x15,\\x16," +
//                "\\x17,\\x18,\\x19,\\x1A,\\x1B,\\x1C,\\x1D,\\x1E,\\x1F," +
//                "\\x00,\\x01,\\x02,\\x03,\\x04,\\x05,\\x06,\\x07,\\x08,\\x09,\\x0A,\\x0B,\\x0C,\\x0D,\\x0E,\\x0F," +
//                "\uC280-\uC29F]]";


//        final String regEx = "\uC296";

//        if (!ret.equals(ret.replaceAll(regEx, "?"))) {
//            System.out.println();
//            System.out.println(ret);
//            System.out.println(ret.replaceAll(regEx, "******"));
//        }
        // ret = ret.replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "?");


        final String regEx = "[\\p{Cc}&&[^\n,\r,\t]]";
        ret = ret.replaceAll(regEx, "?");

        return ret;
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

    private void startWaiting() {
        maskerPane.setMaskerText("Filmliste ist zu alt, eine neue downloaden");
        maskerPane.setButtonText("Button Text");
        maskerPane.setMaskerVisible(true, true, true);
        Thread th = new Thread(waitTask);
        th.setName("startWaiting");
        th.start();
    }

    private class WaitTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            try {
                Thread.sleep(5000);
            } catch (Exception ignore) {
            }
            return null;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return super.cancel(mayInterruptIfRunning);
        }
    }

    public void close() {
        if (waitTask.isRunning()) {
            waitTask.cancel();
        }
        maskerPane.switchOffMasker();
    }
}
