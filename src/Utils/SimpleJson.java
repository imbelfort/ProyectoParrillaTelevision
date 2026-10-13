package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleJson {

    public static String getString(String json, String key) {
        if (json == null || key == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public static String getNumberAsString(String json, String key) {
        if (json == null || key == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public static int getInt(String json, String key, int defaultValue) {
        String value = getNumberAsString(json, key);

        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.split("\\.")[0]);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    public static String getObjectBlock(String json, String key) {
        if (json == null || key == null) {
            return null;
        }

        String pattern = "\"" + key + "\"";
        int startKey = json.indexOf(pattern);

        if (startKey < 0) {
            return null;
        }

        int startObj = json.indexOf("{", startKey);

        if (startObj < 0) {
            return null;
        }

        int depth = 0;

        for (int i = startObj; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;

                if (depth == 0) {
                    return json.substring(startObj, i + 1);
                }
            }
        }

        return null;
    }
}