package com.springboot.betterreads_data_loader;

import com.datastax.oss.driver.api.core.CqlSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.springboot.betterreads_data_loader.model.author.Author;
import com.springboot.betterreads_data_loader.properties.DatastaxAstraProperties;
import com.springboot.betterreads_data_loader.repository.author.AuthorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.nio.file.Files.*;

@SpringBootApplication
@EnableConfigurationProperties(DatastaxAstraProperties.class)
public class BetterreadsDataLoaderApplication {

	@Autowired
	private AuthorRepository authorRepository;

	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {
		SpringApplication.run(BetterreadsDataLoaderApplication.class, args);
	}

//	@Bean
//	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DatastaxAstraProperties astraProperties) {
//		Path bundle = astraProperties.getSecureConnectBundle().toPath();
//		return builder -> builder.withCloudSecureConnectBundle(bundle);
//	}

	@Bean
	public CqlSession cqlSession() {
		return CqlSession.builder()
				.withCloudSecureConnectBundle(Paths.get("src/main/resources/secure-connect.zip"))
				.withAuthCredentials("client_id", "client_secret")
				.withKeyspace("main")  // Specify your keyspace here
				.build();
	}
	@PostConstruct
	public void run() {
		System.out.println("Application started");
		System.out.println(authorDumpLocation);
		initAuthors();

	}

	private void initAuthors() {
		Path path = Paths.get(authorDumpLocation);
		List<Author> authorList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				// Read and parse line
				String jsonString = line.substring(line.indexOf("{"));
				JsonNode jsonNode = null;
				try {
					jsonNode = objectMapper.readTree(jsonString);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
				// Construct Author Object
				Author author = new Author();
				author.setId(jsonNode.get("key").asText().replace("/authors/", ""));
				author.setName(jsonNode.get("name").asText());
				author.setPersonalName(jsonNode.get("name").asText());

				// Persist using repository
				System.out.println(author);
				authorList.add(author);

			});
		   authorRepository.saveAll(authorList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initWorks() {

	}
}
