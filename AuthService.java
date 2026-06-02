package com.company.invoice.service;


import com.company.invoice.dto.AuthRequest;
import com.company.invoice.dto.RegisterRequest;
import com.company.invoice.model.User;

public interface AuthService {
	 String register(RegisterRequest request);

	    String login(AuthRequest request);
   
}
