package me.virjid.karting.json;

import me.virjid.karting.json.entity.TestBlogPostEntity;
import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.model.JSONObject;
import me.virjid.karting.json.util.JSON;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;

/**
 * @author Virjid
 */
public class KartingjsonTest {
    private static String array_json;
    private static String object_json;
    private static String revise_json;
    private static String unicode_json;

    @BeforeAll
    public static void readData() {
        array_json   = readResource("array.json");
        object_json  = readResource("object.json");
        revise_json  = readResource("revise.json");
        unicode_json = readResource("unicode.json");
    }

    // 简单地修正JSON字符串
    @Test
    public void reviseJson() {
        JSONObject obj = JSON.parseJSONObject(revise_json);

        System.out.println(obj.toString(2));
    }


    // 解析JSON对象字符串
    @Test
    public void parseJSONObjectString() {
        JSONObject obj = JSON.parseJSONObject(object_json);
        System.out.println(obj.toString(2));
    }

    // 解析JSON数组字符串
    @Test
    public void parseJSONArrayString() {
        JSONArray array = JSON.parseJSONArray(array_json);
        System.out.println(array.toString(2));
    }

    // Object对象转JSONObject对象
    @Test
    public void objectToJSONObject() {
        TestBlogPostEntity post = new TestBlogPostEntity();
        JSONObject obj = JSON.toJSONObject(post);
        System.out.println(obj.toString(2));
    }

    // unicode编码转义测试
    @Test
    public void unicodeTest() {
        JSONObject obj = JSON.parseJSONObject(unicode_json);
        System.out.println(obj.getString("message"));
        System.out.println(obj.toString(2));
    }


    @NotNull
    private static String readResource(String name) {
        URL url = KartingjsonTest.class.getClassLoader().getResource(name);
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
