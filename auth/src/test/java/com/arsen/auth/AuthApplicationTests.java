package com.arsen.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootTest
class AuthApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class TestUserDetailsConfiguration {

		@Bean
		UserDetailsService userDetailsService() {
			return new InMemoryUserDetailsManager(
					User.withUsername("john").password("{noop}secret").authorities("ROLE_USER").build());
		}
	}

}
