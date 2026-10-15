package com.devhive.DevHive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DevHiveApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
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
