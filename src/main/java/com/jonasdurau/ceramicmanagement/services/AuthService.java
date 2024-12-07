package com.jonasdurau.ceramicmanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jonasdurau.ceramicmanagement.config.TenantContext;
import com.jonasdurau.ceramicmanagement.config.TokenService;
import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.repositories.CompanyRepository;

@Service
public class AuthService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String email, String password) {
        // Busca a empresa pelo email
        Company company = companyRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        // Verifica a senha (já deve estar criptografada)
        if (!passwordEncoder.matches(password, company.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        // Configura o TenantContext com a URL e porta do banco
        TenantContext.setCurrentTenant(company.getDatabaseUrl() + ":" + company.getDatabasePort());

        // Gera o token JWT contendo os dados do tenant
        return tokenService.generateToken(company);
    }
}