package com.example.jsondemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SpringBootApplication
public class JsondemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsondemoApplication.class, args);
	}

	@RestController
	public static class ApiController {
		private final String DATA_DIR = "data/input/users.json";
		private final ObjectMapper objectMapper = new ObjectMapper();

		@GetMapping("/api/users")
		public ResponseEntity<Object> getUsers(
				@RequestParam(value = "page", defaultValue = "0") int page,
				@RequestParam(value = "size", defaultValue = "10") int size,
				@RequestParam(value = "sort", required = false) String sort) {
			try {
				ClassPathResource resource = new ClassPathResource(DATA_DIR);
				List<User> users = objectMapper.readValue(
						resource.getInputStream(),
						new TypeReference<List<User>>() {

						});

				List<User> sortedUsers = users.stream()
						.skip(page * size)
						.limit(size)
						.collect(Collectors.toList());
				if (sort != null && !sort.trim().isEmpty()) {
					sortedUsers = sortedUsers.stream()
							.sorted(getComparator(sort))
							.collect(Collectors.toList());
				}
				System.out.println(sortedUsers);
				return ResponseEntity.ok(sortedUsers);
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}

		private Comparator<User> getComparator(String sort) {
			switch (sort) {
				case "username":
					return Comparator.comparing(User::getUsername);
				case "user_id":
					return Comparator.comparingLong(User::getUser_id);
				case "post_count":
					return Comparator.comparingInt(User::getPost_count);
				default:
					return Comparator.comparing(User::getUsername);
			}
		}
	}
}