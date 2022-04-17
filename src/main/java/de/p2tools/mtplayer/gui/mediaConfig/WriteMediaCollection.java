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

package de.p2tools.mtplayer.gui.mediaConfig;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import de.p2tools.mtplayer.controller.mediaDb.MediaData;
import de.p2tools.mtplayer.controller.mediaDb.MediaDataList;
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class WriteMediaCollection {

    public void write(String file, MediaDataList dataList) {
        try {
            try (FileOutputStream fos = new FileOutputStream(file);
                 JsonGenerator jg = getJsonGenerator(fos)) {

                jg.writeStartObject();
                for (int i = 0; i < dataList.size(); ++i) {
                    jg.writeArrayFieldStart("Datei " + (i + 1));
                    MediaData md = dataList.get(i);
                    jg.writeString(md.getName());
                    jg.writeString(md.getPath());
                    jg.writeEndArray();
                }
            }
        } catch (Exception ex) {
            PLog.errorLog(945120987, ex, "nach: " + file);
        }
    }

    private JsonGenerator getJsonGenerator(OutputStream os) throws IOException {
        JsonFactory jsonF = new JsonFactory();
        JsonGenerator jg = jsonF.createGenerator(os, JsonEncoding.UTF8);
        jg.useDefaultPrettyPrinter(); // enable indentation just to make debug/testing easier
        return jg;
    }
}
