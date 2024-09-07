package com.springboot.betterreads_data_loader;

import com.datastax.oss.driver.api.core.CqlSession;
import com.springboot.betterreads_data_loader.model.author.Author;
import com.springboot.betterreads_data_loader.properties.DatastaxAstraProperties;
import com.springboot.betterreads_data_loader.repository.author.AuthorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@SpringBootApplication
@EnableConfigurationProperties(DatastaxAstraProperties.class)
public class BetterreadsDataLoaderApplication {

	@Autowired
	private AuthorRepository authorRepository;

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
		Author author = new Author();
		author.setId("1");
		author.setName("Akshit");
		author.setPersonalName("Akshit");
		authorRepository.save(author);
	}

}
