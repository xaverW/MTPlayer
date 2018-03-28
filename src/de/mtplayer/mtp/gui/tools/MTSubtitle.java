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


package de.mtplayer.mtp.gui.tools;

import de.mtplayer.mLib.tools.FileUtils;
import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mLib.tools.SysMsg;
import de.mtplayer.mLib.tools.TimedTextMarkupLanguageParser;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.download.Download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class MTSubtitle {

    private static final int TIMEOUT = 10_000;
    private static final String SUFFIX_TTML = "ttml";
    private static final String SUFFIX_SRT = "srt";
    private static final String SRT_FILETYPE = ".srt";

    private InputStream getContentDecoder(final String encoding, InputStream in) throws IOException {
        if (encoding != null) {
            InputStream out = null;
            switch (encoding.toLowerCase()) {
                case "gzip":
                    out = new GZIPInputStream(in);
                    break;
                case "deflate":
                    out = new InflaterInputStream(in, new Inflater(true));
                    break;
            }
            return out;
        } else
            return in;
    }

    private void setupConnection(HttpURLConnection conn) {
        conn.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
    }

    private void downloadContent(InputStream in, String strSubtitelFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(strSubtitelFile)) {
            final byte[] buffer = new byte[65536];
            int n;
            while ((n = in.read(buffer)) != -1) {
                fos.write(buffer, 0, n);
            }
            SysMsg.sysMsg(new String[]{"Untertitel", "  geschrieben"});
        }
    }

    private void writeSrt(String strSubtitelFile, Download datenDownload) {
        final Path p = Paths.get(strSubtitelFile);
        final TimedTextMarkupLanguageParser ttmlp = new TimedTextMarkupLanguageParser();
        if (ttmlp.parse(p) || ttmlp.parseXmlFlash(p)) {
            final Path srt = Paths.get(datenDownload.getFileNameWithoutSuffix() + SRT_FILETYPE);
            ttmlp.toSrt(srt);
        }
        ttmlp.cleanup();
    }

    public void writeSubtitle(Download datenDownload) {
        String suffix;
        String urlSubtitle = "";
        InputStream in = null;

        if (datenDownload.getUrlSubtitle().isEmpty())
            return;

        try {
            SysMsg.sysMsg(new String[]{"Untertitel: ", datenDownload.getUrlSubtitle(),
                    "schreiben nach: ", datenDownload.getZielPfad()});

            urlSubtitle = datenDownload.getUrlSubtitle();
            suffix = FileUtils.getSuffixFromUrl(urlSubtitle);
            if (!suffix.endsWith(SUFFIX_SRT))
                suffix = SUFFIX_TTML;

            Files.createDirectories(Paths.get(datenDownload.getZielPfad()));

            final HttpURLConnection conn = (HttpURLConnection) new URL(urlSubtitle).openConnection();
            setupConnection(conn);
            if ((conn.getResponseCode()) < HttpURLConnection.HTTP_BAD_REQUEST) {
                in = getContentDecoder(conn.getContentEncoding(), conn.getInputStream());

                final String strSubtitelFile = datenDownload.getFileNameWithoutSuffix() + '.' + suffix;
                downloadContent(in, strSubtitelFile);

                if (!strSubtitelFile.endsWith(SRT_FILETYPE))
                    writeSrt(strSubtitelFile, datenDownload);
            } else
                Log.errorLog(752301248, "url: " + urlSubtitle);
        } catch (final Exception ignored) {
            Log.errorLog(461203210, ignored, "SubtitelUrl: " + urlSubtitle);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final Exception ignored) {
            }
        }
    }
}
