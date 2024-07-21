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
import java.util.stream.Collectors;

@SpringBootApplication
public class JsondemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsondemoApplication.class, args);
	}

	@RestController
	public static class ApiController {
		private final String DATA_DIR = "data/input/pages.json";
		private final ObjectMapper objectMapper = new ObjectMapper();

		@GetMapping("/api/gamerecord/users")
		public ResponseEntity<List<UserDTO>> getUsers() {
			try {
				ClassPathResource resource = new ClassPathResource(DATA_DIR);
				List<User> users = objectMapper.readValue(
						resource.getInputStream(),
						new TypeReference<List<User>>() {

						});
				List<UserDTO> sortedUsers = users.stream()
						.map(user -> new UserDTO(user.getUsername(), user.getTag()))
						.sorted(Comparator.comparing(UserDTO::getUsername)
								.thenComparing(UserDTO::getTag))
						.collect(Collectors.toList());
				System.out.println("Response:" + sortedUsers);
				return ResponseEntity.ok(sortedUsers);
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}

		@GetMapping("/api/gamerecord/winrate")
		public ResponseEntity<Object> getWinrate(
				@RequestParam(value = "username", required = false) String username,
				@RequestParam(value = "tag", required = false) String tag) {

			if (username == null || tag == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new ErrorDTO("Invalid data format"));
			}
			try {
				ClassPathResource resource = new ClassPathResource(DATA_DIR);
				List<User> users = objectMapper.readValue(
						resource.getInputStream(),
						new TypeReference<List<User>>() {

						});
				Optional<User> userOpt = users.stream()
						.filter(user -> username.equals(user.getUsername()) && tag.equals(user.getTag()))
						.findFirst();
				if (userOpt.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body(new ErrorDTO("Data not found"));
				}
				User user = userOpt.get();
				int winCount = (int) user.getWin();
				int loseCount = (int) user.getLose();
				int totalGames = winCount + loseCount;
				int winRate = (int) ((winCount / (double) totalGames) * 100);

				WinrateDTO winrateDTO = new WinrateDTO(winRate);

				System.out.println("Response: " + winRate);
				return ResponseEntity.ok(winrateDTO);
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}