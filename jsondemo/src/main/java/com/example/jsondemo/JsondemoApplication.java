package com.example.jsondemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class JsondemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsondemoApplication.class, args);
	}

	@RestController
	public static class ApiController {
		private final String DATA_DIR = "data/xml/users.xml";
		private final XmlMapper xmlMapper = new XmlMapper();

		@GetMapping("/api/users")
		public ResponseEntity<Object> getUsers(
				@RequestParam(value = "userName", required = false) String userName) {
			if (userName == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ErrorDTO("Invalid data format"));
			}
			try {
				ClassPathResource resource = new ClassPathResource(DATA_DIR);
				List<UserDTO> users = xmlMapper.readValue(
						resource.getInputStream(),
						new TypeReference<List<UserDTO>>() {

						});
				List<UserDTO> sortedUsers = users.stream()
						.filter(user -> userName.equals(user.getUsername()))
						.collect(Collectors.toList());
				if (sortedUsers.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body(new ErrorDTO("data not found"));
				}
				System.out.println(sortedUsers);
				return ResponseEntity.ok(sortedUsers);
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}