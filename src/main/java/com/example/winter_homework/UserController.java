package com.example.winter_homework;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/index")
    public ResponseEntity<String> userShow(
            @RequestParam Integer age,
            @RequestParam(name = "sort_by", required = false, defaultValue = "username") String sort_by,
            @RequestParam(name = "direction", required = false, defaultValue = "up") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") Long page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") Long per_page
    ) {
        Optional<Comparator<User>> comparator = User.getComparatorFor(sort_by, direction);

        if (comparator.isEmpty())
            return ResponseEntity.status(400).build();

        return ResponseEntity.accepted().body(
                userRepository.findByAgeBetween(age - 5, age + 5).stream()
                        .sorted(comparator.get())
                        .skip(page * per_page)
                        .limit(per_page)
                        .map(User::toString).collect(Collectors.joining(", "))
        );
    }

    @PostMapping("/create")
    public ResponseEntity<Void> userStore(@RequestBody JsonNode node) {
        if (userRepository.findByUsername(node.get("username").asText()).isPresent())
            return ResponseEntity.status(409).build();

        if (!Objects.equals(node.get("password").asText(), node.get("repeat_password").asText()))
            return ResponseEntity.status(400).build();

        userRepository.save(User.createFromJSON(node));

        return ResponseEntity.status(200).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> userShow(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty())
            return ResponseEntity.status(404).build();

        return ResponseEntity.accepted().body(
                user.get().toString()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> userDelete(@PathVariable Long id) {
        if (userRepository.findById(id).isEmpty())
            return ResponseEntity.status(404).build();

        userRepository.deleteById(id);

        return ResponseEntity.status(200).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> userUpdate(@PathVariable Long id, @RequestBody JsonNode node) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty())
            return ResponseEntity.status(404).build();

        if (!Objects.equals(node.get("password").asText(), node.get("repeat_password").asText()))
            return ResponseEntity.status(400).build();

        user.get().setUsername(node.get("username").asText());
        user.get().setAge(node.get("age").asInt());
        user.get().setPassword(node.get("password").asText());

        userRepository.save(user.get());

        return ResponseEntity.status(200).build();
    }
}
