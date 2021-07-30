/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.filmlist.checkFilmlistUpdate;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.tools.MLHttpClient;
import de.p2tools.p2Lib.tools.log.PLog;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

public class SearchUpdateWithId {

    /**
     * Check if a newer filmlist id is available on the remote server
     * "Filmliste" : [ "19.08.2018, 13:17", "19.08.2018, 11:17", "3", "MSearch [Vers.: 3.1.62]", "3ca1dcb332f78296ba002d8f28918205" ],
     *
     * @return true if newer is availble, otherwise false.
     */
    public boolean hasNewRemoteFilmlist() {
        boolean ret = false;
        final String id = ProgData.getInstance().filmlist.getFilmlistId();

        // todo url anpassen
        final Request.Builder builder = new Request.Builder().url("https://verteiler1.mediathekview.de/filmliste.id");
        builder.addHeader("User-Agent", ProgInfos.getUserAgent());

        try (Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
             ResponseBody body = response.body()) {

            if (body != null && response.isSuccessful()) {
                String remoteId = body.string();
//                System.out.println(remoteId);
                if (!remoteId.isEmpty() && !remoteId.equalsIgnoreCase(id)) {
                    // Filmliste hat sich geändert
                    ret = true;
                }
            } else {
                // dann hat er die URL nicht gefunden??
                if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    ret = true;
                }
            }


        } catch (UnknownHostException ex) {
            ret = true; //Netzwerkfehler
        } catch (IOException ex) {
            ret = true; //Netzwerkfehler
        } catch (Exception ex) {
            PLog.errorLog(912364788, ex);
        }

        return ret;
    }
}
