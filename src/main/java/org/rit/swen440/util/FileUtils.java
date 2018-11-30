package org.rit.swen440.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper();

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
            return objectMapper.readValue(jsonString, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
