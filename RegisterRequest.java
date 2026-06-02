package com.company.invoice.dto;
import jakarta.validation.constraints.*;

public class RegisterRequest {

    @NotBlank(message = "Email is required") 
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 chars")
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password; 
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) { 
        this.password = password;
    }
}