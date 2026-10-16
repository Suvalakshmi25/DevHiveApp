package com.devhive.DevHive;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.devhive.DevHive.entity.Organization;
import com.devhive.DevHive.respository.OrganizationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DevHiveApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrganizationRepository organizationRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void contextLoads() {
	}

	@Test
	void organizationApiAllowsFrontendCorsPreflight() throws Exception {
		mockMvc.perform(options("/api/orgs")
				.header("Origin", "http://localhost:4200")
				.header("Access-Control-Request-Method", "GET")
				.header("Access-Control-Request-Headers", "authorization"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
	}

	@Test
	@Transactional
	void organizationsCanBeSavedAndLoadedFromDatabase() {
		String organizationName = "persistence-test-" + UUID.randomUUID();
		Organization saved = organizationRepository.saveAndFlush(
				Organization.builder().name(organizationName).build());

		entityManager.clear();

		assertEquals(organizationName,
				organizationRepository.findById(saved.getId()).orElseThrow().getName());
	}

	@Test
	void organizationApiRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/api/orgs"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void organizationApiRejectsInvalidBearerTokens() throws Exception {
		mockMvc.perform(get("/api/orgs").header("Authorization", "Bearer invalid"))
				.andExpect(status().isUnauthorized());
	}
}
