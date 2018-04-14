/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

package de.mtplayer.mtp.controller.filmlist.writeFilmlist;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.controller.data.film.FilmlistXml;
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class WriteFilmlistJson {

    protected JsonGenerator getJsonGenerator(OutputStream os) throws IOException {
        JsonFactory jsonF = new JsonFactory();
        JsonGenerator jg = jsonF.createGenerator(os, JsonEncoding.UTF8);
        jg.useDefaultPrettyPrinter(); // enable indentation just to make debug/testing easier

        return jg;
    }

    public void write(String datei, Filmlist filmlist) {
        try {
            PLog.userLog("Filme schreiben (" + filmlist.size() + " Filme) :");

            PLog.userLog("   --> Start Schreiben nach: " + datei);
            String sender = "", thema = "";

            try (FileOutputStream fos = new FileOutputStream(datei);
                 JsonGenerator jg = getJsonGenerator(fos)) {

                jg.writeStartObject();
                // Infos zur Filmliste
                jg.writeArrayFieldStart(FilmlistXml.FILMLISTE);
                for (int i = 0; i < FilmlistXml.MAX_ELEM; ++i) {
                    jg.writeString(filmlist.metaDaten[i]);
                }
                jg.writeEndArray();
                // Infos der Felder in der Filmliste
                jg.writeArrayFieldStart(FilmlistXml.FILMLISTE);
                for (int i = 0; i < FilmXml.JSON_NAMES.length; ++i) {
                    jg.writeString(FilmXml.COLUMN_NAMES[FilmXml.JSON_NAMES[i]]);
                }
                jg.writeEndArray();
                //Filme schreiben
                for (Film datenFilm : filmlist) {
                    datenFilm.arr[FilmXml.FILM_NEU] = Boolean.toString(datenFilm.isNewFilm()); // damit wirs beim nächsten Programmstart noch wissen

                    jg.writeArrayFieldStart(FilmXml.TAG_JSON_LIST);
                    for (int i = 0; i < FilmXml.JSON_NAMES.length; ++i) {
                        int m = FilmXml.JSON_NAMES[i];
                        if (m == FilmXml.FILM_SENDER) {
                            if (datenFilm.arr[m].equals(sender)) {
                                jg.writeString("");
                            } else {
                                sender = datenFilm.arr[m];
                                jg.writeString(datenFilm.arr[m]);
                            }
                        } else if (m == FilmXml.FILM_THEMA) {
                            if (datenFilm.arr[m].equals(thema)) {
                                jg.writeString("");
                            } else {
                                thema = datenFilm.arr[m];
                                jg.writeString(datenFilm.arr[m]);
                            }
                        } else {
                            jg.writeString(datenFilm.arr[m]);
                        }
                    }
                    jg.writeEndArray();
                }
                jg.writeEndObject();
                PLog.userLog("   --> geschrieben!");
            }
        } catch (Exception ex) {
            PLog.errorLog(846930145, ex, "nach: " + datei);
        }
    }
}
