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

public class SetData extends SetProps {

    private final ProgList progList = new ProgList();
//    public static boolean[] spaltenAnzeigen = new boolean[MAX_ELEM];

    public SetData() {
    }

    public SetData(String name) {
        // neue Pset sind immer gleich Button
        setName(name);
        setButton(true);
    }

    // public
    public boolean addProg(ProgData prog) {
        return progList.add(prog);
    }

    public ProgList getProgList() {
        return progList;
    }

    public ProgData getProg(int i) {
        return progList.get(i);
    }

    public boolean progsContainPath() {
        // ein Programmschalter mit
        // "**" (Pfad/Datei) oder %a (Pfad) oder %b (Datei)
        // damit ist es ein Set zum Speichern
        boolean ret = false;

        for (ProgData progData : progList) {
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
        if (!progList.isEmpty()) {
            ret = false;
        }
        return ret;
    }

    public boolean isLable() {
        // wenn die Programmliste leer ist und einen Namen hat, ist es ein Lable
        return progList.isEmpty() && !getName().isEmpty();
    }

    public boolean isFreeLine() {
        //Wenn die Programmgruppe keinen Namen hat, leere Zeile
        return getName().isEmpty();
    }


    public ProgData getProgUrl(String url) {
        //mit einer Url das Passende Programm finden
        //passt nichts, wird das letzte Programm genommen
        //ist nur ein Programm in der Liste wird dieses genommen
        ProgData ret = null;
        if (progList.isEmpty()) {
            // todo bei vielen Downloads beim Start kommt das für jeden Download
//            new MTAlert().showInfoAlert("Kein Programm", "Programme einrichten!",
//                    "Es ist kein Programm zum Download eingerichtet");
        } else if (progList.size() == 1) {
            ret = progList.get(0);
        } else {
            for (ProgData progData : progList) {
                if (progData.urlTesten(url)) {
                    ret = progData;
                    break;
                }
            }
            if (!progList.isEmpty() && ret == null) {
                ret = progList.get(progList.size() - 1);
            }
        }
        return ret;
    }

    public String getDestFileName(String url) {
        //gibt den Zieldateinamen für den Film zurück
        final ProgData progData = getProgUrl(url);
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
        if (!getPraefix().isEmpty() || !getSuffix().isEmpty()) {
            if (SetsPrograms.testPrefix(getPraefix(), url, true)
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
        ret.setName("Kopie-" + getName());
        ret.setPlay(false);

        for (final ProgData prog : getProgList()) {
            ret.addProg(prog.copy());
        }

        return ret;
    }

}
