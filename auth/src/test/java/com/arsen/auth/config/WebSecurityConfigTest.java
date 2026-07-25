package com.arsen.auth.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebSecurityConfigTest {
    private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
    private WebSecurityConfig config;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        config = new WebSecurityConfig(userDetailsService);
        userDetails = User.withUsername("john")
                .password(new BCryptPasswordEncoder().encode("secret"))
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void passwordEncoderHashesPassword() {
        BCryptPasswordEncoder encoder = config.bCryptPasswordEncoder();

        String encoded = encoder.encode("secret");
        assertThat(encoded).isNotEqualTo("secret");
        assertThat(encoder.matches("secret", encoded)).isTrue();
        assertThat(encoder.matches("wrong", encoded)).isFalse();
    }

    @Test
    void authenticationProviderAuthenticatesValidCredentials() {
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        DaoAuthenticationProvider provider = config.daoAuthenticationProvider();

        Authentication authentication = provider.authenticate(
                new UsernamePasswordAuthenticationToken("john", "secret"));

        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getName()).isEqualTo("john");
    }

    @Test
    void authenticationProviderRejectsWrongPassword() {
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        DaoAuthenticationProvider provider = config.daoAuthenticationProvider();

        assertThatThrownBy(() -> provider.authenticate(
                new UsernamePasswordAuthenticationToken("john", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }
}
