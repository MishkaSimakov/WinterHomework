package com.example.winter_homework;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @ToString.Exclude
    Long id;

    String username;

    @ToString.Exclude
    String password;

    Integer age;

    public static User createFromJSON(JsonNode node) {
        User user = new User();

        user.setUsername(node.get("username").asText());
        user.setAge(node.get("age").asInt());
        user.setPassword(node.get("password").asText());

        return user;
    }

    public static Optional<Comparator<User>> getComparatorFor(String column, String direction) {
        Optional<Comparator<User>> comparator = Optional.empty();

        if (column.equals("username"))
            comparator = Optional.of(Comparator.comparing(User::getUsername));
        else if (column.equals("age"))
            comparator = Optional.of(Comparator.comparing(User::getAge));

        if (comparator.isPresent() && direction.equals("up"))
            comparator = Optional.of(comparator.get().reversed());

        return comparator;
    }
}
