package me.virjid.karting.json.entity;

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

    private TestBlogAuthorEntity author = new TestBlogAuthorEntity();

    private int star = 9999;

    private LocalDateTime createAt = LocalDateTime.now();

    private LocalDateTime updateAt = LocalDateTime.now();
}
