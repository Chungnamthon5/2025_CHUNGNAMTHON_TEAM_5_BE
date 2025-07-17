package com.chungnamthon.cheonon.Auth.Jwt;

import java.time.LocalDateTime;

public class TokenWithExpiry {
    private final String token;
    private final LocalDateTime expiry;

    public TokenWithExpiry(String token, LocalDateTime expiry) {
        this.token = token;
        this.expiry = expiry;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }
}