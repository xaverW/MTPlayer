package de.p2tools.mtplayer.controller.mediadb;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MediaDateReadWriteFactory {
    private MediaDateReadWriteFactory() {
    }

    public static List<MediaData> read() {
        List<MediaData> list = new ArrayList<>();
        try {
            final Path urlPath = FileFactory.getUrlFilePath(ProgInfos.getSettingsDirectory_String(), ProgConst.FILE_MEDIA_JSON);
            if (!urlPath.toFile().exists()) {
                return list;
            }

            JsonParser jsonParser = new JsonFactory().createParser(urlPath.toFile());
            while (jsonParser.nextToken() != null) {
                String name = jsonParser.currentName();

                if (MediaData.TAG.equals(name)) {
                    jsonParser.nextToken();
                    readJson(jsonParser, list);
                }
            }
            jsonParser.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    public static void write() {
        write(ProgData.getInstance().mediaDataList);
    }

    public static void write(List<MediaData> list) {
        try {
            final Path urlPath = FileFactory.getUrlFilePath(ProgInfos.getSettingsDirectory_String(), ProgConst.FILE_MEDIA_JSON);
            final JsonGenerator jsonGenerator = new JsonFactory()
                    .createGenerator(new FileOutputStream(urlPath.toFile()));
            jsonGenerator.useDefaultPrettyPrinter();
            jsonGenerator.writeStartObject(); // start root object

            // Memo
            for (MediaData mediaData : list) {
                if (mediaData.isExternal()) {
                    // nur externe
                    writeJson(mediaData, jsonGenerator);
                }
            }
            jsonGenerator.writeEndObject(); //end address object
            jsonGenerator.flush();
            jsonGenerator.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void updateOldMedia() {
        String settingsDir = ProgInfos.getSettingsDirectory_String();
        MediaDataList list = new MediaDataList();
        String fileNameTxt = ProgConst.FILE_MEDIA_DB;

        final Path xmlFilePath = Path.of(settingsDir, fileNameTxt);

        // Alte Liste laden
        try {
            if (!Files.exists(xmlFilePath) || xmlFilePath.toFile().length() == 0) {
                return;
            }

            list.addAll(ReadMediaDb.loadSavedExternalMediaData());

        } catch (final Exception ex) {
            P2Log.errorLog(656231547, ex.getMessage());
        }

        MediaDateReadWriteFactory.write(list); // schreiben

        // Und jetzt die alten noch löschen
        try {
            if (Files.exists(xmlFilePath)) {
                Files.delete(xmlFilePath);
            }
        } catch (Exception ex) {
            P2Log.errorLog(956231458, ex.getMessage());
        }
    }

    private static void writeJson(MediaData mediaData, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeObjectFieldStart(MediaData.TAG);
        //start address object
        jsonGenerator.writeStringField("name", mediaData.getName());
        jsonGenerator.writeStringField("path", mediaData.getPath());
        jsonGenerator.writeStringField("size", mediaData.getSize().getSizeAsStr());
        jsonGenerator.writeStringField("collectionId", mediaData.getCollectionId() + "");
        jsonGenerator.writeEndObject(); //end address object
    }

    private static void readJson(JsonParser jsonParser, List<MediaData> list) throws IOException {
        String name = "";
        String path = "";
        String size = "";
        String collectionId = "";

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.currentName();
            jsonParser.nextToken();
            if ("name".equals(key)) {
                name = jsonParser.getText();
            } else if ("path".equals(key)) {
                path = jsonParser.getText();
            } else if ("size".equals(key)) {
                size = jsonParser.getText();
            } else if ("collectionId".equals(key)) {
                collectionId = jsonParser.getText();
            }
        }

        MediaData mediaData = new MediaData(name, path, size, collectionId);
        list.add(mediaData);
    }
}
