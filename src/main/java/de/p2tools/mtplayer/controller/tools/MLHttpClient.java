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

package de.p2tools.mtplayer.controller.tools;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class MLHttpClient {
    private final static MLHttpClient ourInstance = new MLHttpClient();
    private final OkHttpClient httpClient;
    private final OkHttpClient copyClient;

    private MLHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(100, 1, TimeUnit.SECONDS))
                .build();
        httpClient.dispatcher().setMaxRequests(100);

        copyClient = httpClient.newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS).build();
    }

    public static MLHttpClient getInstance() {
        return ourInstance;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public OkHttpClient getReducedTimeOutClient() {
        return copyClient;
    }
}
