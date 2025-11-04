package com.bizmate.salesPages.util;

import com.opencsv.CSVReaderHeaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

final class Csv {
    static List<Map<String,String>> read(ResourceLoader loader, String location) {
        try {
            Resource r = loader.getResource(location);
            if (!r.exists()) return List.of();

            try (var is = r.getInputStream();
                 var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                // ✅ BOM 제거 (가끔 남아 있음)
                reader.mark(1);
                if (reader.read() != 0xFEFF) reader.reset();

                // ✅ OpenCSV 파서 시작
                try (var csv = new CSVReaderHeaderAware(reader)) {
                    List<Map<String,String>> rows = new ArrayList<>();
                    Map<String,String> row;
                    while ((row = csv.readMap()) != null) {
                        // ✅ 완전히 빈 줄은 스킵
                        if (row.values().stream().allMatch(v -> v == null || v.isBlank())) continue;
                        rows.add(trim(row));
                    }
                    return rows;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV read error: " + location, e);
        }
    }

    private static Map<String,String> trim(Map<String,String> src) {
        Map<String,String> m = new LinkedHashMap<>();
        src.forEach((k,v) -> m.put(k == null ? null : k.trim(), v == null ? null : v.trim()));
        return m;
    }

    static String s(Map<String,String> m, String key) { return Optional.ofNullable(m.get(key)).orElse(null); }
    static Boolean b(Map<String,String> m, String k) { var v=s(m,k); return v==null?null:Boolean.valueOf(v); }
    static LocalDate d(Map<String,String> m, String k) { var v=s(m,k); return v==null?null:LocalDate.parse(v); }
    static BigDecimal bd(Map<String,String> m, String k) { var v=s(m,k); return v==null?null:new BigDecimal(v); }
    static Long l(Map<String,String> m,String k){ var v=s(m,k); return v==null?null:Long.valueOf(v); }

    private Csv() {}
}
