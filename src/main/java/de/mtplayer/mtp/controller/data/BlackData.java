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

import de.mtplayer.mtp.tools.storedFilter.Filter;

public class BlackData extends BlackProps {


    public Filter fChannel = new Filter();
    public Filter fTheme = new Filter();
    public Filter fThemeTitle = new Filter();
    public Filter fTitle = new Filter();
    public Filter fSomewhere = new Filter();


    public BlackData() {
        super();
        initFilter();
    }

    public BlackData(String sender, String theme, String title, String themeTitle) {
        super();
        initFilter();

        setChannel(sender);
        setTheme(theme);
        setTitle(title);
        setThemeTitle(themeTitle);
    }

    public void createFilter() {
        fChannel.filter = getChannel();
        fChannel.exact = false;
        fChannel.makeFilterArray();

        fTheme.filter = getTheme();
        fTheme.exact = getThemeExact();
        fTheme.makeFilterArray();

        fThemeTitle.filter = getThemeTitle();
        fThemeTitle.makeFilterArray();

        fTitle.filter = getTitle();
        fTitle.makeFilterArray();
    }

    private void initFilter() {
        channelProperty().addListener(l -> createFilter());

        themeProperty().addListener(l -> createFilter());
        themeExactProperty().addListener(l -> createFilter());

        themeTitleProperty().addListener(l -> createFilter());
        titleProperty().addListener(l -> createFilter());
    }


}
