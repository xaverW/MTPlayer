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

package de.p2tools.mtplayer.controller.filmlist.writeFilmlist;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.data.film.FilmDataXml;
import de.p2tools.mtplayer.controller.data.film.Filmlist;
import de.p2tools.mtplayer.controller.data.film.FilmlistXml;
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

    public void write(String file, Filmlist filmlist) {
        try {
            String sender = "", theme = "";

            try (FileOutputStream fos = new FileOutputStream(file);
                 JsonGenerator jg = getJsonGenerator(fos)) {

                jg.writeStartObject();
                // Infos zur Filmliste
                jg.writeArrayFieldStart(FilmlistXml.FILMLIST);
                for (int i = 0; i < FilmlistXml.MAX_ELEM; ++i) {
                    jg.writeString(filmlist.metaData[i]);
                }
                jg.writeEndArray();

                // Infos der Felder in der Filmliste
                jg.writeArrayFieldStart(FilmlistXml.FILMLIST);
                for (int i = 0; i < FilmDataXml.JSON_NAMES.length; ++i) {
                    jg.writeString(FilmDataXml.COLUMN_NAMES[FilmDataXml.JSON_NAMES[i]]);
                }
                jg.writeEndArray();

                //Filme schreiben
                for (FilmData film : filmlist) {
                    film.arr[FilmDataXml.FILM_NEW] = Boolean.toString(film.isNewFilm()); // damit wirs beim nächsten Programmstart noch wissen

                    jg.writeArrayFieldStart(FilmDataXml.TAG_JSON_LIST);
                    for (int i = 0; i < FilmDataXml.JSON_NAMES.length; ++i) {
                        int m = FilmDataXml.JSON_NAMES[i];
                        if (m == FilmDataXml.FILM_CHANNEL) {
                            if (film.arr[m].equals(sender)) {
                                jg.writeString("");
                            } else {
                                sender = film.arr[m];
                                jg.writeString(film.arr[m]);
                            }
                        } else if (m == FilmDataXml.FILM_THEME) {
                            if (film.arr[m].equals(theme)) {
                                jg.writeString("");
                            } else {
                                theme = film.arr[m];
                                jg.writeString(film.arr[m]);
                            }
                        } else {
                            jg.writeString(film.arr[m]);
                        }
                    }
                    jg.writeEndArray();
                }
                jg.writeEndObject();
            }
        } catch (Exception ex) {
            PLog.errorLog(846930145, ex, "nach: " + file);
        }
    }
}
