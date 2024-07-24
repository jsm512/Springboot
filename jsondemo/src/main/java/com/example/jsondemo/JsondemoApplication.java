package com.example.jsondemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class JsondemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsondemoApplication.class, args);
	}

	@RestController
	public static class ApiController {

		@PostMapping("/process-data")
		public ResponseEntity<List<Map<String, Object>>> processData(@RequestBody List<Map<String, Object>> data) {
			try {
				List<Map<String, Object>> transformedData = data.stream()
						.filter(entry -> {
							Object ageObj = entry.get("age");
							int age = ((Number) ageObj).intValue();
							return age >= 30 ? true : false;
						})
						.map(entry -> {
							Map<String, Object> result = entry.entrySet().stream()
									.filter(e -> !e.getKey().equals("age") && !e.getKey().equals("email"))
									.collect(Collectors.toMap(
											e -> {
												switch (e.getKey()) {
													case "name":
														return "fullName";
													case "occupation":
														return "job";
													case "location":
														return "city";
													default:
														return e.getKey();
												}
											},
											Map.Entry::getValue,
											(oldValue, newValue) -> newValue,
											LinkedHashMap::new));

							// Ensure the order of fields
							Map<String, Object> orderedResult = new LinkedHashMap<>();
							orderedResult.put("fullName", result.get("fullName"));
							orderedResult.put("job", result.get("job"));
							orderedResult.put("city", result.get("city"));

							return orderedResult;
						})
						.collect(Collectors.toList());
				Map<String, Long> occupationCounts = transformedData.stream()
						.map(entry -> entry.get("job"))
						.map(Object::toString)
						.collect(Collectors.groupingBy(
								occupation -> occupation,
								Collectors.counting()));
				List<Map<String, Object>> response = new ArrayList<>();
				response.add(Collections.singletonMap("transformedUsers", transformedData));
				response.add(Collections.singletonMap("occupation", occupationCounts));
				return ResponseEntity.ok(response);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}