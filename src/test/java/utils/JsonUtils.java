package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode loadJson(String resourcePath) {
        try (InputStream is = JsonUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("JSON resource not found on classpath: " + resourcePath);
            }
            return MAPPER.readTree(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON resource: " + resourcePath, e);
        }
    }

    public static Object[][] toDataProviderArray(JsonNode arrayNode, String... fields) {
        List<Object[]> rows = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                JsonNode fieldNode = item.get(fields[i]);
                row[i] = (fieldNode != null && !fieldNode.isNull()) ? fieldNode.asText() : "";
            }
            rows.add(row);
        }
        return rows.toArray(new Object[0][]);
    }
}
