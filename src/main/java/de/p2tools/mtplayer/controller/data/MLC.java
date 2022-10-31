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


package de.p2tools.mtplayer.controller.data;

import de.p2tools.p2Lib.mtFilm.tools.MLConfigs;
import de.p2tools.p2Lib.tools.PColorFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class MLC {

    private final double DIV = 0.4;
    private final double DIV_DARK = 0.6;

    private String cssFontBold = "";
    private String cssFont = "";
    private String cssBackground = "";
    private String cssBackgroundSel = "";

    private MLConfigs mlConfigs;
    private MLConfigs mlConfigsDark;
    private boolean dark = false;
    private final String text;
    private final Color resetColor;
    private final Color resetColorDark;
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color", Color.WHITE);

    public MLC(MLConfigs mlConfigs, Color color, MLConfigs mlConfigsDark, Color colorDark, String text) {
        this.mlConfigs = mlConfigs;
        this.mlConfigsDark = mlConfigsDark;
        this.resetColor = color;
        this.resetColorDark = colorDark;
        this.text = text;
        setColorTheme(dark);
    }

    public void setColorTheme(boolean dark) {
        this.dark = dark;
        if ((dark ? mlConfigsDark : mlConfigs).get().isEmpty()) {
            // dann sinds noch die Org-Farben
            changeMyColor(dark ? resetColorDark : resetColor);
            return;
        }

        try {
            changeMyColor(Color.web((dark ? mlConfigsDark : mlConfigs).get()));
        } catch (final Exception ignored) {
            resetColor();
        }
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color newColor) {
        // Farbe setzen
        changeMyColor(newColor);
        // und sichern
        (dark ? mlConfigsDark : mlConfigs).setValue(getColorToHex());
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public String getText() {
        return text;
    }

    public Color getResetColor() {
        return (dark ? resetColorDark : resetColor);
    }

    public void resetColor() {
        // set reset color
        changeMyColor(dark ? resetColorDark : resetColor);
        // clear saved color
        (dark ? mlConfigsDark : mlConfigs).setValue("");
    }

    // ============================================
    // sind die CSS Farben
    // ============================================

    public String getCssBackground() {
        return cssBackground;
    }

    public String getCssBackgroundSel() {
        return cssBackgroundSel;
    }

    public String getCssFont() {
        return cssFont;
    }

    public String getCssFontBold() {
        return cssFontBold;
    }

    public String getDarkerColorToWeb() {
        return "#" + PColorFactory.getColorToHex(dark ? getBrighterColor(color.getValue()) : getDarkerColor(color.getValue()));
    }

    public String getColorToWeb() {
        return "#" + PColorFactory.getColorToHex(color.getValue());
    }

    private String getColorToHex() {
        return PColorFactory.getColorToHex(color.getValue());
    }

    private void changeMyColor(Color newColor) {
        // set the color
        color.set(newColor);

        // build the css for the color
        cssFontBold = ("-fx-font-weight: bold; -fx-text-fill: " + getColorToWeb() + ";").intern();
        cssFont = ("-fx-text-fill: " + getColorToWeb() + ";").intern();
        cssBackground = ("-fx-control-inner-background: " + getColorToWeb() + ";").intern();
        cssBackgroundSel = ("-fx-control-inner-background: " + getColorToWeb() + ";" +
                "-fx-selection-bar: " + getDarkerColorToWeb() + ";" +
                " -fx-selection-bar-non-focused: " + getDarkerColorToWeb() + ";").intern();
    }

    private Color getDarkerColor(Color color) {
        Color c;
        double min = color.getRed() < color.getGreen() ? color.getRed() : color.getGreen();
        min = min < color.getBlue() ? min : color.getBlue();

        min = min < DIV ? min : DIV;
        double red;
        double green;
        double blue;
        double change = 0.99 * min;

        if (change > 0.2) {
            red = color.getRed() - change;
            green = color.getGreen() - change;
            blue = color.getBlue() - change;

        } else {
            // da ändert sich dann auch der Farbton
            red = color.getRed() > DIV ? (color.getRed() - DIV) : 0.0;
            green = color.getGreen() > DIV ? (color.getGreen() - DIV) : 0.0;
            blue = color.getBlue() > DIV ? (color.getBlue() - DIV) : 0.0;
        }
        c = new Color(red, green, blue, color.getOpacity());
        return c;
    }

    private Color getBrighterColor(Color color) {
        Color c;
        double max = color.getRed() > color.getGreen() ? color.getRed() : color.getGreen();
        max = max > color.getBlue() ? max : color.getBlue();

        max = max > DIV_DARK ? max : DIV_DARK;
        double red;
        double green;
        double blue;

        double change = 1.0 - max;
        change = 0.99 * change;

        if (change > 0.2) {
            red = color.getRed() + change;
            green = color.getGreen() + change;
            blue = color.getBlue() + change;

        } else {
            // da ändert sich dann auch der Farbton
            red = color.getRed() < DIV_DARK ? (color.getRed() + DIV) : 1;
            green = color.getGreen() < DIV_DARK ? (color.getGreen() + DIV) : 1;
            blue = color.getBlue() < DIV_DARK ? (color.getBlue() + DIV) : 1;
        }
        c = new Color(red, green, blue, color.getOpacity());
        return c;
    }

}
