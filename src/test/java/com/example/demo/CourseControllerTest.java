package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.example.demo.CourseController.Course;
import com.example.demo.CourseController.CourseInput;

public class CourseControllerTest {

    private CourseController controller;

    @BeforeEach
    void setUp() {
        // Fresh controller for every test
        controller = new CourseController();
    }

    // Test 01 — Find an existing course
    @Test
    void test01_getExistingCourse() {
        ResponseEntity<Course> response = controller.getCourseById(101L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(101L, response.getBody().id());
        assertEquals("IS-401", response.getBody().code());
    }

    // Test 02 — Look up a missing course
    @Test
    void test02_getMissingCourse() {
        ResponseEntity<Course> response = controller.getCourseById(9999L);

        assertEquals(404, response.getStatusCode().value());
        assertEquals(null, response.getBody());
    }

    // Test 03 — Create a valid course
    @Test
    void test03_createValidCourse() {
        CourseInput input = new CourseInput("IS-404", "Software Testing", 3);

        ResponseEntity<?> response = controller.createCourse(input);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Course);

        Course created = (Course) response.getBody();
        assertEquals("IS-404", created.code());
        assertEquals(3, created.credits());
    }

    // Test 04 — Reject zero credits
    @Test
    void test04_rejectZeroCredits() {
        CourseInput input = new CourseInput("IS-404", "Software Testing", 0);

        ResponseEntity<?> response = controller.createCourse(input);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        String message = response.getBody().toString();
        assertTrue(message.toLowerCase().contains("credits"),
            "Expected message to mention credits, got: " + message);
    }

    // Test 05 — Delete an existing course
    @Test
    void test05_deleteExistingCourse() {
        ResponseEntity<Void> response = controller.deleteCourse(103L);

        assertEquals(204, response.getStatusCode().value());

        // Verify the course is gone
        ResponseEntity<Course> after = controller.getCourseById(103L);
        assertEquals(404, after.getStatusCode().value());
    }
}