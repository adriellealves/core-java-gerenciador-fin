package com.adrielle.corefinancas.security;

import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Pega o token do cabeçalho da requisição
        var token = this.recoverToken(request);
        
        if (token != null) {
            // 2. Valida o token e pega o ID do usuário (se o token for inválido, retorna vazio)
            var userId = tokenService.validateToken(token);
            
            if (!userId.isEmpty()) {
                // 3. Busca o usuário no banco de dados para confirmar que ele ainda existe
                User user = userRepository.findById(UUID.fromString(userId))
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                
                // 4. Diz ao Spring Security: "Pode deixar ele passar, eu garanto quem ele é!"
                var authentication = new UsernamePasswordAuthenticationToken(user, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // 5. Continua o fluxo normal da requisição (vai para o Controller)
        filterChain.doFilter(request, response);
    }

    // Função auxiliar para extrair o token do cabeçalho "Authorization: Bearer <token>"
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}