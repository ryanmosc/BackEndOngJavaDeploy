package com.ong.api_backend.model.user;

public record RegisterDTO(String login, String password, UserRole role) {
}
