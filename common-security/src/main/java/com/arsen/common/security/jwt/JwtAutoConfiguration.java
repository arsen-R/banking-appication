package com.arsen.common.security.jwt;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    @Bean
    public JwtTokenParser jwtTokenParser(JwtProperties properties) {
        return new JwtTokenParser(properties);
    }
}
