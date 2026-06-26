package de.p2tools.mtplayer.controller.data.bookmark;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.tools.FileFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class BookmarkReadWriteJsonFactory {
    private BookmarkReadWriteJsonFactory() {
    }

    public static void read() {
        try {
            final Path urlPath = FileFactory
                    .getUrlFilePath(ProgInfos.getSettingsDirectory_String(), ProgConst.FILE_BOOKMARKS_JSON);
            if (!urlPath.toFile().exists()) {
                return;
            }

            JsonParser jsonParser = new JsonFactory().createParser(urlPath.toFile());
            while (jsonParser.nextToken() != null) {
                String name = jsonParser.currentName();

                if (BookmarkData.TAG.equals(name)) {
                    jsonParser.nextToken();
                    readJson(jsonParser);
                }
            }
            jsonParser.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void write() {
        write(ProgData.getInstance().bookmarkList);
    }

    public static void write(List<BookmarkData> list) {
        try {
            final Path urlPath = FileFactory
                    .getUrlFilePath(ProgInfos.getSettingsDirectory_String(), ProgConst.FILE_BOOKMARKS_JSON);
            final JsonGenerator jsonGenerator = new JsonFactory()
                    .createGenerator(new FileOutputStream(urlPath.toFile()));
            jsonGenerator.useDefaultPrettyPrinter();
            jsonGenerator.writeStartObject(); // start root object

            // Memo
            for (BookmarkData bookmarkData : list) {
                writeJson(bookmarkData, jsonGenerator);
            }
            jsonGenerator.writeEndObject(); //end address object
            jsonGenerator.flush();
            jsonGenerator.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private static void writeJson(BookmarkData bookmarkData, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeObjectFieldStart(BookmarkData.TAG);
        //start address object
        jsonGenerator.writeBooleanField("audio", bookmarkData.isAudio());
        jsonGenerator.writeStringField("channel", bookmarkData.getChannel());
        jsonGenerator.writeStringField("theme", bookmarkData.getTheme());
        jsonGenerator.writeStringField("title", bookmarkData.getTitle());
        jsonGenerator.writeStringField("url", bookmarkData.getUrl());
        jsonGenerator.writeStringField("info", bookmarkData.getInfo());
        jsonGenerator.writeStringField("date", bookmarkData.getDate().toString());

        jsonGenerator.writeEndObject(); //end address object
    }

    private static void readJson(JsonParser jsonParser) throws IOException {
        boolean audio = false;
        String channel = "";
        String theme = "";
        String title = "";
        String url = "";
        String info = "";
        String date = "";

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.currentName();
            jsonParser.nextToken();
            if ("audio".equals(key)) {
                audio = jsonParser.getBooleanValue();
            } else if ("channel".equals(key)) {
                channel = jsonParser.getText();
            } else if ("theme".equals(key)) {
                theme = jsonParser.getText();
            } else if ("title".equals(key)) {
                title = jsonParser.getText();
            } else if ("url".equals(key)) {
                url = jsonParser.getText();
            } else if ("info".equals(key)) {
                info = jsonParser.getText();
            } else if ("date".equals(key)) {
                date = jsonParser.getText();
            }
        }

        BookmarkData bookmarkData = new BookmarkData(audio, channel, theme, title, url, info, date);
        ProgData.getInstance().bookmarkList.add(bookmarkData);
    }
}
