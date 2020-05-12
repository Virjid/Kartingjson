package me.virjid.karting.json.entity;

import me.virjid.karting.json.annotation.TimeFormat;
import me.virjid.karting.json.annotation.Transient;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Virjid
 */
public class TestBlogAuthorEntity implements Serializable {
    private static final long serialVersionUID = -2103563206251211520L;

    private String username = "Virjid";

    @Transient
    private String password = "123456";

    private LocalDate birthday = LocalDate.of(1997, 6, 29);

    private LocalDate signOnDate = LocalDate.of(2020, 5, 10);

    private LocalTime signOnTime = LocalTime.of(10, 6, 50);

    @TimeFormat("yyyyMMddHHmmss")
    private LocalDateTime lastSignInAt = LocalDateTime.now();

    private List<String> friends = new ArrayList<>();

    {
        friends.add("Joker");
        friends.add("Jack");
        friends.add("Peter");
    }

    private LocalDateTime[] everySignInTime = new LocalDateTime[3];

    {
        everySignInTime[0] = LocalDateTime.now();
        everySignInTime[1] = LocalDateTime.now();
        everySignInTime[2] = LocalDateTime.now();
    }
}
