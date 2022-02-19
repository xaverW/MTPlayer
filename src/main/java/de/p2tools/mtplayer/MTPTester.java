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


package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.dialogs.ProgInfoDialog;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.tools.DiacriticFactory;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.regex.Pattern;

public class MTPTester {
    private final ProgInfoDialog progInfoDialog;
    private final HashSet<String> hashSet = new HashSet<>();
    private final ProgData progData;
    private final TextArea textArea = new TextArea();
    private String text = "";
    private final PMaskerPane maskerPane = new PMaskerPane();

    public MTPTester(final ProgData progData) {
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
            gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

            maskerPane.setMaskerVisible(false);
            maskerPane.setButtonText("Abbrechen");
            maskerPane.getButton().setOnAction(a -> close());

            final StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(gridPane, maskerPane);
            progInfoDialog.getvBoxCont().getChildren().addAll(stackPane);


            // Create the ButtonBar instance
            final ButtonBar buttonBar = new ButtonBar();
            final Button okButton = new Button("OK");
            ButtonBar.setButtonData(okButton, ButtonBar.ButtonData.OK_DONE);
            final Button cButton = new Button("Abbrechen");
            ButtonBar.setButtonData(cButton, ButtonBar.ButtonData.CANCEL_CLOSE);
            buttonBar.getButtons().addAll(okButton, cButton);
            progInfoDialog.getvBoxCont().getChildren().add(buttonBar);


            final Text text = new Text("Debugtools");
            text.setFont(Font.font(null, FontWeight.BOLD, 15));

            Button btnMarkFilm = new Button("Diakrit");
            btnMarkFilm.setMaxWidth(Double.MAX_VALUE);
            btnMarkFilm.setOnAction(a -> check());

            int row = 0;
            gridPane.add(text, 0, row, 2, 1);
            gridPane.add(btnMarkFilm, 0, ++row);

            gridPane.add(textArea, 0, ++row, 2, 1);

        }
    }

    int count = 0;
    String test = "äöü ń ǹ ň ñ ṅ ņ ṇ ṋ    ( ç/č/c => c; a/á/à/â/ă/ȁ/å/ā/ã => a aber ä => ä )";

    private void check() {
        PDuration.counterStart("MTPTester diakritische Zeichen");

        progData.filmlist.stream().forEach(film -> {
//            stripDiacritics(film.getTitle());
            flatten(film.getTitle());
        });

        try {
            System.out.println("TEST " + test);
            System.out.println("==============");
            System.out.println("NFC  " + new String(Normalizer.normalize(test, Normalizer.Form.NFC).getBytes("ascii"), "ascii"));
            System.out.println("NFD  " + new String(Normalizer.normalize(test, Normalizer.Form.NFD).getBytes("ascii"), "ascii"));
            System.out.println("NFKC " + new String(Normalizer.normalize(test, Normalizer.Form.NFKC).getBytes("ascii"), "ascii"));
            System.out.println("NFKD " + new String(Normalizer.normalize(test, Normalizer.Form.NFKD).getBytes("ascii"), "ascii"));
            System.out.println("==============");
            System.out.println("NFC  " + new String(Normalizer.normalize(test, Normalizer.Form.NFC)));
            System.out.println("NFD  " + new String(Normalizer.normalize(test, Normalizer.Form.NFD)));
            System.out.println("NFKC " + new String(Normalizer.normalize(test, Normalizer.Form.NFKC)));
            System.out.println("NFKD " + new String(Normalizer.normalize(test, Normalizer.Form.NFKD)));
            System.out.println("==============");
            System.out.println("DiacriticFactory " + DiacriticFactory.flattenDiacritic(test));
            System.out.println("apache " + org.apache.commons.lang3.StringUtils.stripAccents(test));
            flatten(test);

        } catch (Exception ex) {
        }
        PDuration.counterStop("MTPTester diakritische Zeichen");
        System.out.println("Anzahl: " + count);
    }

    private final Pattern DIACRITICS_AND_FRIENDS
            = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    private String stripDiacritics(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
        return str;
    }


    private void flatten(String str) {
        final String s = flattenApache(str);
        if (!str.equals(s)) {
            System.out.println(str);
            System.out.println(s);
            ++count;
        }
    }

    private String flattenApache(String string) {
        try {
            if (!string.contains("ä") && !string.contains("ö") && !string.contains("ü") &&
                    !string.contains("Ä") && !string.contains("Ö") && !string.contains("Ü")) {
//                return org.apache.commons.lang3.StringUtils.stripAccents(string);
//                return Normalizer.normalize(string, Normalizer.Form.NFKD);
                return stripDiacritics(string);

            } else {
                String to = "";
                for (char c : string.toCharArray()) {
                    String s = c + "";
                    if (s.equals("ä") || s.equals("ö") || s.equals("ü") ||
                            s.equals("Ä") || s.equals("Ö") || s.equals("Ü")) {
                        to += s;
                    } else {
//                        to += org.apache.commons.lang3.StringUtils.stripAccents(s);
//                        to += Normalizer.normalize(s, Normalizer.Form.NFKD);
                        to += stripDiacritics(s);
                    }
                }
                return to;
            }
        } catch (Exception ex) {
        }
        return string;
    }


    private String flattenToAscii(String string) {
        String norm = "";
        try {

//         norm = Normalizer.normalize(string, Normalizer.Form.NFC);
            norm = new String(Normalizer.normalize(string, Normalizer.Form.NFKD).getBytes("ascii"), "ascii");
//         norm = Normalizer.normalize(string, Normalizer.Form.NFC).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
//         norm = StringEscapeUtils.unescapeJava(string);

//        char[] out = new char[norm.length()];
//
//        int j = 0;
//        for (int i = 0, n = norm.length(); i < n; ++i) {
//            char c = norm.charAt(i);
//            int type = Character.getType(c);
//
//            //Log.d(TAG,""+c);
//            //by Ricardo, modified the character check for accents, ref: http://stackoverflow.com/a/5697575/689223
//            if (type != Character.NON_SPACING_MARK) {
//                out[j] = c;
//                j++;
//            }
//        }
            //Log.d(TAG,"normalized string:"+norm+"/"+new String(out));

        } catch (Exception ex) {

        }
        return new String(norm);
    }

    private void makrFilterOk(final boolean ok) {
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

    public void close() {
        maskerPane.switchOffMasker();
    }
}
