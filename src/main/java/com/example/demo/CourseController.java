package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses")
@CrossOrigin(origins = "http://localhost:5173")
public class CourseController {

    public record Course(long id, String code, String title, int credits) {}
    public record CourseInput(String code, String title, int credits) {}

    private final List<Course> courses = new ArrayList<>(List.of(
        new Course(101L, "IS-401", "Advanced Enterprise Web", 3),
        new Course(102L, "DB-201", "Database Systems", 3),
        new Course(103L, "SE-202", "Software Engineering", 3)
    ));

    private long nextId = 104L;

    @GetMapping
    public List<Course> getCourses() {
        return courses;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable long id) {
        return courses.stream()
            .filter(c -> c.id() == id)
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseInput input) {
        if (input.code() == null || input.code().isBlank()) {
            return ResponseEntity.badRequest().body("code is required");
        }
        if (input.title() == null || input.title().isBlank()) {
            return ResponseEntity.badRequest().body("title is required");
        }
        if (input.credits() <= 0) {
            return ResponseEntity.badRequest()
                .body("credits must be positive");
        }

        Course created = new Course(
            nextId++,
            input.code().trim(),
            input.title().trim(),
            input.credits()
        );
        courses.add(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(
            @PathVariable long id,
            @RequestBody CourseInput req) {

        if (req.code() == null || req.code().isBlank()) {
            return ResponseEntity.badRequest().body("code is required");
        }
        if (req.title() == null || req.title().isBlank()) {
            return ResponseEntity.badRequest().body("title is required");
        }
        if (req.credits() <= 0) {
            return ResponseEntity.badRequest()
                .body("credits must be positive");
        }

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).id() == id) {
                Course updated = new Course(id, req.code().trim(),
                    req.title().trim(), req.credits());
                courses.set(i, updated);
                return ResponseEntity.ok(updated);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable long id) {
        Optional<Course> found = courses.stream()
            .filter(c -> c.id() == id)
            .findFirst();
        if (found.isPresent()) {
            courses.remove(found.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}