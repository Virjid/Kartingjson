package me.virjid.karting.json.entity;

import me.virjid.karting.json.annotation.Property;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Virjid
 */
public class TestBlogPostEntity implements Serializable {
    private static final long serialVersionUID = -2317346037690341651L;

    private String no = UUID.randomUUID().toString();

    private String title = "Hello world";

    @Property("author_entity")
    private TestBlogAuthorEntity author = new TestBlogAuthorEntity();

    @Property("starts")
    private int star = 9999;

    private LocalDateTime createAt = LocalDateTime.now();

    @Property("update_at")
    private LocalDateTime updateAt = LocalDateTime.now();
}
