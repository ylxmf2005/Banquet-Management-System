package hk.polyu.comp.project2411.bms.model;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;

import oracle.sql.TIMESTAMP;

public class Utils {
    
    public static Timestamp parseTimestamp(Object dateTime) {
        if (dateTime == null) {
            return null;
        }
        if (dateTime instanceof Timestamp timestamp) {
            return timestamp;
        }
        if (dateTime instanceof TIMESTAMP oracleTimestamp) {
            try {
                return oracleTimestamp.timestampValue();
            } catch (SQLException e) {
                throw new RuntimeException("Error converting Oracle TIMESTAMP to Timestamp", e);
            }
        }
    
        String[] patterns = {
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss'.'SSSSSS",
            "yyyy-MM-dd HH:mm:ss'.'SSSSS",
            "MMM dd, yyyy, hh:mm:ss a",
            "yyyy-MM-dd",
            "MM/dd/yyyy HH:mm:ss",
            "MM/dd/yyyy hh:mm:ss a",
            "dd-MMM-yyyy HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss"
        };
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDateTime localDateTime = LocalDateTime.parse(dateTime.toString(), formatter);
                return Timestamp.valueOf(localDateTime);
            } catch (Exception ignored) {
            }
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime.toString(), 
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM));
            return Timestamp.valueOf(localDateTime);
        } catch (Exception ignored) {
        }

        try {
            return Timestamp.valueOf(dateTime.toString());
        } catch (Exception e) {
            System.out.println("Failed to parse datetime: " + dateTime);
            System.out.println("Object type: " + dateTime.getClass());
            throw new IllegalArgumentException("Invalid dateTime format: " + dateTime, e);
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

    public static void main(String[] args) {
        System.out.println(parseTimestamp("Nov 24, 2024, 12:21:29 AM"));
        System.out.println(parseTimestamp("2024-11-23 16:22:49.422392"));
        System.out.println(parseTimestamp("2024.11.23 16:22:49"));
    }
}

