package me.virjid.karting.json.entity;

import java.util.List;

/**
 * @author Virjid
 */
public class TestSimpleEntity {
    private String hello;
    private Double number;
    private List<String> list;

    @Override
    public String toString() {
        return "TestSimpleEntity{" +
                "hello='" + hello + '\'' +
                ", number=" + number +
                '}';
    }
}
