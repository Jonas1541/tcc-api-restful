package com.jonasdurau.ceramicmanagement.dtos;

public class TokenResponseDTO {
    
    private String token;

    public TokenResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
