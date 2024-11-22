package hk.polyu.comp.project2411.bms.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Utils {
    public static Timestamp parseTimestamp(Object dateTime) {
        if (dateTime == null) {
            return null;
        }
        if (dateTime instanceof Timestamp timestamp) {
            return timestamp;
        }
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime localDateTime = LocalDateTime.parse((String) dateTime, isoFormatter);
            return Timestamp.valueOf(localDateTime);
        } catch (Exception e1) {
            try {
                return Timestamp.valueOf((String) dateTime);
            } catch (Exception e2) {
                throw new IllegalArgumentException("Invalid dateTime format: " + dateTime, e2);
            }
        }
    }

    public static Map<String, Object> getLowerCasedMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        Map<String, Object> lowerCasedMap = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            lowerCasedMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return lowerCasedMap;
    }
}
