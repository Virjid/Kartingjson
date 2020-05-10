package me.virjid.karting.json;

import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.util.JSON;

import java.io.*;
import java.net.URL;

/**
 * @author Virjid
 */
public class Test {
    public static void main(String[] args) throws IOException {
        String data = readSource("data.json");

        JSONArray array = JSON.parseJSONArray(data);
        System.out.println(array.toString(4));
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
