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


package de.p2tools.mtplayer.controller.data.cleaningdata;

public class CleaningFactory {

    public static String[] REPLACE_LIST = {
            ",", "·", ".", ";", "-", "–", "˗", "*", "#", "@",
            "/", ":", "&", "!", "?", "°", "=", "\"", "|"
    };

    public static String[] CLEAN_LIST = {
            "am", "auf", "aus", "aber", "alles", "als",
            "beim", "bis", "besser",
            "da", "du", "der", "die", "des", "das", "den", "dem", "direkt", "durch", "dich", "dein", "deine", "deinem",
            "er", "es", "einen", "eine", "ein", "extra",
            "für",
            "gibt", "groß", "große", "großen", "großem", "geht", "ganz", "gegen",
            "ist", "in", "ich", "im", "ihr", "ihre", "ins",
            "kann", "keine", "kommt", "klar",
            "mit", "mehr", "meine", "meinen", "meinem", "man",
            "nie", "nach", "neu", "neue", "neues",
            "ohne", "oder",
            "richtig", "richtige",
            "sie", "sind",
            "tag", "the",
            "und", "über", "uns", "unter", "unters", "unseren", "unser",
            "vom", "von", "vor", "viele", "viel",
            "was", "wie", "wir", "woche", "wenn", "will", "wollen", "woran", "warum", "war", "wer",
            "zur", "zum",

            /*Sender*/
            "livestream", "3sat", "sat", "ard",
            "arte.de", "arte.en", "arte.es", "arte.fr", "arte.it", "arte.pl",
            "kika", "mdr", "ndr", "orf", "phoenix",
            "rbb", "radio bremen tv",
            "rbtv", "srf", "swr", "wdr", "zdf", "zdf-tivi",
            /*Sender*/
    };


    private CleaningFactory() {
    }
}
