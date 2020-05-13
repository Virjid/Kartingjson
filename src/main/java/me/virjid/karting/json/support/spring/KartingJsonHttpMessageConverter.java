package me.virjid.karting.json.support.spring;

import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.util.JSON;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Virjid
 */
public class KartingJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public KartingJsonHttpMessageConverter() {
        super(new MediaType("application", "json", StandardCharsets.UTF_8));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> type, @NotNull HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream in = httpInputMessage.getBody();
        byte[] buffer = new byte[in.available()];
        in.read(buffer);

        String json = new String(buffer, StandardCharsets.UTF_8);

        if (type == JSONArray.class) {
            return JSON.parseJSONArray(json);
        } else {
            return JSON.parseJSONObject(json);
        }
    }

    @Override
    protected void writeInternal(Object o, @NotNull HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        String json = JSON.toJSONObject(o).toString();
        httpOutputMessage.getBody().write(json.getBytes());
        httpOutputMessage.getBody().flush();
        httpOutputMessage.getBody().close();
    }
}
