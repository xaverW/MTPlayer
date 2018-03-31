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

import de.mtplayer.mLib.tools.Data;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BlackProps extends Data<BlackProps> {


    public static final int BLACKLIST_NR = 0;
    public static final int BLACKLIST_SENDER = 1;
    public static final int BLACKLIST_SENDER_EXAKT = 2;
    public static final int BLACKLIST_THEMA = 3;
    public static final int BLACKLIST_THEMA_EXAKT = 4;
    public static final int BLACKLIST_TITEL = 5;
    public static final int BLACKLIST_THEMA_TITEL = 6;

    public static final String TAG = "Blacklist";
    public static final String[] XML_NAMES = {
            "black-nr",
            "black-sender",
            "black-sender-exakt",
            "black-thema",
            "black-thema-exakt",
            "black-titel",
            "black-thema-titel"};
    public static int MAX_ELEM = XML_NAMES.length;

    public String[] arr;

    private int nr = 0;
    private final StringProperty sender = new SimpleStringProperty("");
    private final BooleanProperty senderExact = new SimpleBooleanProperty(true);
    private final StringProperty thema = new SimpleStringProperty("");
    private final BooleanProperty themaExact = new SimpleBooleanProperty(true);
    private final StringProperty titel = new SimpleStringProperty("");
    private final StringProperty themaTitel = new SimpleStringProperty("");


    public BlackProps() {
        arr = super.makeArr(MAX_ELEM);
    }


    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getSender() {
        return sender.get();
    }

    public StringProperty senderProperty() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender.set(sender);
    }

    public boolean isSenderExact() {
        return senderExact.get();
    }

    public BooleanProperty senderExactProperty() {
        return senderExact;
    }

    public void setSenderExact(boolean senderExact) {
        this.senderExact.set(senderExact);
    }

    public String getThema() {
        return thema.get();
    }

    public StringProperty themaProperty() {
        return thema;
    }

    public void setThema(String thema) {
        this.thema.set(thema);
    }

    public boolean isThemaExact() {
        return themaExact.get();
    }

    public BooleanProperty themaExactProperty() {
        return themaExact;
    }

    public void setThemaExact(boolean themaExact) {
        this.themaExact.set(themaExact);
    }

    public String getTitel() {
        return titel.get();
    }

    public StringProperty titelProperty() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel.set(titel);
    }

    public String getThemaTitel() {
        return themaTitel.get();
    }

    public StringProperty themaTitelProperty() {
        return themaTitel;
    }

    public void setThemaTitel(String themaTitel) {
        this.themaTitel.set(themaTitel);
    }

    public void setPropsFromXml() {
        setSender(arr[BLACKLIST_SENDER]);
        setSenderExact(arr[BLACKLIST_SENDER_EXAKT].isEmpty() ? true : Boolean.parseBoolean(arr[BLACKLIST_SENDER_EXAKT]));
        setThema(arr[BLACKLIST_THEMA]);
        setThemaExact(arr[BLACKLIST_THEMA_EXAKT].isEmpty() ? true : Boolean.parseBoolean(arr[BLACKLIST_THEMA_EXAKT]));
        setTitel(arr[BLACKLIST_TITEL]);
        setThemaTitel(arr[BLACKLIST_THEMA_TITEL]);
    }

    public void setXmlFromProps() {
        arr[BLACKLIST_NR] = getNr() + "";
        arr[BLACKLIST_SENDER] = getSender();
        arr[BLACKLIST_SENDER_EXAKT] = String.valueOf(isSenderExact());
        arr[BLACKLIST_THEMA] = getThema();
        arr[BLACKLIST_THEMA_EXAKT] = String.valueOf(isThemaExact());
        arr[BLACKLIST_TITEL] = getTitel();
        arr[BLACKLIST_THEMA_TITEL] = getThemaTitel();
    }
}
