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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.tools.PIndex;

public class SetData extends SetDataProps {

    private final ProgramList programList = new ProgramList();
//    public static boolean[] spaltenAnzeigen = new boolean[MAX_ELEM];

    public SetData() {
        setId(PIndex.getIndexStr());
    }

    public SetData(String name) {
        // neue Pset sind immer gleich Button
        setId(PIndex.getIndexStr());
        setVisibleName(name);
        setButton(true);
    }

    // public
    public boolean addProg(ProgramData prog) {
        return programList.add(prog);
    }

    public ProgramList getProgramList() {
        return programList;
    }

    public ProgramData getProg(int i) {
        return programList.get(i);
    }

    public boolean progsContainPath() {
        // ein Programmschalter mit
        // "**" (Pfad/Datei) oder %a (Pfad) oder %b (Datei)
        // damit ist es ein Set zum Speichern
        boolean ret = false;

        for (ProgramData progData : programList) {
            if (progData.getProgSwitch().contains("**")
                    || progData.getProgSwitch().contains("%a")
                    || progData.getProgSwitch().contains("%b")) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public boolean isEmpty() {
        boolean ret = true;
        for (final String s : arr) {
            if (!s.isEmpty()) {
                ret = false;
            }
        }
        if (!programList.isEmpty()) {
            ret = false;
        }
        return ret;
    }

    public boolean isLable() {
        // wenn die Programmliste leer ist und einen Namen hat, ist es ein Lable
        return programList.isEmpty() && !getVisibleName().isEmpty();
    }

    public boolean isFreeLine() {
        //Wenn die Programmgruppe keinen Namen hat, leere Zeile
        return getVisibleName().isEmpty();
    }


    public ProgramData getProgUrl(String url) {
        //mit einer Url das Passende Programm finden
        //passt nichts, wird das letzte Programm genommen
        //ist nur ein Programm in der Liste wird dieses genommen
        ProgramData ret = null;
        if (programList.isEmpty()) {
            // todo bei vielen Downloads beim Start kommt das für jeden Download
//            new MTAlert().showInfoAlert("Kein Programm", "Programme einrichten!",
//                    "Es ist kein Programm zum Download eingerichtet");
        } else if (programList.size() == 1) {
            ret = programList.get(0);
        } else {
            for (ProgramData progData : programList) {
                if (progData.urlTesten(url)) {
                    ret = progData;
                    break;
                }
            }
            if (!programList.isEmpty() && ret == null) {
                ret = programList.get(programList.size() - 1);
            }
        }
        return ret;
    }

    public String getDestFileName(String url) {
        //gibt den Zieldateinamen für den Film zurück
        final ProgramData progData = getProgUrl(url);
        String ret = getDestName();
        if (!checkDownloadDirect(url) && progData != null) {
            // nur wenn kein direkter Download und ein passendes Programm
            if (!progData.getDestName().isEmpty()) {
                ret = progData.getDestName();
            }
        }
        return ret;
    }

    public boolean checkDownloadDirect(String url) {
        //auf direkte prüfen, pref oder suf: wenn angegeben dann muss es stimmen
        if (!getPrefix().isEmpty() || !getSuffix().isEmpty()) {
            if (SetsPrograms.testPrefix(getPrefix(), url, true)
                    && SetsPrograms.testPrefix(getSuffix(), url, false)) {
                return true;
            }
        }
        return false;
    }

    public SetData copy() {
        final SetData ret = new SetData();

        setXmlFromProps();
        System.arraycopy(arr, 0, ret.arr, 0, arr.length);
        ret.setPropsFromXml();

        //es darf nur einen geben!
        ret.setId(PIndex.getIndexStr());
        ret.setVisibleName("Kopie-" + getVisibleName());
        ret.setPlay(false);

        for (final ProgramData programData : getProgramList()) {
            ret.addProg(programData.copy());
        }

        return ret;
    }

    public String setDataToString() {
        String ret = "";
        ret += "================================================" + P2LibConst.LINE_SEPARATOR;
        ret += "| Programmset" + P2LibConst.LINE_SEPARATOR;
        for (int i = 0; i < MAX_ELEM; ++i) {
            ret += "| " + COLUMN_NAMES[i] + ": " + arr[i] + P2LibConst.LINE_SEPARATOR;
        }
        for (final Object aListeProg : programList) {
            ret += "|" + P2LibConst.LINE_SEPARATOR;
            ret += aListeProg.toString();
        }
        ret += "|_______________________________________________" + P2LibConst.LINE_SEPARATOR;
        return ret;
    }

}
