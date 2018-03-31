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

package de.mtplayer.mtp.controller.data.abo;

import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.tools.storedFilter.Filter;

public class Abo extends AboProps {
    public int nr;

    public Filter fSender = new Filter();
    public Filter fThema = new Filter();
    public Filter fThemaTitel = new Filter();
    public Filter fTitel = new Filter();
    public Filter fSomewhere = new Filter();


    public Abo() {
        // neue Abos sind immer ein
        setActive(true);
        setResolution(Film.AUFLOESUNG_NORMAL);
        initFilter();
    }

    public Abo(String name,
               String sender,
               String thema,
               String themaTitel,
               String titel,
               String irgendwo,
               int mmindestdauerMinuten,
               int mmaxdestdauerMinuten,
               String ziel,
               String pset) {

        // neue Abos sind immer ein
        setActive(true);
        setResolution(Film.AUFLOESUNG_NORMAL);
        initFilter();

        setName(name);

        setSender(sender);
        setTheme(thema);
        setThemeTitle(themaTitel);
        setTitle(titel);
        setSomewhere(irgendwo);

        setMin(mmindestdauerMinuten);
        setMax(mmaxdestdauerMinuten);

        setDest(ziel);
        setPset(pset);
    }

    private void initFilter() {
        senderProperty().addListener(l -> createFilter());
        senderExactProperty().addListener(l -> createFilter());
        themeProperty().addListener(l -> createFilter());
        themeExactProperty().addListener(l -> createFilter());

        themeTitleProperty().addListener(l -> createFilter());
        titleProperty().addListener(l -> createFilter());
        somewhereProperty().addListener(l -> createFilter());
    }

    private void createFilter() {
        fSender = new Filter(getSender(), isSenderExact());
        fSender.setArray();

        fThema = new Filter(getTheme(), isThemeExact());
        fThema.setArray();

        fThemaTitel = new Filter(getThemeTitle());
        fThemaTitel.setArray();

        fTitel = new Filter(getTitle());
        fTitel.setArray();

        fSomewhere = new Filter(getSomewhere());
        fSomewhere.setArray();
    }

    public boolean isEmpty() {
        // liefert TRUE wenn das Abo leer ist, also bei jedem Film ansprechen würde
        // ist dann offensichtlich falsch!!
        if (getSender().isEmpty()
                && getTheme().isEmpty()
                && getThemeTitle().isEmpty()
                && getTitle().isEmpty()
                && getSomewhere().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    public void aufMichKopieren(Abo abo) {
        for (int i = 0; i < AboXml.MAX_ELEM; ++i) {
            this.properties[i].setValue(abo.properties[i].getValue());
        }
        this.setXmlFromProps();
    }

    public Abo getCopy() {
        final Abo ret = new Abo();
        for (int i = 0; i < AboXml.MAX_ELEM; ++i) {
            ret.properties[i].setValue(this.properties[i].getValue());
        }
        ret.setXmlFromProps();
        return ret;
    }

}
