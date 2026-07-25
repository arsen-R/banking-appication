package com.arsen.auth.config;

import com.arsen.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private static final String TOKEN = "token";

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    private JwtAuthenticationFilter filter;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final UserDetails userDetails = User.withUsername("john").password("secret").authorities("ROLE_USER").build();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesRequestWithValidToken() throws Exception {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenReturn("john");
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtService.validateToken(TOKEN, userDetails)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
        assertThat(authentication.getDetails()).isInstanceOf(WebAuthenticationDetails.class);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doesNotAuthenticateWhenHeaderIsMissing() throws Exception {
        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).extractUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doesNotAuthenticateWhenHeaderIsNotBearer() throws Exception {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).extractUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doesNotAuthenticateWhenTokenIsInvalid() throws Exception {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenReturn("john");
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtService.validateToken(TOKEN, userDetails)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void keepsExistingAuthentication() throws Exception {
        Authentication existing = new TestingAuthenticationToken("jane", "credentials");
        SecurityContextHolder.getContext().setAuthentication(existing);
        request.addHeader("Authorization", "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenReturn("john");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(existing);
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void continuesChainWhenTokenParsingFails() throws Exception {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenThrow(new IllegalArgumentException("malformed"));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void continuesChainWhenUserIsUnknown() throws Exception {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        when(jwtService.extractUsername(TOKEN)).thenReturn("john");
        when(userDetailsService.loadUserByUsername("john")).thenThrow(new UsernameNotFoundException("john"));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
