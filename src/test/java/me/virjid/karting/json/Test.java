package me.virjid.karting.json;

import me.virjid.karting.json.annotation.TimeFormat;
import me.virjid.karting.json.annotation.Transient;
import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.model.JSONObject;
import me.virjid.karting.json.util.JSON;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Virjid
 */
public class Test {
    static class Entity implements Serializable {
        private static final long serialVersionUID = 72560647186584409L;
        private transient String hello = "k";
        @TimeFormat("HHmmss")
        private LocalTime now = LocalTime.now();
        private Map<String, Object> map = new HashMap<>();

        {
            map.put("4", .6);
            map.put("ad", LocalDateTime.now());
            map.put("ere", 5.6);
        }
    }

    public static void main(String[] args) throws IOException {
        String data = readSource("data.json");

        JSONObject model = JSON.toJSONObject(new Entity());
        System.out.println(model.toString(2));
    }

    private static String readSource(String name) {
        URL url = Test.class.getClassLoader().getResource("data.json");
        assert url != null;
        File file = new File(url.getPath());
        StringBuilder data = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            reader.lines().forEach(data::append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
