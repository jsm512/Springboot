package com.example.jsondemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class JsondemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsondemoApplication.class, args);
	}

	@RestController
	public static class ApiController {

		@PostMapping("/merge-urls")
		public ResponseEntity<Object> mergeUrls(@RequestBody List<String> urls) {
			try {
				if (urls.size() != 2) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Collections.singletonMap("error", "Exactly two URLs must be provided"));
				}

				URI uri1 = new URI(urls.get(0));
				URI uri2 = new URI(urls.get(1));

				String baseUrl1 = uri1.getScheme() + "://" + uri1.getHost() + uri1.getPath();
				String baseUrl2 = uri2.getScheme() + "://" + uri2.getHost() + uri2.getPath();

				if (!baseUrl1.equals(baseUrl2)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Collections.singletonMap("error", "Base URLs do not match"));
				}

				Map<String, String> queryParams1 = getQueryParams(uri1.getQuery());
				Map<String, String> queryParams2 = getQueryParams(uri2.getQuery());

				if (queryParams1.isEmpty() && queryParams2.isEmpty()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(Collections.singletonMap("error", "Both URLs have no query parameters"));
				}

				Map<String, String> mergedParams = new LinkedHashMap<>(queryParams1);
				mergedParams.putAll(queryParams2);

				String mergedQuery = mergedParams.entrySet().stream()
						.map(entry -> entry.getKey() + "=" + entry.getValue())
						.collect(Collectors.joining("&"));

				String mergedUrl = baseUrl1 + "?" + mergedQuery;
				return ResponseEntity.ok(Collections.singletonMap("result", mergedUrl));

			} catch (URISyntaxException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Collections.singletonMap("error", "Invalid URL format"));
			}
		}

		private Map<String, String> getQueryParams(String query) {
			if (query == null || query.isEmpty()) {
				return Collections.emptyMap();
			}

			return Arrays.stream(query.split("&"))
					.map(param -> param.split("="))
					.collect(Collectors.toMap(
							param -> param[0],
							param -> param.length > 1 ? param[1] : "",
							(oldValue, newValue) -> newValue,
							LinkedHashMap::new));
		}
	}
}