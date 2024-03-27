package de.p2tools.mtplayer.controller.livesearch;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class FileSize {

    private FileSize() {
    }

    public static String laengeString(String url) {
        String groesseStr = "";
        long l = getFileSizeFromUrl(url);
        if (l > 1000000L) {
            groesseStr = String.valueOf(l / 1000000L);
        } else if (l > 0L) {
            groesseStr = "1";
        }

        return groesseStr;
    }

    private static long getFileSizeFromUrl(String url) {
        if (!url.toLowerCase().startsWith("http")) {
            return -1L;

        } else {
            Request request = (new Request.Builder()).url(url).head().build();
            long respLength = -1L;
            try {
                Response response = MVHttpClient.getInstance().getReducedTimeOutClient().newCall(request).execute();
                try {
                    if (response.isSuccessful()) {
                        respLength = Long.parseLong(response.header("Content-Length", "-1"));
                    }
                } catch (Throwable var8) {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (Throwable var7) {
                            var8.addSuppressed(var7);
                        }
                    }
                    throw var8;
                }

                if (response != null) {
                    response.close();
                }
            } catch (NumberFormatException | IOException var9) {
                respLength = -1L;
            }

            if (respLength < 1000000L) {
                respLength = -1L;
            }

            return respLength;
        }
    }
}
