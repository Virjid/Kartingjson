package me.virjid.karting.json;

import me.virjid.karting.json.entity.TestBlogPostEntity;
import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.model.JSONObject;
import me.virjid.karting.json.util.JSON;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
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

    @BeforeAll
    public static void readData() {
        array_json  = readResource("array.json");
        object_json = readResource("object.json");
    }

    @Test
    public void objectToJSONObject() {
        TestBlogPostEntity post = new TestBlogPostEntity();
        JSONObject obj = JSON.toJSONObject(post);
        System.out.println(obj.toString(4));
    }

    @Test
    public void parseJSONObjectString() {
        JSONObject obj = JSON.parseJSONObject(object_json);
        Assert.assertSame(obj.toString(2), object_json);
    }

    @Test
    public void parseJSONArrayString() {
        JSONArray array = JSON.parseJSONArray(array_json);
        Assert.assertSame(array.toString(2), array_json);
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
