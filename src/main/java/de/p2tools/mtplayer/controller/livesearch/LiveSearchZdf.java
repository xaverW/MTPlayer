package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.atdata.AudioFactory;
import de.p2tools.p2lib.mtdownload.MLHttpClient;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class LiveSearchZdf {

    public LiveSearchZdf() {
    }

    public static List<FilmDataMTP> loadLive(JsonInfoDto jsonInfoDto) {
        jsonInfoDto.setStartUrl("https://www.zdf.de");

        int max = 0;
        try {
            final Request.Builder builder = new Request.Builder().url(jsonInfoDto.getStartUrl());
            builder.addHeader("User-Agent", ProgInfos.getUserAgent());
            Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
            ResponseBody body = response.body();

            String api = "";
            if (body != null && response.isSuccessful()) {
                InputStream input = body.byteStream();
                InputStream is = AudioFactory.selectDecompressor(jsonInfoDto.getStartUrl(), input);
                boolean b1 = false, b2 = false;
                try (LineNumberReader in = new LineNumberReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        // <script>
                        // window.zdfsite = {
                        // apiToken: '5bb200097db507149612d7d983131d06c79706d5',
                        if (line.contains("<script>")) {
                            b1 = true;
                        }
                        if (b1 && line.contains("window.zdfsite")) {
                            b2 = true;
                        }
                        if (b2 && line.contains("apiToken:")) {
                            api = line.substring(line.indexOf("'"), line.lastIndexOf("'"));
                            break;
                        }
                    }

//                    get("https://api.zdf.de/search/typeahead?context=user&q=m%C3%BCnchen&abName=ab-2024-03-18&abGroup=gruppe-a");
                    get("https://api.zdf.de/content/documents/zdf/br/landgasthaeuser/page-video-ard-muenchen-100.json?profile=player-3");
                } catch (final Exception ex) {
                    PLog.errorLog(926362547, ex);
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(979858978, ex, "Url: " + jsonInfoDto.getStartUrl());
        }
        Platform.runLater(() -> jsonInfoDto.getProgressProperty().setValue(JsonInfoDto.PROGRESS_NULL));
        System.out.println("Filme gefunden: " + max);
        return jsonInfoDto.getList();
    }


    private static void get(String getUrl) {
        try {
            final Request.Builder builder = new Request.Builder().url(getUrl);

            builder.addHeader("Accept", "*/*");
//            builder.addHeader("Accept-Encoding", "gzip, deflate, br");
            builder.addHeader("Accept-Language", "de-DE,en-US;q=0.7,en;q=0.3");
            builder.addHeader("Api-Auth", "Bearer 5bb200097db507149612d7d983131d06c79706d5");
            builder.addHeader("Cache-Control", "no-cache");
            builder.addHeader("Connection", "keep-alive");
            builder.addHeader("DNT", "1");
            builder.addHeader("Host", "api.zdf.de");
            builder.addHeader("Origin", "https://www.zdf.de");
            builder.addHeader("Pragma", "no-cache");
            builder.addHeader("Referer", "https://www.zdf.de/");
            builder.addHeader("Sec-Fetch-Dest", "empty");
            builder.addHeader("Sec-Fetch-Mode", "cors");
            builder.addHeader("Sec-Fetch-Site", "same-site");
            builder.addHeader("Sec-GPC", "1");
            builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:123.0) Gecko/20100101 Firefox/123.0");


            Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
            ResponseBody body = response.body();


            if (body != null && response.isSuccessful()) {
                FileOutputStream fos = new FileOutputStream("/tmp/usb/url.txt");
                DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));

                InputStream input = body.byteStream();
                InputStream is = AudioFactory.selectDecompressor(getUrl, input);
                try (LineNumberReader in = new LineNumberReader(new InputStreamReader(is))) {

                    String line;
                    while ((line = in.readLine()) != null) {
                        outStream.writeUTF(line);
                        System.out.println(line);
                    }

                    outStream.close();
                } catch (final Exception ex) {
                    PLog.errorLog(926362547, ex);
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(979858978, ex, "Url: " + getUrl);
        }
    }

    private String getUrl(String url) throws IOException, InterruptedException {
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder(uri).build();
        String content = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
        return content;
    }
}
