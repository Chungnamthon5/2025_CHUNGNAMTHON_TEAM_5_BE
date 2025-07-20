package com.chungnamthon.cheonon.auth.dto.response;// KakaoTokenResponse.java
import lombok.Data;

@Data
public class KakaoTokenResponse {
    private String token_type;
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private int refresh_token_expires_in;
}