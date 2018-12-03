package org.rit.swen440.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String readFileToString(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, Charset.defaultCharset());
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readJsonAsObject(String path, Class<T> type) {
        try {
            String jsonString = FileUtils.readFileToString(path);
            return OBJECT_MAPPER.readValue(jsonString, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void writeObjectToJsonFile(String path, T object) {
        try {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(path), object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
